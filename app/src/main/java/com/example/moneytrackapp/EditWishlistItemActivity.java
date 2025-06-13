package com.example.moneytrackapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moneytrackapp.model.WishlistItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditWishlistItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private String editingId = null;

    private EditText nameEditText, priceEditText, notesEditText;
    private ImageView imageView;
    private Button saveButton;
    private String imageBase64 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wishlist_item);

        nameEditText = findViewById(R.id.editTextName);
        priceEditText = findViewById(R.id.editTextPrice);
        notesEditText = findViewById(R.id.editTextNotes);
        imageView = findViewById(R.id.imageView);
        saveButton = findViewById(R.id.buttonSave);

        imageView.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveItem());

        if (getIntent().hasExtra("id")) {
            editingId = getIntent().getStringExtra("id");

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance("https://moneytrackapp-56fdd-default-rtdb.asia-southeast1.firebasedatabase.app")
                        .getReference("users")
                        .child(user.getUid())
                        .child("wishlist")
                        .child(editingId);

                ref.get().addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        WishlistItem item = snapshot.getValue(WishlistItem.class);
                        if (item != null) {
                            nameEditText.setText(item.getName());
                            priceEditText.setText(String.valueOf(item.getPrice()));
                            notesEditText.setText(item.getNotes());
                            imageBase64 = item.getImageBase64();

                            if (imageBase64 != null && !imageBase64.isEmpty()) {
                                byte[] decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load item", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
                imageBase64 = encodeImageToBase64(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, true); // Resize untuk efisiensi
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos); // Kompresi
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private void saveItem() {
        String name = nameEditText.getText().toString().trim();
        String notes = notesEditText.getText().toString().trim();
        String priceStr = priceEditText.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Name and Price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference wishlistRef = FirebaseDatabase.getInstance("https://moneytrackapp-56fdd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users")
                .child(user.getUid())
                .child("wishlist");

        String id = (editingId != null) ? editingId : wishlistRef.push().getKey();
        WishlistItem item = new WishlistItem(id, name, price, notes, imageBase64);
        wishlistRef.child(id).setValue(item).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to save item", Toast.LENGTH_SHORT).show();
        });
    }
}
