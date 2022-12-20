package com.example.bkzalo.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DetailGroup implements Serializable {
    @SerializedName("id_nhomchat")
    private Long id_nhomchat ;
    @SerializedName("id_nguoidung")
    private Long  id_nguoidung ;
    @SerializedName("thoigianthamgia")
    private String  thoigianthamgia  ;
    @SerializedName("thoigianroikhoi")
    private String thoigianroikhoi ;


    public Long getId_nhomchat() {
        return id_nhomchat;
    }

    public void setId_nhomchat(Long id_nhomchat) {
        this.id_nhomchat = id_nhomchat;
    }

    public Long getId_nguoidung() {
        return id_nguoidung;
    }

    public void setId_nguoidung(Long id_nguoidung) {
        this.id_nguoidung = id_nguoidung;
    }

    public String getThoigianthamgia() {
        return thoigianthamgia;
    }

    public void setThoigianthamgia(String thoigianthamgia) {
        this.thoigianthamgia = thoigianthamgia;
    }

    public String getThoigianroikhoi() {
        return thoigianroikhoi;
    }

    public void setThoigianroikhoi(String thoigianroikhoi) {
        this.thoigianroikhoi = thoigianroikhoi;
    }

}
