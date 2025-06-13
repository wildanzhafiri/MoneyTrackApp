package com.example.moneytrackapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

public class ManageCategoryActivity extends AppCompatActivity {

    private ManageCategoryAdapter adapter;
    private RecyclerView recyclerView;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        recyclerView = findViewById(R.id.categoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();

                Collections.swap(CategoryData.getCategories(), fromPos, toPos);
                adapter.notifyItemMoved(fromPos, toPos);

                List<Category> currentList = CategoryData.getCategories();
                for (int i = 0; i < currentList.size(); i++) {
                    Category c = currentList.get(i);
                    c.setOrderIndex(i);
                    if (c.getId() != null) {
                        FirebaseHelper.updateCategoryById(c.getId(), c);
                    }
                }

                return true;
            }


            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        CategoryData.loadCategories(() -> {
            adapter = new ManageCategoryAdapter(this, CategoryData.getCategories());
            recyclerView.setAdapter(adapter);

            adapter.setDragStartListener(viewHolder -> {
                touchHelper.startDrag(viewHolder);
            });

            adapter.notifyDataSetChanged();
        });

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.add_transaction);

        Button goBack = findViewById(R.id.btn_go_back);
        goBack.setOnClickListener(v -> finish());

        Button addCategoryButton = findViewById(R.id.btn_add_category);
        addCategoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCategoryActivity.class);
            intent.putExtra("mode", "add");
            startActivity(intent);
        });
    }

    private void insertInitialCategoriesIfEmpty() {
        FirebaseHelper.fetchOnce(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categories = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Category category = data.getValue(Category.class);
                    if (category != null) {
                        category.setId(data.getKey());
                        categories.add(category);
                    }
                }

                Collections.sort(categories, Comparator.comparingInt(Category::getOrderIndex));
                CategoryData.setCategories(categories);
                if (adapter != null) adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


}

