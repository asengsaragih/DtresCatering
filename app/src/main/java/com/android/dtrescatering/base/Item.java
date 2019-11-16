package com.android.dtrescatering.base;

import com.google.firebase.database.Exclude;

public class Item {
    private String nama, harga, key;

    public Item() {
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
}
