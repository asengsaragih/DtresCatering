package com.android.dtrescatering.base;

import com.google.firebase.database.Exclude;

public class Store {
    String nama;
    String deskripsi;
    String jamBuka, jamTutup;
    String key;

    public Store() {

    }

    public Store(String nama, String deskripsi, String jamBuka, String jamTutup) {
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.jamBuka = jamBuka;
        this.jamTutup = jamTutup;
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

    public String getJamBuka() {
        return jamBuka;
    }

    public void setJamBuka(String jamBuka) {
        this.jamBuka = jamBuka;
    }

    public String getJamTutup() {
        return jamTutup;
    }

    public void setJamTutup(String jamTutup) {
        this.jamTutup = jamTutup;
    }
}
