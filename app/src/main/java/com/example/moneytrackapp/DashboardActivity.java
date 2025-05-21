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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
            return;
        }

        TextView usernameText = findViewById(R.id.username_text);
        String username = currentUser.getEmail();
        usernameText.setText(username != null ? username : "User");

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.home);

        RecyclerView recyclerView = findViewById(R.id.transactionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (TransactionRepository.getAllTransactions().isEmpty()) {
            TransactionRepository.dummyTransaction();
        }

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
        
        Button wishlistButton = findViewById(R.id.btn_open_wishlist);
        wishlistButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, WishlistActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("deleted", false)) {
                recreate(); // ini paling simple: reload seluruh activity
            }
        }
    }

}