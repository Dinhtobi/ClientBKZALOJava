package com.example.bkzalo.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BoxLastMessage implements Serializable {
    @SerializedName("id")
    private int id ;
    @SerializedName("id_sender")
    private int id_sender;
    @SerializedName("id_receiver")
    private int id_receiver;
    @SerializedName("lastmessage")
    private String lastmessage;
    @SerializedName("createAt")
    private String createAt;
    @SerializedName("namesender")
    private String namesender;
    @SerializedName("namreceiver")
    private String namreceiver;
    @SerializedName("urlsender")
    private String urlsender;
    @SerializedName("urlreceiver")
    private String urlreceiver;
    @SerializedName("id_groupchat")
    private int id_groupchat;
    @SerializedName("namegroup")
    private String namegroup ;
    @SerializedName("image")
    private String image;
    @SerializedName("type")
    private String Type;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_sender() {
        return id_sender;
    }

    public void setId_sender(int id_sender) {
        this.id_sender = id_sender;
    }

    public int getId_receiver() {
        return id_receiver;
    }

    public void setId_receiver(int id_receiver) {
        this.id_receiver = id_receiver;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getNamesender() {
        return namesender;
    }

    public void setNamesender(String namesender) {
        this.namesender = namesender;
    }

    public String getNamreceiver() {
        return namreceiver;
    }

    public void setNamreceiver(String namreceiver) {
        this.namreceiver = namreceiver;
    }

    public String getUrlsender() {
        return urlsender;
    }

    public void setUrlsender(String urlsender) {
        this.urlsender = urlsender;
    }

    public String getUrlreceiver() {
        return urlreceiver;
    }

    public void setUrlreceiver(String urlreceiver) {
        this.urlreceiver = urlreceiver;
    }

    public int getId_groupchat() {
        return id_groupchat;
    }

    public void setId_groupchat(int id_groupchat) {
        this.id_groupchat = id_groupchat;
    }

    public String getNamegroup() {
        return namegroup;
    }

    public void setNamegroup(String namegroup) {
        this.namegroup = namegroup;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
