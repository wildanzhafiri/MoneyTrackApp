package com.example.moneytrackapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private LinearLayout paginationContainer;

    private final int ITEMS_PER_PAGE = 5;
    private int currentPage = 1;
    private List<Transaction> allTransactions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tansaction);

        Toolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Transaction Report");
        ImageView menuIcon = new ImageView(this);
        menuIcon.setImageResource(R.drawable.ic_menu);
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.WRAP_CONTENT,
                Gravity.END
        );
        menuIcon.setLayoutParams(layoutParams);
        menuIcon.setPadding(0, 0, 36, 0);


        toolbar.addView(menuIcon);


        menuIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show();
        });


        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.transaction);

        recyclerView = findViewById(R.id.transactionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        paginationContainer = findViewById(R.id.paginationContainer);

        FirebaseDatabase.getInstance("https://tugasakhir-aca04-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("transactions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allTransactions.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            TransactionFirebase t = snap.getValue(TransactionFirebase.class);
                            if (t != null) {
                                allTransactions.add(new Transaction(
                                        snap.getKey(),
                                        t.category,
                                        t.amount,
                                        t.description,
                                        t.date,
                                        t.colorResId,
                                        t.imageBase64
                                ));
                            }
                        }
                        setupPagination();
                        loadTransactionsForPage(1);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(TransactionActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
                    }
                });


        setupPagination();
        loadTransactionsForPage(1);
    }

    private void setupPagination() {
        paginationContainer.removeAllViews();

        int totalPages = (int) Math.ceil((double) allTransactions.size() / ITEMS_PER_PAGE);

        TextView prevBtn = new TextView(this);
        prevBtn.setText("<");
        prevBtn.setTextColor(Color.WHITE);
        prevBtn.setTextSize(14f);
        prevBtn.setPadding(16, 8, 16, 8);
        prevBtn.setGravity(Gravity.CENTER);
        prevBtn.setClickable(true);
        prevBtn.setOnClickListener(v -> {
            if (currentPage > 1) {
                loadTransactionsForPage(currentPage - 1);
            }
        });
        paginationContainer.addView(prevBtn);

        for (int i = 1; i <= totalPages; i++) {
            TextView pageBtn = new TextView(this);
            pageBtn.setText(String.valueOf(i));
            pageBtn.setTextColor(Color.WHITE);
            pageBtn.setTextSize(14f);
            pageBtn.setPadding(16, 8, 16, 8);
            pageBtn.setGravity(Gravity.CENTER);
            pageBtn.setClickable(true);
            final int pageNumber = i;
            pageBtn.setOnClickListener(v -> loadTransactionsForPage(pageNumber));


            paginationContainer.addView(pageBtn);
        }

        TextView nextBtn = new TextView(this);
        nextBtn.setText(">");
        nextBtn.setTextColor(Color.WHITE);
        nextBtn.setTextSize(14f);
        nextBtn.setPadding(16, 8, 16, 8);
        nextBtn.setGravity(Gravity.CENTER);
        nextBtn.setClickable(true);
        nextBtn.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                loadTransactionsForPage(currentPage + 1);
            }
        });
        paginationContainer.addView(nextBtn);
    }


    private void loadTransactionsForPage(int page) {
        currentPage = page;

        int fromIndex = (page - 1) * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, allTransactions.size());

        List<Transaction> pageItems = allTransactions.subList(fromIndex, toIndex);

        if (adapter == null) {
            adapter = new TransactionAdapter(this, pageItems);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(pageItems);
        }

        int itemHeightDp = 120;
        float scale = getResources().getDisplayMetrics().density;
        int itemHeightPx = (int) (itemHeightDp * scale + 0.5f);

        int visibleItemCount = pageItems.size();
        recyclerView.getLayoutParams().height = itemHeightPx * visibleItemCount;
        recyclerView.requestLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("deleted", false)) {
                allTransactions = TransactionRepository.getAllTransactions();
                setupPagination();
                loadTransactionsForPage(currentPage);
            }
        }
    }


}
