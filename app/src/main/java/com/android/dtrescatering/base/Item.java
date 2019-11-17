package com.android.dtrescatering.base;

import com.google.firebase.database.Exclude;

public class Item {
    private String nama;
    private String harga;
    private String gambar;
    private String key;

    public Item() {
    }

    public Item(String nama, String harga, String gambar) {
        this.nama = nama;
        this.harga = harga;
        this.gambar = gambar;
    }

    public Item(String nama, String harga) {
        this.nama = nama;
        this.harga = harga;
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

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
