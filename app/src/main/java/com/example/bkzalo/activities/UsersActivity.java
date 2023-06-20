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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
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
        GetUsersAPI.getuserapi.GetList("All").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loading(false);
                ResponseBody responseBody = response.body();
                try{
                    String jsonString = responseBody.string();
                    Gson gson = new GsonBuilder().create();
                    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                    JsonArray usersArray = jsonObject.getAsJsonArray("users");
                    Type userListType = new TypeToken<List<UserModel>>(){}.getType();
                    List<UserModel> userList = gson.fromJson(usersArray, userListType);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(response.body() != null){
                        for(int i = 0; i< userList.size(); i++) {
                            if (!currentUserId.equals(String.valueOf(userList.get(i).getId()))) {
                                UserModel user = new UserModel();
                                user.setName(userList.get(i).getName());
                                user.setEmail(userList.get(i).getEmail());
                                user.setUrl(userList.get(i).getUrl());
                                user.setStatus(userList.get(i).getStatus());
                                user.setId(userList.get(i).getId());
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
                }catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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