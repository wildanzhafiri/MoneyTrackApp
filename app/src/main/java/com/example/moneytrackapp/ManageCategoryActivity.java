package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

public class ManageCategoryActivity extends AppCompatActivity {

    private ManageCategoryAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        recyclerView = findViewById(R.id.categoryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ManageCategoryAdapter(this, CategoryData.getCategories());
        recyclerView.setAdapter(adapter);

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
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Not used, swipe disabled
            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        adapter.setDragStartListener(viewHolder -> {
            touchHelper.startDrag(viewHolder);
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
}
