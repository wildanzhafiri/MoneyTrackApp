package com.example.moneytrackapp;

public class User {
    public String username;
    public String email;

    public User() {
        // Diperlukan untuk Firebase
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
