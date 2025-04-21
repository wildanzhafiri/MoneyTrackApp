package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    Button logoutButton, backButton;
    ImageView imageProfile, iconEditProfile, dropdownCategories, dropdownCurrency, dropdownWishlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        imageProfile = findViewById(R.id.imageView);
        iconEditProfile = findViewById(R.id.ic_edit_profile);
        dropdownCategories = findViewById(R.id.dropdown_right_categories);
        logoutButton = findViewById(R.id.btn_logout);
        backButton = findViewById(R.id.btn_go_back);
        dropdownWishlist = findViewById(R.id.dropdown_right_wishlist);
        dropdownCurrency = findViewById(R.id.dropdown_right_currency);

        // Set click listeners for views
        imageProfile.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        iconEditProfile.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        dropdownCategories.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ManageCategoryActivity.class);
            startActivity(intent);
        });

//        dropdownWishlist.setOnClickListener(view -> {
//            Intent intent = new Intent(SettingsActivity.this, WishlistActivity.class);
//            startActivity(intent);
//        });

        dropdownCurrency.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, CurrencyActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

    }

}