package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddTransactionActivity extends AppCompatActivity {

    private Spinner spinner;
    private RecyclerView categoryRecycler;
    private CategoryGridAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        EditText etCategory = findViewById(R.id.et_category);

        spinner = findViewById(R.id.spinner_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.transaction_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        categoryRecycler = findViewById(R.id.categoryRecyclerView);
        categoryRecycler.setLayoutManager(new GridLayoutManager(this, 3));

        categoryAdapter = new CategoryGridAdapter(this, CategoryData.getCategories());
        categoryRecycler.setAdapter(categoryAdapter);

        categoryAdapter.setOnCategoryClickListener(categoryName -> {
            etCategory.setText(categoryName);
        });

        Button manageCategoryButton = findViewById(R.id.manage_category_button);
        manageCategoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageCategoryActivity.class);
            startActivity(intent);
        });

        Button manageContinueButton = findViewById(R.id.btn_continue);
        manageContinueButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TransactionActivity.class);
            startActivity(intent);
        });

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.add_transaction);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (categoryAdapter != null) {
            categoryAdapter.notifyDataSetChanged();
        }
    }
}
