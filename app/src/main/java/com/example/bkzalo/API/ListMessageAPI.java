package com.example.bkzalo.API;

import com.example.bkzalo.models.Chat;
import com.example.bkzalo.utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ListMessageAPI {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH-mm-ss").create();
    ListMessageAPI listmessageapi = new Retrofit.Builder().baseUrl(Constants.KEY_API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(ListMessageAPI.class);
    @POST("Test-web/api-admin-listmessage")
    Call<List<Chat>> ListMes(@Body Chat chat);
}
