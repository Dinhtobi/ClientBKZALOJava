package com.example.bkzalo.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.databinding.ItemContainerReceivedMessageBinding;
import com.example.bkzalo.databinding.ItemContainerSentMessageBinding;
import com.example.bkzalo.databinding.LayoutImageReceivedBinding;
import com.example.bkzalo.databinding.LayoutImageSentBinding;
import com.example.bkzalo.models.Message;

import java.util.List;

public class ChatAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<Message> chatMessages;
    private final String senderId;
    private final Bitmap receiverProfileImage;

    public static  final int VIEW_TYPE_SENTMES = 1;
    public static final int VIEW_TYPE_RECEIVEDMES = 2;
    public static  final int VIEW_TYPE_SENTIMAGE = 3;
    public static final int VIEW_TYPE_RECEIVEDIMAGE = 4;
    public ChatAdapter(List<Message> chatMessages, String senderId, Bitmap receiverProfileImage) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
        this.receiverProfileImage = receiverProfileImage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENTMES) {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else if(viewType == VIEW_TYPE_RECEIVEDMES) {
            return new ReceivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else if(viewType == VIEW_TYPE_SENTIMAGE){
            return  new SentImageViewHolder(
                    LayoutImageSentBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false));
        }else{
            return  new ReceivedImageViewHolder(
                    LayoutImageReceivedBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENTMES) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        }else if(getItemViewType(position) == VIEW_TYPE_RECEIVEDMES) {
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position), receiverProfileImage);
        }else if(getItemViewType(position) == VIEW_TYPE_SENTIMAGE){
            ((SentImageViewHolder) holder).setData(chatMessages.get(position));
        }else{
            ((ReceivedImageViewHolder) holder).setData(chatMessages.get(position), receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Long id = chatMessages.get(position).getId_nguoigui();
        String sender = id.toString();
        int fileformat = chatMessages.get(position).getFileformat();
        if (sender.equals(senderId)) {
            if(fileformat == 1){
                return VIEW_TYPE_SENTIMAGE;
            }else return VIEW_TYPE_SENTMES;
        }else {
            if(fileformat == 1) return VIEW_TYPE_RECEIVEDIMAGE;
            else return VIEW_TYPE_RECEIVEDMES;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(Message chatMessage) {
            binding.textMessage.setText(chatMessage.getNoidung());
            binding.textDateTime.setText(chatMessage.getThoigiantao());
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void  setData(Message chatMessage, Bitmap receiverProfileImage) {
            binding.textMessage.setText(chatMessage.getNoidung());
            binding.textDateTime.setText(chatMessage.getThoigiantao().toString());
            binding.imageProfile.setImageBitmap(receiverProfileImage);
        }
    }
    static class SentImageViewHolder extends RecyclerView.ViewHolder {

        private final LayoutImageSentBinding binding;

        SentImageViewHolder(LayoutImageSentBinding layoutImageSentBinding) {
            super(layoutImageSentBinding.getRoot());
            binding = layoutImageSentBinding;
        }

        void setData(Message chatMessage) {
            byte[] bytes = android.util.Base64.decode(chatMessage.getNoidung(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.imgPreview.setImageBitmap(bitmap);
            binding.textDateTime.setText(chatMessage.getThoigiantao());
        }
    }
    static class ReceivedImageViewHolder extends RecyclerView.ViewHolder {

        private final LayoutImageReceivedBinding binding;

        ReceivedImageViewHolder(LayoutImageReceivedBinding layoutImageReceivedBinding) {
            super(layoutImageReceivedBinding.getRoot());
            binding = layoutImageReceivedBinding;
        }

        void  setData(Message chatMessage, Bitmap receiverProfileImage) {
            byte[] bytes = android.util.Base64.decode(chatMessage.getNoidung(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.imgPreview.setImageBitmap(bitmap);
            binding.textDateTime.setText(chatMessage.getThoigiantao().toString());
            binding.imageProfile.setImageBitmap(receiverProfileImage);

        }
    }
}
