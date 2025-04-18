package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {

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

        RecyclerView recyclerView = findViewById(R.id.transactionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Transaction> allTransactions = TransactionRepository.getLatestTransactions(5);

        List<Transaction> latestTransactions = allTransactions.subList(0, Math.min(5, allTransactions.size()));

        TransactionAdapter adapter = new TransactionAdapter(this, latestTransactions);
        recyclerView.setAdapter(adapter);

        int itemHeightDp = 120;
        float scale = getResources().getDisplayMetrics().density;
        int itemHeightPx = (int) (itemHeightDp * scale + 0.5f);

        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
        layoutParams.height = itemHeightPx * latestTransactions.size();
        recyclerView.setLayoutParams(layoutParams);

        TextView seeAll = findViewById(R.id.btnAll);
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, TransactionActivity.class);
                startActivity(intent);
            }
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
}