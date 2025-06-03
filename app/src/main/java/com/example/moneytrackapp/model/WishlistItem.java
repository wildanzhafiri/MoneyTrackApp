package com.example.moneytrackapp.model;

public class WishlistItem {
    private String id;
    private String name;
    private double price;
    private String notes;
    private String imageBase64;

    public WishlistItem() {
        // Required for Firebase
    }

    public WishlistItem(String id, String name, double price, String notes, String imageBase64) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.notes = notes;
        this.imageBase64 = imageBase64;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
}
