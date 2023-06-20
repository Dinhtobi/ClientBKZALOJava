package com.example.bkzalo.models;

import com.google.gson.annotations.SerializedName;


import java.io.Serializable;


public class Message implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("id_sender")
    private int id_sender;
    @SerializedName("id_receiver")
    private int id_receiver;
    @SerializedName("conversionid")
    private String ConversionID;
    @SerializedName("conversionname")
    private String ConversionName;
    @SerializedName("conversionimage")
    private String ConversionImage;
    @SerializedName("content")
    private String content ;
    @SerializedName("createAt")
    private String createAt ;
    @SerializedName("id_group")

    private int id_group ;
    @SerializedName("fileformat")
    private int fileformat;

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

    public String getConversionID() {
        return ConversionID;
    }

    public void setConversionID(String conversionID) {
        ConversionID = conversionID;
    }

    public String getConversionName() {
        return ConversionName;
    }

    public void setConversionName(String conversionName) {
        ConversionName = conversionName;
    }

    public String getConversionImage() {
        return ConversionImage;
    }

    public void setConversionImage(String conversionImage) {
        ConversionImage = conversionImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public int getId_group() {
        return id_group;
    }

    public void setId_group(int id_group) {
        this.id_group = id_group;
    }

    public int getFileformat() {
        return fileformat;
    }

    public void setFileformat(int fileformat) {
        this.fileformat = fileformat;
    }
}
