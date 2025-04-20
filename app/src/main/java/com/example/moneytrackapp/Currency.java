package com.example.moneytrackapp;

public class Currency {
    private final String name;
    private boolean isChecked;

    public Currency(String name) {
        this.name = name;
        this.isChecked = false;
    }

    // Getter & Setter
    public String getName() { return name; }
    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }
}
