package com.example.bkzalo.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.bkzalo.R;
import com.example.bkzalo.listeners.BottomsheetListener;
import com.example.bkzalo.models.Group;
import com.example.bkzalo.models.GroupItem;
import com.example.bkzalo.models.UserModel;
import com.example.bkzalo.utilities.Constants;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

public class ExpandableListViewAdapter  extends BaseExpandableListAdapter {
    private List<GroupItem> listgroup ;
    private Map<GroupItem , List<UserModel>> listuser ;
    private BottomsheetListener listener;
    private UserModel isme ;
    public ExpandableListViewAdapter( BottomsheetListener listener) {

        this.listener= listener;
    }
    public void setData(List<GroupItem> listgroup, Map<GroupItem, List<UserModel>> listuser ,UserModel us){
        this.listgroup = listgroup;
        this.listuser = listuser;
        this.isme = us;
    }
    @Override
    public int getGroupCount() {
        if(listgroup != null){
            return  listgroup.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(listgroup != null && listuser != null){
            return listuser.get(listgroup.get(groupPosition)).size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listgroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listuser.get(listgroup.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        GroupItem group = listgroup.get(groupPosition);
        return group.getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        UserModel us = listuser.get(listgroup.get(groupPosition)).get(childPosition);
        return us.getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_group,parent,false);

        }
        TextView tvgroup =convertView.findViewById(R.id.group_user);
        GroupItem group = listgroup.get(groupPosition);
        tvgroup.setText(group.getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item,parent,false);

        }
        TextView tvuser =convertView.findViewById(R.id.textName);
        RoundedImageView imageView = convertView.findViewById(R.id.imageProfile);
        UserModel us = listuser.get(listgroup.get(groupPosition)).get(childPosition);
        if(us.getId() == isme.getId()){
            tvuser.setText("Bạn");
        }else{
            tvuser.setText(us.getName());
        }
        byte[] bytes = android.util.Base64.decode(us.getUrl(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        tvuser.setOnClickListener(v->listener.BottomsheetClick(us));
        imageView.setImageBitmap(bitmap);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
