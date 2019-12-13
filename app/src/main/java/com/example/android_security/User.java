package com.example.android_security;

public class User {
    private String UID, email;

    public User(String UID, String email) {
        this.UID = UID;
        this.email = email;
    }

    public User() {

    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
