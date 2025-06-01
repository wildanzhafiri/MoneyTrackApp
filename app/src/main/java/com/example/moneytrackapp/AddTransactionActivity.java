
package com.example.moneytrackapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private Spinner spinner;
    private RecyclerView categoryRecycler;
    private String base64Image = null;
    private static final int IMAGE_PICK_CODE = 1000;

    private EditText etCategory, etAmount, etDesc;
    private ImageView ivPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        etCategory = findViewById(R.id.et_category);
        etAmount = findViewById(R.id.et_amount);
        etDesc = findViewById(R.id.et_description);
        ivPreview = findViewById(R.id.iv_preview);

        spinner = findViewById(R.id.spinner_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.transaction_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        categoryRecycler = findViewById(R.id.categoryRecyclerView);
        categoryRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        CategoryGridAdapter categoryAdapter = new CategoryGridAdapter(this, CategoryData.getCategories());
        categoryRecycler.setAdapter(categoryAdapter);

        categoryAdapter.setOnCategoryClickListener(categoryName -> {
            etCategory.setText(categoryName);
        });

        Button btnUpload = findViewById(R.id.btn_upload_image);
        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE_PICK_CODE);
        });

        Button btnManageCategory = findViewById(R.id.manage_category_button);
        btnManageCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageCategory.class);
            startActivity(intent);
        });

        Button btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(v -> {
            String category = etCategory.getText().toString();
            String amount = "Rp " + etAmount.getText().toString();
            String desc = etDesc.getText().toString();
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            String type = spinner.getSelectedItem().toString();

            int colorResId = type.equals("Income") ?
                    R.drawable.category_label_background_green :
                    R.drawable.category_label_background;

            DatabaseReference dbRef = FirebaseDatabase
                    .getInstance("https://tugasakhir-aca04-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("transactions");

            String transactionId = dbRef.push().getKey();

            if (transactionId != null) {
                TransactionFirebase firebaseTransaction = new TransactionFirebase(
                        category, amount, desc, date, base64Image, colorResId
                );

                dbRef.child(transactionId).setValue(firebaseTransaction)
                        .addOnSuccessListener(aVoid -> {
                            Transaction localTransaction = new Transaction(transactionId, category, amount, desc, date, colorResId, base64Image);
                            TransactionRepository.addTransaction(localTransaction);

                            Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, DetailTransactionActivity.class);
                            intent.putExtra("transactionId", transactionId);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to save transaction", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Failed to generate transaction ID", Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.add_transaction);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                ivPreview.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                byte[] imageBytes = baos.toByteArray();
                base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
