package com.example.bkzalo.API;

import com.example.bkzalo.models.BoxLastMessage;
import com.example.bkzalo.utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ListBoxMessageAPI {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    ListBoxMessageAPI listboxmessageAPI = new Retrofit.Builder().baseUrl(Constants.KEY_API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(ListBoxMessageAPI.class);
    @POST("/api/get-boxchat")
    Call<ResponseBody> ListBOX(@Body BoxLastMessage boxLastMessage);
}
