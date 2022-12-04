package com.example.bkzalo.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.util.ArrayList;

import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bkzalo.API.ListBoxMessageAPI;
import com.example.bkzalo.API.SetOfflineAPI;
import com.example.bkzalo.API.SetOnlineAPI;
import com.example.bkzalo.adapters.RecentConversionsAdapter;
import com.example.bkzalo.listeners.ConversionListener;
import com.example.bkzalo.models.BoxLastMessage;
import com.example.bkzalo.models.Chat;
import com.example.bkzalo.models.UserModel;
import com.example.bkzalo.databinding.ActivityMainBinding;
import com.example.bkzalo.utilities.Constants;
import com.example.bkzalo.utilities.PreferenceManager;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ConversionListener {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private List<Chat> conversations;
    private RecentConversionsAdapter conversationsAdapter;
    private  Timer timer;
    private TimerTask task ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        init();
        loadUserDetails();
        SetTrangThai();
        setListeners();
        Reload();
    }

    private void init() {
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversionsAdapter(conversations, this);
        binding.conversationsRecyclerView.setAdapter(conversationsAdapter);
    }
    private void Reload(){
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                listenConversations();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }
    private void setListeners() {
        binding.imageSignOut.setOnClickListener(v -> signOut());
        binding.fabNewChat.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
        binding.Listgroup.setOnClickListener(v ->startActivity(new Intent(getApplicationContext() , GroupListActivity.class)));
    }
    // load ảnh tên người dùng
    private void loadUserDetails(){
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = android.util.Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void listenConversations() {
        BoxLastMessage box1 = new BoxLastMessage();
        box1.setId_nguoigui(Long.parseLong(preferenceManager.getString(Constants.KEY_USER_ID)));
        ListBoxMessageAPI.listboxmessageAPI.ListBOX(box1).enqueue(new Callback<List<BoxLastMessage>>() {
            @Override
            public void onResponse(Call<List<BoxLastMessage>> call, Response<List<BoxLastMessage>> response) {
                List<BoxLastMessage> list = response.body();
                eventListener(list);
            }
            @Override
            public void onFailure(Call<List<BoxLastMessage>> call, Throwable t) {
                    showToast("Lỗi dữ liệu");
            }
        });
        BoxLastMessage box2 = new BoxLastMessage();
        box2.setId_nguoinhan(Long.parseLong(preferenceManager.getString(Constants.KEY_USER_ID)));
        ListBoxMessageAPI.listboxmessageAPI.ListBOX(box2).enqueue(new Callback<List<BoxLastMessage>>() {
            @Override
            public void onResponse(Call<List<BoxLastMessage>> call, Response<List<BoxLastMessage>> response) {
                List<BoxLastMessage> list = response.body();
                eventListener(list);
            }

            @Override
            public void onFailure(Call<List<BoxLastMessage>> call, Throwable t) {
                showToast("Lỗi dữ liệu");
            }
        });
    }

    private void  showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private final void eventListener(List<BoxLastMessage> list) {

        if (list != null) {
            for(BoxLastMessage i : list){
                if(CheckConversions(i)){
                Long senderId = i.getId_nguoigui();
                Long receiderId = i.getId_nguoinhan();
                Chat chat = new Chat();
                chat.setId_nguoigui(senderId);
                chat.setId_nguoinhan(receiderId);
                if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId.toString())){

                    chat.setConversionID(i.getId_nguoinhan().toString());
                    chat.setConversionName(i.getTenreceider());
                    chat.setConversionImage(i.getUrlreceider());
                }
                else{

                    chat.setConversionID(i.getId_nguoigui().toString());
                    chat.setConversionName(i.getTensender());
                    chat.setConversionImage(i.getUrlsender());
                }
                chat.setNoidung(i.getTinnhancuoi());
                chat.setThoigiantao(i.getThoigiantao());
                conversations.add(chat);
            }
                else{
                    for(int j = 0 ; j <conversations.size() ; j++){
                        Long senderid = i.getId_nguoigui();
                        Long receiderid = i.getId_nguoinhan();
                        if(conversations.get(j).getId_nguoigui().equals(senderid) && conversations.get(j).getId_nguoinhan().equals(receiderid)){
                            conversations.get(j).setNoidung(i.getTinnhancuoi());
                            conversations.get(j).setThoigiantao(i.getThoigiantao());
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations , (obj1 ,obj2) ->obj2.getThoigiantao().compareTo(obj1.getThoigiantao()));
            conversationsAdapter.notifyDataSetChanged();
            binding.conversationsRecyclerView.smoothScrollToPosition(0);
            binding.conversationsRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }
    private boolean CheckConversions(BoxLastMessage box){
        boolean add = true;
        for(int i = 0 ; i < conversations.size(); i++){
            if(conversations.get(i).getId_nguoigui().equals(box.getId_nguoigui()) && conversations.get(i).getId_nguoinhan().equals(box.getId_nguoinhan())){
                add = false;
                break;
            }
        }
        return  add ;
    }
    private void SetTrangThai() {
        UserModel us = new UserModel();
        us.setId(Long.parseLong(preferenceManager.getString(Constants.KEY_USER_ID)));
        SetOnlineAPI.setOnlineapi.SetOnl(us)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {

                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        showToast("Không thể cập nhật trạng thái");
                    }
                });
    }

    private void signOut() {
        showToast("Signing out...");
        UserModel us = new UserModel();
        us.setId(Long.parseLong(preferenceManager.getString(Constants.KEY_USER_ID)));
        SetOfflineAPI.setoffAPI.Setoff(us)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        preferenceManager.clear();
                        timer.cancel();
                        task.cancel();
                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        showToast("Thoát thất bại!");
                    }
                });
    }


    @Override
    public void onConversionClicked(UserModel usermodel) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USERMODEL, usermodel);
        startActivity(intent);
    }

}