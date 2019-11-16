package com.android.dtrescatering.base;

import com.google.firebase.database.Exclude;

public class Store {
    String nama, deskripsi, gambar, key;

    public Store() {
    }

    public Store(String nama, String deskripsi, String gambar) {
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.gambar = gambar;
    }

    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
