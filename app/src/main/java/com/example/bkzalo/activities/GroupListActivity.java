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


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
        user.setId(Long.parseLong(preferenceManager.getString(Constants.KEY_USER_ID)));

        ListGroupsAPI.listGroupsApi.listgroup(user).enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                List<Group> list = response.body();
                GetListGroup(list);
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                showErrorMessage();
            }
        });
    }
    private void GetListGroup(List<Group> list){
        if(list!=null){
            for(int i = 0 ; i< list.size(); i++){
                Group group = new Group();
                group.setId_nguoitao(list.get(i).getId_nguoitao());
                group.setTennhom(list.get(i).getTennhom());
                group.setId_nhomchat(list.get(i).getId_nhomchat());
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
        binding.imageadd.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),AddGroup.class)));
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
            if(groupList.get(i).getId_nhomchat().equals(group.getId_nhomchat())){
                add = false ;
                break;
            }
        }
        return add;
    }
}