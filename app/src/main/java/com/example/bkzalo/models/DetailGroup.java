package com.example.bkzalo.models;

public class DetailGroup {
    private Long id_nhomchat ;
    private Long  id_nguoidung ;
    private String  thoigianthamgia  ;
    private String thoigianroikhoi ;
    private String type ;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
