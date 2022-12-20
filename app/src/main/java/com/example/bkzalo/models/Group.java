package com.example.bkzalo.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {
    @SerializedName("id_nguoitao")
    private Long id_nguoitao;
    @SerializedName("tennhom")
    private String tennhom ;
    @SerializedName("id_nhomchat")
    private Long id_nhomchat;
    @SerializedName("image")
    private String image;


    public Long getId_nguoitao() {
        return id_nguoitao;
    }

    public void setId_nguoitao(Long id_nguoitao) {
        this.id_nguoitao = id_nguoitao;
    }

    public String getTennhom() {
        return tennhom;
    }

    public void setTennhom(String tennhom) {
        this.tennhom = tennhom;
    }

    public Long getId_nhomchat() {
        return id_nhomchat;
    }

    public void setId_nhomchat(Long id_nhomchat) {
        this.id_nhomchat = id_nhomchat;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
