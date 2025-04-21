package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class EditWishlistItemActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "com.example.moneytrackapp.EXTRA_ID";
    public static final String EXTRA_NAME = "com.example.moneytrackapp.EXTRA_NAME";
    public static final String EXTRA_PRICE = "com.example.moneytrackapp.EXTRA_PRICE";
    public static final String EXTRA_NOTES = "com.example.moneytrackapp.EXTRA_NOTES";
    public static final String EXTRA_IMAGE_URI = "com.example.moneytrackapp.EXTRA_IMAGE_URI";

    private EditText editTextName;
    private EditText editTextPrice;
    private EditText editTextNotes;
    private long itemId = -1;
    private String imageUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wishlist_item);

        editTextName = findViewById(R.id.edit_text_name);
        editTextPrice = findViewById(R.id.edit_text_price);
        editTextNotes = findViewById(R.id.edit_text_notes);
        Button buttonSave = findViewById(R.id.button_save);
        Button buttonDelete = findViewById(R.id.button_delete);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            // Editing existing item
            setTitle("Edit Wishlist Item");
            itemId = intent.getLongExtra(EXTRA_ID, -1);
            editTextName.setText(intent.getStringExtra(EXTRA_NAME));
            editTextPrice.setText(String.valueOf(intent.getDoubleExtra(EXTRA_PRICE, 0.0)));
            editTextNotes.setText(intent.getStringExtra(EXTRA_NOTES));
            imageUri = intent.getStringExtra(EXTRA_IMAGE_URI) != null ? 
                    intent.getStringExtra(EXTRA_IMAGE_URI) : "";
            
            // Show delete button for existing items
            buttonDelete.setVisibility(View.VISIBLE);
        } else {
            // Adding new item
            setTitle("Add Wishlist Item");
            buttonDelete.setVisibility(View.GONE);
        }

        buttonSave.setOnClickListener(v -> saveItem());
        
        buttonDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Send delete signal back to activity
                    Intent resultData = new Intent();
                    resultData.putExtra(EXTRA_ID, itemId);
                    resultData.putExtra("DELETE", true);
                    setResult(RESULT_OK, resultData);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });
    }

    private void saveItem() {
        String name = editTextName.getText().toString();
        String priceStr = editTextPrice.getText().toString();
        String notes = editTextNotes.getText().toString();

        if (name.trim().isEmpty() || priceStr.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a name and price", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_NAME, name);
        data.putExtra(EXTRA_PRICE, price);
        data.putExtra(EXTRA_NOTES, notes);
        data.putExtra(EXTRA_IMAGE_URI, imageUri);

        if (itemId != -1) {
            data.putExtra(EXTRA_ID, itemId);
        }

        setResult(RESULT_OK, data);
        finish();
    }
}
