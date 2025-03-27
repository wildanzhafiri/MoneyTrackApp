package com.example.moneytrackapp;

public class Category {
    public String name;
    public int iconResId;
    public int bgResId;

    public Category(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() { return name; }
    public int getIconRes() { return iconResId; }
}
