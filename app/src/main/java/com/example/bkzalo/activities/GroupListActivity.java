package com.example.bkzalo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.bkzalo.API.ListGroupsAPI;
import com.example.bkzalo.adapters.GroupAdapter;
import com.example.bkzalo.databinding.ActivityGroupListBinding;
import com.example.bkzalo.databinding.ActivityUsersBinding;
import com.example.bkzalo.listeners.GroupsListener;
import com.example.bkzalo.models.DetailGroup;
import com.example.bkzalo.models.Group;
import com.example.bkzalo.models.UserModel;
import com.example.bkzalo.utilities.Constants;
import com.example.bkzalo.utilities.PreferenceManager;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupListActivity extends AppCompatActivity implements GroupsListener {
    private ActivityGroupListBinding binding;
    private PreferenceManager preferenceManager;
    GroupAdapter groupAdapter;
    List<Group> groupList = new ArrayList<>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        groupAdapter = new GroupAdapter(this::onGroupClicked);
        GetGroups();
    }

    private void GetGroups(){
        loading(true);
        UserModel user = new UserModel();
        user.setId(Long.parseLong(preferenceManager.getString(Constants.KEY_USER_ID)));

        ListGroupsAPI.listGroupsApi.listgroup(user).enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                List<Group> list = response.body();
                for(int i = 0 ; i< list.size(); i++){
                    Group group = new Group();
                    group.setId_nguoitao(list.get(i).getId_nguoitao());
                    group.setTennhom(list.get(i).getTennhom());
                    group.setId_nhomchat(list.get(i).getId_nhomchat());
                    group.setImage(list.get(i).getImage());
                    groupList.add(group);
                }
                if(groupList.size() > 0  ){
                    groupAdapter.setData(groupList);
                    binding.groupRecyclerView.setAdapter(groupAdapter);
                    binding.groupRecyclerView.setVisibility(View.VISIBLE);
                }
                else{
                    showErrorMessage();
                }
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
                showErrorMessage();
            }
        });
    }
    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "Không có nhóm nào cả"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onGroupClicked(Group group) {

    }
}