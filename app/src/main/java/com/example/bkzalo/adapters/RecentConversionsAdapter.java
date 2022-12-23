package com.example.bkzalo.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.databinding.ItemContainerRecentConversionBinding;
import com.example.bkzalo.listeners.ConversionListener;
import com.example.bkzalo.models.Chat;
import com.example.bkzalo.models.Group;
import com.example.bkzalo.models.UserModel;

import java.util.List;

public class RecentConversionsAdapter extends RecyclerView.Adapter<RecentConversionsAdapter.ConversionViewHolder>{

    private final List<Chat> chatMessages;
    private final ConversionListener conversionListener;

    public RecentConversionsAdapter(List<Chat> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {
        ItemContainerRecentConversionBinding binding;

        ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding) {
            super(itemContainerRecentConversionBinding.getRoot());
            binding = itemContainerRecentConversionBinding;
        }

        void setData(Chat chatMessage) {
            binding.imageProfile.setImageBitmap(getConversionImage(chatMessage.getConversionImage()));
            binding.textName.setText(chatMessage.getConversionName());
            binding.textRecentMessage.setText(chatMessage.getNoidung());
            if(chatMessage.getId_nhomchat() != 0L){
                binding.getRoot().setOnClickListener(v -> {
                    Group group = new Group();
                    group.setId_nhomchat(chatMessage.getId_nhomchat());
                    group.setTennhom(chatMessage.getConversionName());
                    group.setImage(chatMessage.getConversionImage());
                    group.setType("User");
                    conversionListener.onConversionGroupClicked(group);
                });
            }else{
                binding.getRoot().setOnClickListener(v -> {
                    UserModel usermodel = new UserModel();
                    usermodel.setId(Long.parseLong(chatMessage.getConversionID()));
                    usermodel.setTen(chatMessage.getConversionName());
                    usermodel.setUrl(chatMessage.getConversionImage());
                    conversionListener.onConversionUserClicked(usermodel);
                });
            }
        }
    }

    private Bitmap getConversionImage(String encodedImage) {
        byte[] bytes = android.util.Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
