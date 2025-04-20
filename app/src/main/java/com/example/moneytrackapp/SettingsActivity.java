package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {
    private Button editButton, logoutButton, backButton;
    private ImageView dropdownCategories, dropdownCurrency, dropdownWishlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editButton = findViewById(R.id.btn_edit_profile);
        dropdownCategories = findViewById(R.id.dropdown_right_categories);
        logoutButton = findViewById(R.id.btn_logout);
        backButton = findViewById(R.id.btn_go_back);
        dropdownWishlist = findViewById(R.id.dropdown_right_wishlist);
        dropdownCurrency = findViewById(R.id.dropdown_right_currency);

        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        dropdownCategories.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ManageCategory.class);
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