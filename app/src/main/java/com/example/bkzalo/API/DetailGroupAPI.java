package com.example.bkzalo.API;

import com.example.bkzalo.models.DetailGroup;
import com.example.bkzalo.utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface DetailGroupAPI {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH-mm-ss").create();
    DetailGroupAPI detailgroupapi = new Retrofit.Builder().baseUrl(Constants.KEY_API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(DetailGroupAPI.class);
    @POST("/api/create-detailgroupchat")
    Call<DetailGroup> adduseringroup(@Body DetailGroup detailGroup );
    @PUT("/api/update-detailgroupchat")
    Call<String>  updateuseringroup(@Body DetailGroup detailGroup);
}
