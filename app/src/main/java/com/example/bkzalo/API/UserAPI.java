package com.example.bkzalo.API;

import com.example.bkzalo.models.UserModel;
import com.example.bkzalo.utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UserAPI {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        UserAPI userAPI = new Retrofit.Builder().baseUrl(Constants.KEY_API)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build().create(UserAPI.class);
    @POST("Test-web/api-admin-new")
    Call<UserModel>sendPost(@Body UserModel usermodel);
    @PUT("Test-web/api-admin-new")
    Call<UserModel> sendPut(@Body UserModel userModel);
}
