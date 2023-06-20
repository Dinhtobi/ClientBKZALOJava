package com.example.bkzalo.API;

import com.example.bkzalo.models.Group;
import com.example.bkzalo.utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface GroupAPI {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH-mm-ss").create();
    GroupAPI groupapi = new Retrofit.Builder().baseUrl(Constants.KEY_API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(GroupAPI.class);
    @POST("/api/create-groupchat")
    Call<ResponseBody> addGroup(@Body Group group);
    @PUT("/api/update-groupchat")
    Call<String> updateGroup(@Body Group group);
}
