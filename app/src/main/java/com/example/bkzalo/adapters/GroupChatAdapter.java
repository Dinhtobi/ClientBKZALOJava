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
import com.example.bkzalo.models.UserModel;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Message> chatMessages;
    private final String senderId;
    private  List<UserModel> usersreceived;

    public static  final int VIEW_TYPE_SENTMES = 1;
    public static final int VIEW_TYPE_RECEIVEDMES = 2;
    public static  final int VIEW_TYPE_SENTIMAGE = 3;
    public static final int VIEW_TYPE_RECEIVEDIMAGE = 4;

    public GroupChatAdapter(List<Message> chatMessages, String senderId) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
    }
    public void setData( List<UserModel> listusers){
        this.usersreceived = listusers;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENTMES) {
            return new ChatAdapter.SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else if(viewType == VIEW_TYPE_RECEIVEDMES) {
            return new ChatAdapter.ReceivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else if(viewType == VIEW_TYPE_SENTIMAGE){
            return  new ChatAdapter.SentImageViewHolder(
                    LayoutImageSentBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false));
        }else{
            return  new ChatAdapter.ReceivedImageViewHolder(
                    LayoutImageReceivedBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == VIEW_TYPE_SENTMES) {
            ((ChatAdapter.SentMessageViewHolder) holder).setData(chatMessages.get(position));
        }else if(getItemViewType(position) == VIEW_TYPE_RECEIVEDMES) {
            String image= getImageUser(position);
            ((ChatAdapter.ReceivedMessageViewHolder) holder).setData(chatMessages.get(position), getBitmapFromEncodedString(image));
        }else if(getItemViewType(position) == VIEW_TYPE_SENTIMAGE){
            ((ChatAdapter.SentImageViewHolder) holder).setData(chatMessages.get(position));
        }else{
            String image= getImageUser(position);
            ((ChatAdapter.ReceivedImageViewHolder) holder).setData(chatMessages.get(position), getBitmapFromEncodedString(image));
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
    public String getImageUser(int position){
        Long id = chatMessages.get(position).getId_nguoigui();
        String image = "";
        for(int i = 0 ; i< usersreceived.size(); i++){
            if(usersreceived.get(i).getId().equals(id)){
                image = usersreceived.get(i).getUrl();
                break;
            }
        }
        return  image;
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
            binding.textDateTime.setText(chatMessage.getThoigiantao());
            binding.imageProfile.setImageBitmap(receiverProfileImage);
        }
    }
    private Bitmap getBitmapFromEncodedString (String encodedImage) {
        byte[] bytes = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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
