package com.example.bkzalo.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BoxLastMessage implements Serializable {
    @SerializedName("id_hopchat")
    private Long id_hopchat ;
    @SerializedName("id_nguoigui")
    private Long id_nguoigui;
    @SerializedName("id_nguoinhan")
    private Long id_nguoinhan;
    @SerializedName("tinnhancuoi")
    private String tinnhancuoi;
    @SerializedName("thoigiantao")
    private String thoigiantao;
    @SerializedName("tensender")
    private String tensender;
    @SerializedName("tenreceider")
    private String tenreceider;
    @SerializedName("urlsender")
    private String urlsender;
    @SerializedName("urlreceider")
    private String urlreceider;
    @SerializedName("id_nhomchat")
    private Long id_nhomchat;
    @SerializedName("tennhom")
    private String tennhom ;
    @SerializedName("image")
    private String image;

    public String getTennhom() {
        return tennhom;
    }

    public void setTennhom(String tennhom) {
        this.tennhom = tennhom;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getId_nhomchat() {
        return id_nhomchat;
    }

    public void setId_nhomchat(Long id_nhomchat) {
        this.id_nhomchat = id_nhomchat;
    }

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId_hopchat() {
        return id_hopchat;
    }

    public void setId_hopchat(Long id_hopchat) {
        this.id_hopchat = id_hopchat;
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

    public String getTinnhancuoi() {
        return tinnhancuoi;
    }

    public void setTinnhancuoi(String tinnhancuoi) {
        this.tinnhancuoi = tinnhancuoi;
    }

    public String getThoigiantao() {
        return thoigiantao;
    }

    public void setThoigiantao(String thoigiantao) {
        this.thoigiantao = thoigiantao;
    }

    public String getTensender() {
        return tensender;
    }

    public void setTensender(String tensender) {
        this.tensender = tensender;
    }

    public String getTenreceider() {
        return tenreceider;
    }

    public void setTenreceider(String tenreceider) {
        this.tenreceider = tenreceider;
    }

    public String getUrlsender() {
        return urlsender;
    }

    public void setUrlsender(String urlsender) {
        this.urlsender = urlsender;
    }

    public String getUrlreceider() {
        return urlreceider;
    }

    public void setUrlreceider(String urlreceider) {
        this.urlreceider = urlreceider;
    }
}
