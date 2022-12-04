package com.example.bkzalo.API;

import com.example.bkzalo.models.BoxLastMessage;
import com.example.bkzalo.utilities.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface BoxMessageAPI {
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    BoxMessageAPI boxmessageAPI = new Retrofit.Builder().baseUrl(Constants.KEY_API)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(BoxMessageAPI.class);
    @POST("Test-web/api-admin-boxmessage")
    Call<BoxLastMessage> converBox(@Body BoxLastMessage boxLastMessage);
    @PUT("Test-web/api-admin-boxmessage")
    Call<BoxLastMessage> Update(@Body BoxLastMessage boxLastMessage);
}
