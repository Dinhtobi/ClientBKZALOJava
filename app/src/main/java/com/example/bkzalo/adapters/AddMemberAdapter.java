package com.example.bkzalo.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.databinding.ItemContainerUsergroupBinding;
import com.example.bkzalo.listeners.CheckAddListener;
import com.example.bkzalo.models.UserModel;

import java.util.List;

public class AddMemberAdapter extends RecyclerView.Adapter<AddMemberAdapter.AddGroupViewHolder> {

    private List<UserModel> users;
    private final CheckAddListener checkAddListener;
    private List<UserModel> checkeds;
    public AddMemberAdapter(CheckAddListener checkAddListener) {
        this.checkAddListener = checkAddListener;
    }
    public void setData(List<UserModel> userResponseList) {
        this.users = userResponseList;
        notifyDataSetChanged();
    }
    public List<UserModel> GetData() {
        return this.users;
    }
    @NonNull
    @Override
    public AddGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUsergroupBinding itemContainerUsergroupBinding = ItemContainerUsergroupBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AddMemberAdapter.AddGroupViewHolder(itemContainerUsergroupBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddGroupViewHolder holder, int position) {
        holder.setGroupData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class AddGroupViewHolder extends RecyclerView.ViewHolder{

        ItemContainerUsergroupBinding binding;

        AddGroupViewHolder(ItemContainerUsergroupBinding itemContainerUsergroupBinding) {
            super(itemContainerUsergroupBinding.getRoot());
            binding = itemContainerUsergroupBinding;
        }

        void setGroupData(UserModel userModel) {
            binding.textName.setText(userModel.getName());
            binding.imageProfile.setImageBitmap(getUserImage(
                    userModel.getUrl()
            ));
            binding.checkman.setOnClickListener(v->checkAddListener.onCheckClick(userModel.getId()));
        }

    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = android.util.Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
