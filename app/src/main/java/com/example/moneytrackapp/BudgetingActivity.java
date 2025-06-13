package com.example.moneytrackapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BudgetingActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 2001;
    private int currentImagePickPosition = -1;

    private RecyclerView recyclerView;
    private BudgetAdapter adapter;
    private List<BudgetModel> budgetList;

    private Button btnToggleDropdown;
    private LinearLayout layoutDropdown;

    private DatabaseReference budgetRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budgeting);

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.budgeting);

        recyclerView = findViewById(R.id.budgetRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        budgetRef = FirebaseDatabase.getInstance("https://moneytrackapp-56fdd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users").child(uid).child("budgets");

        budgetList = new ArrayList<>();
        adapter = new BudgetAdapter(this, budgetList);
        recyclerView.setAdapter(adapter);

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
        loadBudgetsFromFirebase();
    }

    private void setupDropdownListeners() {
        layoutDropdown.removeAllViews();

        List<Category> categories = CategoryData.getCategories();

        for (Category category : categories) {
            TextView textView = new TextView(this);
            textView.setText(category.getName());
            textView.setTextSize(16f);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setPadding(32, 24, 32, 24);
            textView.setBackgroundResource(R.drawable.bg_dropdown_item);
            textView.setClickable(true);
            textView.setFocusable(true);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 8, 0, 8);
            textView.setLayoutParams(params);

            textView.setOnClickListener(v -> {
                BudgetModel newItem = new BudgetModel(category.getName(), 0, 0, 0, 0);
                String budgetId = budgetRef.push().getKey();
                newItem.setBudgetId(budgetId);

                if (budgetId != null) {
                    budgetRef.child(budgetId).setValue(newItem);
                    Toast.makeText(this, "Kategori " + category.getName() + " ditambahkan", Toast.LENGTH_SHORT).show();
                }

                layoutDropdown.setVisibility(View.GONE);
                btnToggleDropdown.setText("Add new budget ▼");
            });

            layoutDropdown.addView(textView);
        }
    }

    private void loadBudgetsFromFirebase() {
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                budgetList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    BudgetModel item = data.getValue(BudgetModel.class);
                    if (item != null) {
                        item.setBudgetId(data.getKey());
                        budgetList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BudgetingActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void launchImagePicker(int position) {
        currentImagePickPosition = position;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            if (currentImagePickPosition != -1 && currentImagePickPosition < budgetList.size()) {
                BudgetModel item = budgetList.get(currentImagePickPosition);

                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    byte[] imageBytes = new byte[inputStream.available()];
                    inputStream.read(imageBytes);
                    inputStream.close();

                    String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    item.setImageUri(selectedImageUri);
                    item.setImageBase64(imageBase64);

                    String budgetId = item.getBudgetId();
                    if (budgetId != null) {
                        budgetRef.child(budgetId).setValue(item);
                    }

                    adapter.notifyItemChanged(currentImagePickPosition);
                    Toast.makeText(this, "Gambar berhasil disimpan", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(this, "Gagal membaca gambar", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
