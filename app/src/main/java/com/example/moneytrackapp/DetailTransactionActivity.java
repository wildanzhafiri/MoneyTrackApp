package com.example.moneytrackapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailTransactionActivity extends AppCompatActivity {

    private TextView tvDate, tvAmount, tvCategory;
    private ImageView imagePreview;
    private Button editButton, deleteButton;
    private String transactionId;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);

        // Inisialisasi view
        tvDate = findViewById(R.id.tvTransactionDate);
        tvAmount = findViewById(R.id.tvAmount);
        tvCategory = findViewById(R.id.tvCategory);
        imagePreview = findViewById(R.id.imagePreview);
        editButton = findViewById(R.id.button);      // Tombol Edit
        deleteButton = findViewById(R.id.button2);   // Tombol Delete

        // Ambil UID user login
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        transactionId = getIntent().getStringExtra("transactionId");
        if (transactionId == null) {
            Toast.makeText(this, "Transaction ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tombol Edit
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditTransactionActivity.class);
            intent.putExtra("transactionId", transactionId);
            startActivity(intent);
        });

        // Tombol Delete
        deleteButton.setOnClickListener(v -> {
            FirebaseDatabase.getInstance("https://moneytrackapp-56fdd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("users")
                    .child(currentUser.getUid())
                    .child("transactions")
                    .child(transactionId)
                    .removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, TransactionActivity.class);
                        intent.putExtra("deleted", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to delete transaction", Toast.LENGTH_SHORT).show();
                    });
        });

        // Ambil data dari Firebase
        FirebaseDatabase.getInstance("https://moneytrackapp-56fdd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")
                .child(currentUser.getUid())
                .child("transactions")
                .child(transactionId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        TransactionFirebase data = snapshot.getValue(TransactionFirebase.class);
                        if (data != null) {
                            tvDate.setText(data.date != null ? data.date : "-");
                            tvAmount.setText(data.amount != null ? data.amount : "-");
                            tvCategory.setText(data.category != null ? data.category : "-");

                            if (data.imageBase64 != null && !data.imageBase64.isEmpty()) {
                                try {
                                    byte[] imageBytes = Base64.decode(data.imageBase64, Base64.DEFAULT);
                                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                    imagePreview.setImageBitmap(decodedBitmap);
                                } catch (Exception e) {
                                    Toast.makeText(DetailTransactionActivity.this, "Failed to decode image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(DetailTransactionActivity.this, "Transaction not found", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DetailTransactionActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}
