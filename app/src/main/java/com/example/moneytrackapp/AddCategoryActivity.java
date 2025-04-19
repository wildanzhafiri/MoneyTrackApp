package com.example.moneytrackapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.moneytrackapp.ToastUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class AddCategoryActivity extends AppCompatActivity {

    private RecyclerView iconRecyclerView;
    private EditText nameInput, budgetInput;
    private ImageView selectedIcon;
    private IconGridAdapter adapter;
    private int selectedIconRes = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.add_transaction);

        nameInput = findViewById(R.id.et_category_name);
        budgetInput = findViewById(R.id.et_budget);
        iconRecyclerView = findViewById(R.id.icon_recycler);
        selectedIcon = findViewById(R.id.selected_icon);

        List<Integer> iconList = Arrays.asList(
                R.drawable.ic_groceries, R.drawable.ic_cafe, R.drawable.ic_electronics,
                R.drawable.ic_gifts, R.drawable.ic_laundry, R.drawable.ic_party,
                R.drawable.ic_liquor, R.drawable.ic_fuel, R.drawable.ic_maintenance,
                R.drawable.ic_education, R.drawable.ic_self_development, R.drawable.ic_money,
                R.drawable.ic_health, R.drawable.ic_transportation, R.drawable.ic_restaurant,
                R.drawable.ic_sport, R.drawable.ic_savings, R.drawable.ic_institute,
                R.drawable.ic_donate
        );

        adapter = new IconGridAdapter(this, iconList);
        iconRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        iconRecyclerView.setAdapter(adapter);

        adapter.setOnIconClickListener(iconRes -> {
            selectedIconRes = iconRes;
            selectedIcon.setImageResource(iconRes);
        });

        Button btnAddCategory = findViewById(R.id.btn_add_category);
        btnAddCategory.setOnClickListener(v -> {
            String categoryName = nameInput.getText().toString().trim();
            String budget = budgetInput.getText().toString().trim();

                ToastUtils.showStaticToast(this, "Category added successfully!");
                finish();
        });

    }
}
