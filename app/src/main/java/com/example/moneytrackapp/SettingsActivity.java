package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {
    private Button backButton;
    private ImageView dropdownCategories;

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

        backButton = findViewById(R.id.btn_go_back);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        dropdownCategories = findViewById(R.id.dropdown_right_categories);
        dropdownCategories.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ManageCategory.class);
            startActivity(intent);
        });

    }

}