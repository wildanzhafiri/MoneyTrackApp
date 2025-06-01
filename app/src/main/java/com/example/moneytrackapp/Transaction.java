package com.example.moneytrackapp;

public class Transaction {
    public String id; // ✅ Tambahan field ID
    public String category;
    public String amount;
    public String description;
    public String date;
    public int colorResId;
    public String imageBase64;

    // Constructor tanpa ID (untuk lokal)
    public Transaction(String category, String amount, String description, String date, int colorResId, String imageBase64) {
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.colorResId = colorResId;
        this.imageBase64 = imageBase64;
    }

    // Constructor dengan ID (untuk data Firebase)
    public Transaction(String id, String category, String amount, String description, String date, int colorResId, String imageBase64) {
        this(category, amount, description, date, colorResId, imageBase64);
        this.id = id;
    }
}
