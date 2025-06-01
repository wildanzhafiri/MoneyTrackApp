
package com.example.moneytrackapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditTransactionActivity extends AppCompatActivity {

    private Spinner spinner;
    private RecyclerView categoryRecycler;
    private EditText etCategory, etAmount, etDesc;
    private ImageView ivPreview;
    private String base64Image = null;
    private String transactionId = null;
    private static final int IMAGE_PICK_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        etCategory = findViewById(R.id.et_category);
        etAmount = findViewById(R.id.et_amount);
        etDesc = findViewById(R.id.et_description);
        spinner = findViewById(R.id.spinner_type);
        ivPreview = findViewById(R.id.image_preview);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.transaction_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        categoryRecycler = findViewById(R.id.categoryRecyclerView);
        categoryRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        CategoryGridAdapter categoryAdapter = new CategoryGridAdapter(this, CategoryData.getCategories());
        categoryRecycler.setAdapter(categoryAdapter);
        categoryAdapter.setOnCategoryClickListener(categoryName -> etCategory.setText(categoryName));

        transactionId = getIntent().getStringExtra("transactionId");
        if (transactionId == null) {
            Toast.makeText(this, "Transaction ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance("https://tugasakhir-aca04-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("transactions")
                .child(transactionId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TransactionFirebase data = snapshot.getValue(TransactionFirebase.class);
                if (data != null) {
                    etCategory.setText(data.category);
                    etAmount.setText(data.amount.replace("Rp ", ""));
                    etDesc.setText(data.description != null ? data.description : "");
                    base64Image = data.imageBase64;

                    spinner.setSelection(data.colorResId == R.drawable.category_label_background_green ? 0 : 1);

                    if (base64Image != null && !base64Image.isEmpty()) {
                        byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        ivPreview.setImageBitmap(decodedBitmap);
                    }
                } else {
                    Toast.makeText(EditTransactionActivity.this, "Transaction not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditTransactionActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        Button btnUpload = findViewById(R.id.btn_upload_image);
        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE_PICK_CODE);
        });

        Button btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(v -> {
            if (transactionId == null) {
                Toast.makeText(this, "Transaction ID not found", Toast.LENGTH_SHORT).show();
                return;
            }

            String newCategory = etCategory.getText().toString();
            String newAmount = "Rp " + etAmount.getText().toString();
            String newDesc = etDesc.getText().toString();
            String newDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            String type = spinner.getSelectedItem().toString();

            int colorResId = type.equals("Income") ?
                    R.drawable.category_label_background_green :
                    R.drawable.category_label_background;

            TransactionFirebase updatedTransaction = new TransactionFirebase(
                    newCategory, newAmount, newDesc, newDate, base64Image, colorResId
            );

            FirebaseDatabase.getInstance("https://tugasakhir-aca04-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .getReference("transactions")
                    .child(transactionId)
                    .setValue(updatedTransaction)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, TransactionActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update transaction", Toast.LENGTH_SHORT).show();
                    });
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
