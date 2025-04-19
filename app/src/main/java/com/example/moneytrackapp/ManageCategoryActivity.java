package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ManageCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        RecyclerView recyclerView = findViewById(R.id.categoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ManageCategoryAdapter(this, CategoryData.getCategories()));

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.add_transaction);

        Button goBack = findViewById(R.id.btn_go_back);
        goBack.setOnClickListener(v -> finish());

        Button addCategoryButton = findViewById(R.id.btn_add_category);
        addCategoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageCategoryActivity.this, AddCategoryActivity.class);
            intent.putExtra("mode", "add");
            startActivity(intent);
        });


    }
}
