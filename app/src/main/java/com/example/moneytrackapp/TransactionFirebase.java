
package com.example.moneytrackapp;

public class TransactionFirebase {
    public String category, amount, description, date, imageBase64;
    public int colorResId; // ✅ Tambahan field

    public TransactionFirebase() {} // Diperlukan oleh Firebase

    public TransactionFirebase(String category, String amount, String description, String date, String imageBase64, int colorResId) {
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.imageBase64 = imageBase64;
        this.colorResId = colorResId;
    }
}
