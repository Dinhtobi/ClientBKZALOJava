package com.example.bkzalo.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {
    @SerializedName("id_createdbyuser")
    private int id_createdbyuser;
    @SerializedName("namegroup")
    private String namegroup ;
    @SerializedName("id")
    private int id;
    @SerializedName("image")
    private String image;
    @SerializedName("type")
    private String type;

    public int getId_createdbyuser() {
        return id_createdbyuser;
    }

    public void setId_createdbyuser(int id_createdbyuser) {
        this.id_createdbyuser = id_createdbyuser;
    }

    public String getNamegroup() {
        return namegroup;
    }

    public void setNamegroup(String namegroup) {
        this.namegroup = namegroup;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
