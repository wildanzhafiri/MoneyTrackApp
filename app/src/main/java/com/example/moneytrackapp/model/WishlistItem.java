package com.example.moneytrackapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import java.util.Objects;

@Entity(tableName = "wishlist_items")
public class WishlistItem {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private double price;
    private String notes;
    private String imageUri;

    public WishlistItem() {
    }

    @Ignore
    public WishlistItem(String name, double price, String notes, String imageUri) {
        this.name = name;
        this.price = price;
        this.notes = notes;
        this.imageUri = imageUri;
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WishlistItem that = (WishlistItem) o;
        return id == that.id &&
               Double.compare(that.price, price) == 0 &&
               Objects.equals(name, that.name) &&
               Objects.equals(notes, that.notes) &&
               Objects.equals(imageUri, that.imageUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, notes, imageUri);
    }
}
