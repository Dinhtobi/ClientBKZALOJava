package com.example.bkzalo.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.databinding.ItemContainerReceivedMessageBinding;
import com.example.bkzalo.databinding.ItemContainerSentMessageBinding;
import com.example.bkzalo.models.Chat;
import com.example.bkzalo.models.Group;
import com.example.bkzalo.models.UserModel;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Chat> chatMessages;
    private final String senderId;
    private  List<UserModel> usersreceived;

    public static  final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public GroupChatAdapter(List<Chat> chatMessages, String senderId) {
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
        if (viewType == VIEW_TYPE_SENT) {
            return new ChatAdapter.SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else {
            return new ChatAdapter.ReceivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((ChatAdapter.SentMessageViewHolder) holder).setData(chatMessages.get(position));
        }else {
            String image= getImageUser(position);
            ((ChatAdapter.ReceivedMessageViewHolder) holder).setData(chatMessages.get(position), getBitmapFromEncodedString(image));
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
        if (sender.equals(senderId)) {
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVED;
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

        void setData(Chat chatMessage) {
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

        void  setData(Chat chatMessage, Bitmap receiverProfileImage) {
            binding.textMessage.setText(chatMessage.getNoidung());
            binding.textDateTime.setText(chatMessage.getThoigiantao().toString());
            binding.imageProfile.setImageBitmap(receiverProfileImage);
        }
    }
    private Bitmap getBitmapFromEncodedString (String encodedImage) {
        byte[] bytes = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
