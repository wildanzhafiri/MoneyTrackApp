package com.example.moneytrackapp;

public class Transaction {
    public String category;
    public String amount;
    public String description;
    public String date;
    public int colorResId;

    public Transaction(String category, String amount, String description, String date, int colorResId) {
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.colorResId = colorResId;
    }
}
