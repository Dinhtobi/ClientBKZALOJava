package com.example.bkzalo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bkzalo.API.ListBoxMessageAPI;
import com.example.bkzalo.API.ListGroupsAPI;
import com.example.bkzalo.API.SetOfflineAPI;
import com.example.bkzalo.API.SetOnlineAPI;
import com.example.bkzalo.adapters.RecentConversionsAdapter;
import com.example.bkzalo.databinding.ActivityMainBinding;
import com.example.bkzalo.listeners.ConversionListener;
import com.example.bkzalo.models.BoxLastMessage;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.models.Group;
import com.example.bkzalo.models.UserModel;
import com.example.bkzalo.utilities.Constants;
import com.example.bkzalo.utilities.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ConversionListener {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private List<Message> conversations;
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
        binding.fabNewChat.setOnClickListener(v -> UserListClick());
        binding.Listgroup.setOnClickListener(v ->GroupListClick());
        binding.imageProfile.setOnClickListener(v ->{
            ProfileCLick();
        });
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
        box1.setId_sender(Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID)));
        box1.setType("All");

        ListBoxMessageAPI.listboxmessageAPI.ListBOX(box1).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                try {
                    String jsonString = responseBody.string();
                    Gson gson = new GsonBuilder().create();
                    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                    JsonElement messageElement = jsonObject.get("message");
                    List<BoxLastMessage> list;

                    if (messageElement != null && messageElement.isJsonArray()) {
                        JsonArray messagesArray = messageElement.getAsJsonArray();
                        Type messageListType = new TypeToken<List<BoxLastMessage>>(){}.getType();
                        list = gson.fromJson(messagesArray, messageListType);
                    } else {
                        list = new ArrayList<>(); // Hoặc giá trị mặc định tùy vào yêu cầu của bạn
                    }
                    if(list != null)   ConversionBox(list);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                    showToast("Lỗi dữ liệu");
            }
        });
        BoxLastMessage box2 = new BoxLastMessage();
        box2.setId_receiver(Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID)));
        box2.setType("All");
        ListBoxMessageAPI.listboxmessageAPI.ListBOX(box2).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                try {
                    String jsonString = responseBody.string();
                    Gson gson = new GsonBuilder().create();
                    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                    List<BoxLastMessage> list;
                    JsonElement messageElement = jsonObject.get("message");

                    if (messageElement != null && messageElement.isJsonArray()) {
                        JsonArray messagesArray = messageElement.getAsJsonArray();
                        Type messageListType = new TypeToken<List<BoxLastMessage>>(){}.getType();
                        list = gson.fromJson(messagesArray, messageListType);
                    } else {
                        list = new ArrayList<>(); // Hoặc giá trị mặc định tùy vào yêu cầu của bạn
                    }
                    if(list != null)   ConversionBox(list);

                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showToast("Lỗi dữ liệu");
            }
        });
        UserModel user = new UserModel();
        user.setId(Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID)));
        ListGroupsAPI.listGroupsApi.listgroup(user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                try {
                    String jsonString = responseBody.string();
                    Gson gson = new GsonBuilder().create();
                    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                    List<Group> list;
                    JsonElement messageElement = jsonObject.get("message");

                    if (messageElement != null && messageElement.isJsonArray()) {
                        JsonArray messagesArray = messageElement.getAsJsonArray();
                        Type messageListType = new TypeToken<List<Group>>(){}.getType();
                        list = gson.fromJson(messagesArray, messageListType);
                    } else {
                        list = new ArrayList<>(); // Hoặc giá trị mặc định tùy vào yêu cầu của bạn
                    }
                    for(Group i : list){
                        BoxLastMessage box3 = new BoxLastMessage();
                        box3.setId_groupchat(i.getId());
                        box3.setType("All");
                        ListBoxMessageAPI.listboxmessageAPI.ListBOX(box3).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                ResponseBody responseBody = response.body();
                                try {
                                    String jsonString = responseBody.string();
                                    Gson gson = new GsonBuilder().create();
                                    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                                    JsonArray messagesArray = jsonObject.getAsJsonArray("message");
                                    Type messageListType = new TypeToken<List<BoxLastMessage>>(){}.getType();
                                    List<BoxLastMessage> list = gson.fromJson(messagesArray, messageListType);
                                    if(list != null)   ConversionBox(list);

                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                showToast("Lỗi dữ liệu");
                            }
                        });
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void  showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private final void ConversionBox(List<BoxLastMessage> list) {

        if (list.size() != 0) {
            for(BoxLastMessage i : list){
                  if(i.getId_groupchat() != 0){
                          if(CheckConversions(i)){
                                int senderid = i.getId_sender();
                                int id_nhomchat = i.getId_groupchat();
                                Message chat = new Message();
                                chat.setId_group(id_nhomchat);
                                chat.setId_sender(senderid);
                                chat.setId_receiver(0);
                                chat.setConversionID(String.valueOf(id_nhomchat));
                                chat.setConversionName(i.getNamegroup());
                                chat.setConversionImage(i.getImage());
                                chat.setContent(i.getLastmessage());
                                chat.setCreateAt(i.getCreateAt());
                                conversations.add(chat);

                            }else{
                                for(int j = 0 ; j <conversations.size() ; j++){
                                    int senderid = i.getId_sender();
                            int id_nhomchat = i.getId_groupchat();
                            if(conversations.get(j).getId_sender()==senderid && conversations.get(j).getId_group()== id_nhomchat){
                                conversations.get(j).setContent(i.getLastmessage());
                                conversations.get(j).setCreateAt(i.getCreateAt());
                                conversations.get(j).setConversionName(i.getNamegroup());
                                break;
                            }
                        }
                    }
              }else{
                  if(CheckConversions(i)){
                      int senderId = i.getId_sender();
                      int receiderId = i.getId_receiver();
                      Message chat = new Message();
                      chat.setId_sender(senderId);
                      chat.setId_receiver(receiderId);
                      chat.setId_group(i.getId_groupchat());
                      if(preferenceManager.getString(Constants.KEY_USER_ID).equals(String.valueOf(senderId))){

                          chat.setConversionID(String.valueOf(i.getId_receiver()));
                          chat.setConversionName(i.getNamreceiver());
                          chat.setConversionImage(i.getUrlreceiver());
                      }
                      else{

                          chat.setConversionID(String.valueOf(i.getId_sender()));
                          chat.setConversionName(i.getNamesender());
                          chat.setConversionImage(i.getUrlsender());
                      }
                      chat.setContent(i.getLastmessage());
                      chat.setCreateAt(i.getCreateAt());
                      conversations.add(chat);
                  }
                  else{
                      for(int j = 0 ; j <conversations.size() ; j++){
                          int senderid = i.getId_sender();
                          int receiderid = i.getId_receiver();
                          if(conversations.get(j).getId_sender() == senderid && conversations.get(j).getId_receiver() == receiderid){
                              conversations.get(j).setContent(i.getLastmessage());
                              conversations.get(j).setCreateAt(i.getCreateAt());
                              break;
                          }
                      }
                  }
              }
            }
            Collections.sort(conversations , (obj1 ,obj2) ->obj2.getCreateAt().compareTo(obj1.getCreateAt()));
            conversationsAdapter.notifyDataSetChanged();
            binding.conversationsRecyclerView.smoothScrollToPosition(0);
            binding.conversationsRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }
    private boolean CheckConversions(BoxLastMessage box){
        boolean add = true;
        if(box.getId_groupchat() !=0L){
            for(int i = 0 ; i < conversations.size(); i++){
                if(conversations.get(i).getId_group() ==box.getId_groupchat()){
                    add = false;
                    break;
                }
            }
        }else{
            for(int i = 0 ; i < conversations.size(); i++){
                if(conversations.get(i).getId_sender()== box.getId_sender() && conversations.get(i).getId_receiver()==box.getId_receiver()){
                    add = false;
                    break;
                }
            }
        }
        return  add ;
    }
    private void SetTrangThai() {
        UserModel us = new UserModel();
        us.setId(Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID)));
        us.setStatus(1);
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
        us.setId(Integer.parseInt(preferenceManager.getString(Constants.KEY_USER_ID)));
        us.setStatus(0);
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
    public void onConversionUserClicked(UserModel usermodel) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USERMODEL, usermodel);
        startActivity(intent);
    }

    @Override
    public void onConversionGroupClicked(Group group) {
        Intent intent = new Intent(getApplicationContext(), GroupChatActivities.class);
        intent.putExtra(Constants.KEY_GROUP, group);
        startActivity(intent);
    }

    public void GroupListClick(){
        Intent intent = new Intent(getApplicationContext(), GroupListActivity.class);
        startActivity(intent);
    }
    public void UserListClick(){
        Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
        startActivity(intent);
    }
    public void ProfileCLick(){
        Intent intent = new Intent(getApplicationContext(), ProfileUserActivity.class);
        startActivity(intent);

    }
    @Override
    protected void onResume() {
        super.onResume();
        loadUserDetails();
        conversations.clear();
        listenConversations();
    }

}