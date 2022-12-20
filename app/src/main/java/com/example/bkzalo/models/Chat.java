package com.example.bkzalo.models;

import com.google.gson.annotations.SerializedName;


import java.io.Serializable;


public class Chat implements Serializable {
    @SerializedName("id_tinnhan")
    private Long id_tinnhan;
    @SerializedName("id_nguoigui")
    private Long id_nguoigui;
    @SerializedName("id_nguoinhan")
    private Long id_nguoinhan;
    @SerializedName("conversionid")
    private String ConversionID;
    @SerializedName("conversionname")
    private String ConversionName;
    @SerializedName("conversionimage")
    private String ConversionImage;
    @SerializedName("noidung")
    private String noidung ;
    @SerializedName("thoigiantao")
    private String thoigiantao ;
    @SerializedName("id_nhomchat")
    private Long id_nhomchat ;

    public String getNoidung() {
        return noidung;
    }

    public void setNoidung(String noidung) {
        this.noidung = noidung;
    }

    public String getThoigiantao() {
        return thoigiantao;
    }

    public void setThoigiantao(String thoigiantao) {
        this.thoigiantao = thoigiantao;
    }

    public Long getId_nhomchat() {
        return id_nhomchat;
    }

    public void setId_nhomchat(Long id_nhomchat) {
        this.id_nhomchat = id_nhomchat;
    }

    public Long getId_tinnhan() {
        return id_tinnhan;
    }

    public void setId_tinnhan(Long id_tinnhan) {
        this.id_tinnhan = id_tinnhan;
    }

    public Long getId_nguoigui() {
        return id_nguoigui;
    }

    public void setId_nguoigui(Long id_nguoigui) {
        this.id_nguoigui = id_nguoigui;
    }

    public Long getId_nguoinhan() {
        return id_nguoinhan;
    }

    public void setId_nguoinhan(Long id_nguoinhan) {
        this.id_nguoinhan = id_nguoinhan;
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
}
