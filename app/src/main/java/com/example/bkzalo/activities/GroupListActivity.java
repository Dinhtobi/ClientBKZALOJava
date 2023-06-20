package com.example.bkzalo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bkzalo.API.ListGroupsAPI;
import com.example.bkzalo.adapters.GroupAdapter;
import com.example.bkzalo.databinding.ActivityGroupListBinding;
import com.example.bkzalo.listeners.GroupsListener;
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


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupListActivity extends AppCompatActivity implements GroupsListener {
    private ActivityGroupListBinding binding;
    private PreferenceManager preferenceManager;
    GroupAdapter groupAdapter;
    List<Group> groupList ;
    private  Timer timer;
    private TimerTask task ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        groupList = new ArrayList<>() ;
        groupAdapter = new GroupAdapter(this::onGroupClicked);
        Reload();
    }
    private void Reload(){
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
               GetGroups();
               System.out.println(preferenceManager.getString(Constants.KEY_USER_ID));
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }
    private void GetGroups(){
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
                    GetListGroup(list);
                }catch (IOException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showErrorMessage();
            }
        });
    }
    private void GetListGroup(List<Group> list){
        if(list!=null){
            for(int i = 0 ; i< list.size(); i++){
                Group group = new Group();
                group.setId_createdbyuser(list.get(i).getId_createdbyuser());
                group.setNamegroup(list.get(i).getNamegroup());
                group.setId(list.get(i).getId());
                group.setImage(list.get(i).getImage());
                group.setType("User");
                if(CheckGroup(group)){
                    groupList.add(group);
                }
            }
            if(groupList.size() > 0  ){
                groupAdapter.setData(groupList);
                binding.groupRecyclerView.setAdapter(groupAdapter);
                binding.groupRecyclerView.smoothScrollToPosition(0);
                binding.groupRecyclerView.setVisibility(View.VISIBLE);
            }
            else{
                showErrorMessage();
            }
        }
    }
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.imageadd.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddGroupActivity.class)));
    }
    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "Không có nhóm nào cả"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }
    @Override
    public void onGroupClicked(Group group) {
        Intent intent = new Intent(getApplicationContext(), GroupChatActivities.class);
        intent.putExtra(Constants.KEY_GROUP,  group);
        timer.cancel();
        task.cancel();
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            timer.cancel();
            task.cancel();
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }
    private boolean CheckGroup(Group group){
        boolean add = true ;
        for (int i = 0 ; i < groupList.size() ; i++){
            if(groupList.get(i).getId()== group.getId()){
                add = false ;
                break;
            }
        }
        return add;
    }
}