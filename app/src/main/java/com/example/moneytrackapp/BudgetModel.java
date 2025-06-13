package com.example.moneytrackapp;

import android.net.Uri;
import com.google.firebase.database.Exclude;

public class BudgetModel {
    private String categoryName;
    private int amount;
    private int progress;
    private int spent;
    private int left;
    private String imageBase64;
    private String budgetId;

    @Exclude
    private Uri imageUri;

    public BudgetModel() {}

    public BudgetModel(String categoryName, int amount, int progress, int spent, int left) {
        this.categoryName = categoryName;
        this.amount = amount;
        this.progress = progress;
        this.spent = spent;
        this.left = left;
    }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) {
        this.amount = amount;
        updateLeftAndProgress();
    }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public int getSpent() { return spent; }
    public void setSpent(int spent) {
        this.spent = spent;
        updateLeftAndProgress();
    }

    public int getLeft() { return left; }
    public void setLeft(int left) { this.left = left; }

    private void updateLeftAndProgress() {
        this.left = this.amount - this.spent;
        if (amount == 0) {
            this.progress = 0;
        } else {
            this.progress = (int) ((spent * 100.0f) / amount);
        }
    }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public String getBudgetId() { return budgetId; }
    public void setBudgetId(String budgetId) { this.budgetId = budgetId; }

    @Exclude
    public Uri getImageUri() { return imageUri; }
    public void setImageUri(Uri imageUri) { this.imageUri = imageUri; }
}
