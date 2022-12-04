package com.example.bkzalo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bkzalo.API.GetUsersAPI;
import com.example.bkzalo.adapters.UsersAdapter;
import com.example.bkzalo.databinding.ActivityUsersBinding;
import com.example.bkzalo.listeners.UserListener;
import com.example.bkzalo.models.UserModel;
import com.example.bkzalo.utilities.Constants;
import com.example.bkzalo.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersActivity extends AppCompatActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
    UsersAdapter usersAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        usersAdapter = new UsersAdapter(this::onUserClicked);
        getUsers();

    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
    private List<UserModel> users = new ArrayList<>();
    private void getUsers() {
        loading(true);

        GetUsersAPI.getuserapi.GetList().enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                loading(false);
                List<UserModel> listus = response.body();
                String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                if(response.body() != null){
                    for(int i = 0; i< listus.size(); i++) {
                        if (!currentUserId.equals(listus.get(i).getId().toString())) {
                            UserModel user = new UserModel();
                            user.setTen(listus.get(i).getTen());
                            user.setEmail(listus.get(i).getEmail());
                            user.setUrl(listus.get(i).getUrl());
                            user.setTrangthai(listus.get(i).getTrangthai());
                            user.setId(listus.get(i).getId());
                            users.add(user);
                        }

                    }
                    if(users.size() > 0 ){
                        usersAdapter.setData(users);
                        binding.usersRecyclerView.setAdapter(usersAdapter);
                        binding.usersRecyclerView.setVisibility(View.VISIBLE);
                    }
                    else{
                        showErrorMessage();
                    }
                }
                else{
                    showErrorMessage();
                }
            }

            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {

            }

        });
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "Không có người nào cả"));
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
    public void onUserClicked(UserModel UserModel) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USERMODEL,  UserModel);
        startActivity(intent);
        finish();
    }
}