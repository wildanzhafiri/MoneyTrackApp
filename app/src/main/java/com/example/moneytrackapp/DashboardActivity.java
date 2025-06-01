package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        String username = getIntent().getStringExtra("USERNAME");
        TextView usernameText = findViewById(R.id.username_text);
        if (username != null && !username.isEmpty()) {
            usernameText.setText(username);
        } else {
            usernameText.setText("User");
        }

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.home);

        recyclerView = findViewById(R.id.transactionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

                        // Urutkan agar yang terbaru ada di atas (jika ada field waktu lebih baik pakai itu)
                        Collections.reverse(allTransactions);

                        List<Transaction> latest = allTransactions.subList(0, Math.min(3, allTransactions.size()));

                        adapter = new TransactionAdapter(DashboardActivity.this, latest);
                        recyclerView.setAdapter(adapter);

                        // Resize tinggi RecyclerView
                        int itemHeightDp = 120;
                        float scale = getResources().getDisplayMetrics().density;
                        int itemHeightPx = (int) (itemHeightDp * scale + 0.5f);
                        recyclerView.getLayoutParams().height = itemHeightPx * latest.size();
                        recyclerView.requestLayout();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DashboardActivity.this, "Failed to load recent transactions", Toast.LENGTH_SHORT).show();
                    }
                });

        TextView seeAll = findViewById(R.id.btnAll);
        seeAll.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, TransactionActivity.class);
            startActivity(intent);
        });

        LinearLayout manageExpandIncome = findViewById(R.id.btn_expand_income);
        manageExpandIncome.setOnClickListener(v -> {
            Intent intent = new Intent(this, TransactionActivity.class);
            startActivity(intent);
        });

        LinearLayout manageExpandExpense = findViewById(R.id.btn_expand_expense);
        manageExpandExpense.setOnClickListener(v -> {
            Intent intent = new Intent(this, TransactionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("deleted", false)) {
                recreate(); // refresh total
            }
        }
    }
}
