package com.example.moneytrackapp;

public class Category {
    private String id;
    private String name;
    private int iconRes;
    private int orderIndex;
    private String iconUrl;

    public Category() {
        // Diperlukan Firebase
    }

    public Category(String id, String name, int iconRes, int orderIndex) {
        this.id = id;
        this.name = name;
        this.iconRes = iconRes;
        this.orderIndex = orderIndex;
    }

    public Category(String name, int iconRes) {
        this.name = name;
        this.iconRes = iconRes;
    }
    // Getter & Setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

}
