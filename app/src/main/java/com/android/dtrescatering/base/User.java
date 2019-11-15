package com.android.dtrescatering.base;

public class User {
    public String nama, email, password, rePassword, phone;

    public User() {
    }

    public User(String nama, String email, String password, String rePassword, String phone) {
        this.nama = nama;
        this.email = email;
        this.password = password;
        this.rePassword = rePassword;
        this.phone = phone;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
