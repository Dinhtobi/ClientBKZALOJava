package com.example.bkzalo.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DetailGroup implements Serializable {
    @SerializedName("id")
    private int id ;
    @SerializedName("id_groupchat")
    private int id_groupchat ;
    @SerializedName("id_user")
    private int  id_user ;
    @SerializedName("timejoin")
    private String  timejoin  ;
    @SerializedName("timeout")
    private String timeout ;
    @SerializedName("status")
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_groupchat() {
        return id_groupchat;
    }

    public void setId_groupchat(int id_groupchat) {
        this.id_groupchat = id_groupchat;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getTimejoin() {
        return timejoin;
    }

    public void setTimejoin(String timejoin) {
        this.timejoin = timejoin;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
