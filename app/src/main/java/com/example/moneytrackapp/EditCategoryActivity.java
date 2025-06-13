package com.example.moneytrackapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditCategoryActivity extends AppCompatActivity {

    private RecyclerView iconRecyclerView;
    private EditText nameInput;
    private ImageView selectedIcon;
    private IconGridAdapter adapter;

    private int selectedIconRes = -1;
    private String selectedIconUrl = null;
    private String categoryId;
    private String initialCategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.add_transaction);

        nameInput = findViewById(R.id.et_category_name);
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

        List<String> uploadedIconUrls = Collections.emptyList();

        adapter = new IconGridAdapter(this, iconList, uploadedIconUrls);
        iconRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        iconRecyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        categoryId = intent.getStringExtra("category_id");
        initialCategoryName = intent.getStringExtra("category_name");
        selectedIconRes = intent.getIntExtra("icon_res", -1);
        selectedIconUrl = intent.getStringExtra("icon_url");

        nameInput.setText(initialCategoryName);

        if (selectedIconRes != -1) {
            selectedIcon.setImageResource(selectedIconRes);
            adapter.setSelectedIconFromRes(selectedIconRes);
        } else if (selectedIconUrl != null && !selectedIconUrl.isEmpty()) {
            if (selectedIconUrl.startsWith("data:image") || selectedIconUrl.length() > 100) {
                byte[] imageBytes = Base64.decode(selectedIconUrl, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                selectedIcon.setImageBitmap(bitmap);
            } else {
                Glide.with(this).load(selectedIconUrl).into(selectedIcon);
            }
        }


        adapter.setOnIconSelectedListener((resId, imageUrl) -> {
            selectedIconRes = resId;
            selectedIconUrl = imageUrl;

            if (resId != -1) {
                selectedIcon.setImageResource(resId);
            } else {
                Glide.with(this).load(imageUrl).into(selectedIcon);
            }
        });

        Button btnDone = findViewById(R.id.btn_done);
        btnDone.setOnClickListener(v -> {
            String newCategoryName = nameInput.getText().toString().trim();

            if (newCategoryName.isEmpty() || (selectedIconRes == -1 && selectedIconUrl == null)) {
                ToastUtils.showStaticToast(this, "Please fill all fields");
                return;
            }

            if (categoryId == null || categoryId.isEmpty()) {
                ToastUtils.showStaticToast(this, "Category ID not found!");
                return;
            }

            Category updated = new Category();
            updated.setId(categoryId);
            updated.setName(newCategoryName);
            updated.setIconRes(selectedIconRes);
            updated.setIconUrl(selectedIconUrl);

            CategoryData.updateCategory(categoryId, newCategoryName, selectedIconRes); // Optional: bisa diubah pakai objek
            ToastUtils.showStaticToast(this, "Category updated successfully!");
            finish();
        });

        Button btnDelete = findViewById(R.id.btn_delete_category);
        btnDelete.setOnClickListener(v -> {
            CategoryData.deleteCategory(categoryId);
            ToastUtils.showStaticToast(this, "Category deleted successfully!");
            finish();
        });
    }
}
