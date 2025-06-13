package com.example.moneytrackapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        menuIcon.setOnClickListener(v -> Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show());

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.transaction);

        recyclerView = findViewById(R.id.transactionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        paginationContainer = findViewById(R.id.paginationContainer);

        // ✅ Ambil UID user yang login
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = user.getUid();

        // ✅ Akses transaksi di dalam /users/{uid}/transactions
        FirebaseDatabase.getInstance("https://moneytrackapp-56fdd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users")
                .child(uid)
                .child("transactions")
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
    }

    private void setupPagination() {
        paginationContainer.removeAllViews();
        int totalPages = (int) Math.ceil((double) allTransactions.size() / ITEMS_PER_PAGE);

        TextView prevBtn = createPageButton("<", () -> {
            if (currentPage > 1) loadTransactionsForPage(currentPage - 1);
        });
        paginationContainer.addView(prevBtn);

        for (int i = 1; i <= totalPages; i++) {
            int page = i;
            TextView pageBtn = createPageButton(String.valueOf(i), () -> loadTransactionsForPage(page));
            paginationContainer.addView(pageBtn);
        }

        TextView nextBtn = createPageButton(">", () -> {
            if (currentPage < totalPages) loadTransactionsForPage(currentPage + 1);
        });
        paginationContainer.addView(nextBtn);
    }

    private TextView createPageButton(String text, Runnable onClick) {
        TextView btn = new TextView(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(14f);
        btn.setPadding(16, 8, 16, 8);
        btn.setGravity(Gravity.CENTER);
        btn.setOnClickListener(v -> onClick.run());
        return btn;
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
        recyclerView.getLayoutParams().height = itemHeightPx * pageItems.size();
        recyclerView.requestLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getBooleanExtra("deleted", false)) {
            recreate(); // reload activity untuk ambil ulang data terbaru
        }
    }
}
