package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class EditCategoryActivity extends AppCompatActivity {

    private RecyclerView iconRecyclerView;
    private EditText nameInput, budgetInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.add_transaction);

        EditText nameInput = findViewById(R.id.et_category_name);
        EditText budgetInput = findViewById(R.id.et_budget);
        RecyclerView iconRecyclerView = findViewById(R.id.icon_recycler);
        ImageView selectedIcon = findViewById(R.id.selected_icon);

        List<Integer> iconList = Arrays.asList(
                R.drawable.ic_groceries,
                R.drawable.ic_cafe,
                R.drawable.ic_electronics,
                R.drawable.ic_gifts,
                R.drawable.ic_laundry,
                R.drawable.ic_party,
                R.drawable.ic_liquor,
                R.drawable.ic_fuel,
                R.drawable.ic_maintenance,
                R.drawable.ic_education,
                R.drawable.ic_self_development,
                R.drawable.ic_money,
                R.drawable.ic_health,
                R.drawable.ic_transportation,
                R.drawable.ic_restaurant,
                R.drawable.ic_sport,
                R.drawable.ic_savings,
                R.drawable.ic_institute,
                R.drawable.ic_donate
        );

        IconGridAdapter adapter = new IconGridAdapter(this, iconList);
        iconRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        iconRecyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");
        if ("edit".equals(mode)) {
            String categoryName = intent.getStringExtra("category_name");
            int iconRes = intent.getIntExtra("icon_res", -1);

            nameInput.setText(categoryName);
            if (iconRes != -1) {
                selectedIcon.setImageResource(iconRes);
            }

            adapter.setSelectedIcon(iconRes);
        }

        adapter.setOnIconClickListener(selectedIcon::setImageResource);
    }

}
