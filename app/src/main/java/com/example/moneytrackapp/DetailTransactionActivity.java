package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Iterator;

public class DetailTransactionActivity extends AppCompatActivity {

    private TextView tvTransactionDate, tvAmount, tvCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);

        tvTransactionDate = findViewById(R.id.tvTransactionDate);
        tvAmount = findViewById(R.id.tvAmount);
        tvCategory = findViewById(R.id.tvCategory);

        String date = getIntent().getStringExtra("date");
        String amount = getIntent().getStringExtra("amount");
        String category = getIntent().getStringExtra("category");
        String description = getIntent().getStringExtra("description");

        tvTransactionDate.setText(date);
        tvAmount.setText(amount);
        tvCategory.setText(category);

        Button btnEdit = findViewById(R.id.button);
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditTransactionActivity.class);
            intent.putExtra("date", date);
            intent.putExtra("amount", amount);
            intent.putExtra("category", category);
            intent.putExtra("description", description);
            startActivity(intent);
        });


        Button btnDelete = findViewById(R.id.button2);
        btnDelete.setOnClickListener(v -> {
            TransactionRepository.removeTransaction(date, amount, category);

            Intent resultIntent = new Intent();
            resultIntent.putExtra("deleted", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

    }
}
