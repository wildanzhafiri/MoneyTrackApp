package com.example.moneytrackapp;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BudgetingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BudgetAdapter adapter;
    private List<BudgetModel> budgetList;

    private Button btnToggleDropdown;
    private LinearLayout layoutDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgeting);

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.budgeting);

        recyclerView = findViewById(R.id.budgetRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Dummy
        budgetList = new ArrayList<>();
        budgetList.add(new BudgetModel("Entertainment", 50000000, 50, 25000000, 25000000));

        adapter = new BudgetAdapter(this, budgetList);
        recyclerView.setAdapter(adapter);

        // Dropdown
        btnToggleDropdown = findViewById(R.id.btn_budget_dropdown);
        layoutDropdown = findViewById(R.id.layout_budget_dropdown);

        btnToggleDropdown.setOnClickListener(v -> {
            if (layoutDropdown.getVisibility() == View.VISIBLE) {
                layoutDropdown.setVisibility(View.GONE);
                btnToggleDropdown.setText("Add new budget ▼");
            } else {
                layoutDropdown.setVisibility(View.VISIBLE);
                btnToggleDropdown.setText("Add new budget ▲");
            }
        });
        setupDropdownListeners();
    }

    private void setupDropdownListeners() {
        layoutDropdown.removeAllViews(); // Bersihkan dulu

        List<Category> categories = CategoryData.getCategories();

        for (Category category : categories) {
            TextView textView = new TextView(this);
            textView.setText(category.getName());
            textView.setTextSize(16f);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setPadding(32, 24, 32, 24); // padding atas-bawah, kanan-kiri
            textView.setBackgroundResource(R.drawable.bg_dropdown_item); // gunakan drawable putih
            textView.setClickable(true);
            textView.setFocusable(true);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 8, 0, 8); // spasi antar item
            textView.setLayoutParams(params);

            textView.setOnClickListener(v -> {
                BudgetModel newItem = new BudgetModel(category.getName(), 0, 0, 0, 0);
                budgetList.add(newItem);
                adapter.notifyItemInserted(budgetList.size() - 1);
                layoutDropdown.setVisibility(View.GONE);
                btnToggleDropdown.setText("Add new budget ▼");
                Toast.makeText(this, "Kategori " + category.getName() + " ditambahkan", Toast.LENGTH_SHORT).show();
            });

            layoutDropdown.addView(textView);
        }
    }


}
