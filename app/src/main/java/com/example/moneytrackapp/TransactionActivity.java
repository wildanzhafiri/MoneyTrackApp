package com.example.moneytrackapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        recyclerView = findViewById(R.id.transactionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        paginationContainer = findViewById(R.id.paginationContainer);

        allTransactions = getAllTransactions();

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


    private List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        list.add(new Transaction("Makan", "Rp 20.000", "Ayam Ungkep Reguler", "27/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Sangu", "Rp 100.000", "Sangu dari mamah", "25/01/2025", R.drawable.category_label_background_green));
        list.add(new Transaction("Jajan", "Rp 9.000", "Kue Sus", "25/01/2025", R.drawable.category_label_background_green));
        list.add(new Transaction("Joki", "Rp 50.000", "Laprak PAW", "24/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Bensin", "Rp 30.000", "Sangu dari mamah", "24/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Makan", "Rp 10.000", "Tahu Telor", "24/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Lainnya", "Rp 0", "Tidak ada transaksi", "—", R.drawable.category_label_background));
        list.add(new Transaction("Makan", "Rp 20.000", "Ayam Ungkep Reguler", "27/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Sangu", "Rp 100.000", "Sangu dari mamah", "25/01/2025", R.drawable.category_label_background_green));
        list.add(new Transaction("Jajan", "Rp 9.000", "Kue Sus", "25/01/2025", R.drawable.category_label_background_green));
        list.add(new Transaction("Joki", "Rp 50.000", "Laprak PAW", "24/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Bensin", "Rp 30.000", "Sangu dari mamah", "24/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Makan", "Rp 10.000", "Tahu Telor", "24/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Lainnya", "Rp 0", "Tidak ada transaksi", "—", R.drawable.category_label_background));
        list.add(new Transaction("Makan", "Rp 20.000", "Ayam Ungkep Reguler", "27/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Sangu", "Rp 100.000", "Sangu dari mamah", "25/01/2025", R.drawable.category_label_background_green));
        list.add(new Transaction("Jajan", "Rp 9.000", "Kue Sus", "25/01/2025", R.drawable.category_label_background_green));
        list.add(new Transaction("Joki", "Rp 50.000", "Laprak PAW", "24/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Bensin", "Rp 30.000", "Sangu dari mamah", "24/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Makan", "Rp 10.000", "Tahu Telor", "24/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Lainnya", "Rp 0", "Tidak ada transaksi", "—", R.drawable.category_label_background));
        list.add(new Transaction("Makan", "Rp 20.000", "Ayam Ungkep Reguler", "27/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Sangu", "Rp 100.000", "Sangu dari mamah", "25/01/2025", R.drawable.category_label_background_green));
        list.add(new Transaction("Jajan", "Rp 9.000", "Kue Sus", "25/01/2025", R.drawable.category_label_background_green));
        list.add(new Transaction("Joki", "Rp 50.000", "Laprak PAW", "24/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Bensin", "Rp 30.000", "Sangu dari mamah", "24/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Makan", "Rp 10.000", "Tahu Telor", "24/01/2025", R.drawable.category_label_background));
        list.add(new Transaction("Lainnya", "Rp 0", "Tidak ada transaksi", "—", R.drawable.category_label_background));
        return list;
    }
}
