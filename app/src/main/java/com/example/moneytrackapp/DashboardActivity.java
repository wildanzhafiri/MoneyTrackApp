package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions = new ArrayList<>();

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
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://moneytrackapp-56fdd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")
                .child(currentUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);
                usernameText.setText(username != null ? username : "User");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                usernameText.setText("User");
            }
        });

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.home);

        recyclerView = findViewById(R.id.transactionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 🔁 Ambil transaksi milik user yang login
        DatabaseReference transactionsRef = FirebaseDatabase.getInstance("https://moneytrackapp-56fdd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")
                .child(currentUser.getUid())
                .child("transactions");

        transactionsRef.addValueEventListener(new ValueEventListener() {
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

                Collections.reverse(allTransactions); // urutkan dari terbaru
                List<Transaction> latest = allTransactions.subList(0, Math.min(3, allTransactions.size()));

                adapter = new TransactionAdapter(DashboardActivity.this, latest);
                recyclerView.setAdapter(adapter);

                // Sesuaikan tinggi RecyclerView
                int itemHeightDp = 120;
                float scale = getResources().getDisplayMetrics().density;
                int itemHeightPx = (int) (itemHeightDp * scale + 0.5f);
                recyclerView.getLayoutParams().height = itemHeightPx * latest.size();
                recyclerView.requestLayout();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnAll).setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, TransactionActivity.class));
        });

        findViewById(R.id.btn_expand_income).setOnClickListener(v -> {
            startActivity(new Intent(this, TransactionActivity.class));
        });

        findViewById(R.id.btn_expand_expense).setOnClickListener(v -> {
            startActivity(new Intent(this, TransactionActivity.class));
        });

        Button wishlistButton = findViewById(R.id.btn_open_wishlist);
        wishlistButton.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, WishlistActivity.class));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getBooleanExtra("deleted", false)) {
            recreate(); // refresh tampilan
        }
    }
}
