package com.example.bkzalo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.bkzalo.R;
import com.example.bkzalo.models.Group;
import com.example.bkzalo.models.UserModel;

import java.util.List;
import java.util.Map;

public class ExpandableListViewAdapter  extends BaseExpandableListAdapter {
    private List<Group> listgroup ;
    private Map<Group , List<UserModel>> listuser ;

    public ExpandableListViewAdapter(List<Group> listgroup, Map<Group, List<UserModel>> listuser) {
        this.listgroup = listgroup;
        this.listuser = listuser;
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
        Group group = listgroup.get(groupPosition);
        return group.getId_nhomchat();
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
        Group group = listgroup.get(groupPosition);
        tvgroup.setText(group.getTennhom());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_usergroup,parent,false);

        }
        TextView tvuser =convertView.findViewById(R.id.textName);
        UserModel us = listuser.get(listgroup.get(groupPosition)).get(childPosition);
        tvuser.setText(us.getTen());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
