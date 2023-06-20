package com.example.bkzalo.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.databinding.ItemContainerUserBinding;
import com.example.bkzalo.listeners.GroupsListener;
import com.example.bkzalo.models.Group;
import com.example.bkzalo.models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {
    private List<Group> groups;
    private GroupsListener groupsListener;
    public GroupAdapter(GroupsListener groupsListener){
        this.groupsListener = groupsListener ;
    }
    public void setData(List<Group> groups){
        this.groups = groups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new GroupAdapter.GroupViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        holder.setGroupData(groups.get(position));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder{

        ItemContainerUserBinding binding;

        GroupViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setGroupData(Group group) {
            binding.textName.setText(group.getNamegroup());
            binding.imageProfile.setImageBitmap(getUserImage(group.getImage()));
            binding.getRoot().setOnClickListener(v -> groupsListener.onGroupClicked(group));
        }
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = android.util.Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
