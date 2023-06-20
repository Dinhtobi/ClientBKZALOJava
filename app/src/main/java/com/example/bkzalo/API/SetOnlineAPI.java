package com.example.bkzalo.API;

import com.example.bkzalo.models.UserModel;
import com.example.bkzalo.utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SetOnlineAPI {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    SetOnlineAPI setOnlineapi = new Retrofit.Builder().baseUrl(Constants.KEY_API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(SetOnlineAPI.class);
    @POST("/api/set-status-user")
    Call<UserModel> SetOnl(@Body UserModel userModel);

}
