package com.example.bkzalo.API;

import com.example.bkzalo.models.UserModel;
import com.example.bkzalo.utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GetUsersAPI {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    GetUsersAPI getuserapi = new Retrofit.Builder().baseUrl(Constants.KEY_API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(GetUsersAPI.class);
    @GET("/api/get-all-users")
    Call<ResponseBody> GetList(@Query("id") String id);
}
