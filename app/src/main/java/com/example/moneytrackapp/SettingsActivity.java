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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    Button logoutButton, backButton;
    ImageView imageProfile, dropdownEditProfile, dropdownCategories, dropdownCurrency, dropdownWishlist, dropdownWallet;
    TextView usernameView, editProfile, manageCategories, myWishlist, chooseCurrency, myWallets;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        usernameView = findViewById(R.id.usernameView);
        imageProfile = findViewById(R.id.imageView);
        editProfile = findViewById(R.id.et_);
        manageCategories = findViewById(R.id.et_categories);
        myWishlist = findViewById(R.id.et_language);
        myWallets = findViewById(R.id.et_wallet);
        chooseCurrency = findViewById(R.id.et_currency);
        dropdownEditProfile = findViewById(R.id.dropdown_right_profile);
        dropdownCategories = findViewById(R.id.dropdown_right_categories);
        dropdownWishlist = findViewById(R.id.dropdown_right_wishlist);
        dropdownWallet = findViewById(R.id.dropdown_right_wallet);
        dropdownCurrency = findViewById(R.id.dropdown_right_currency);
        logoutButton = findViewById(R.id.btn_logout);
        backButton = findViewById(R.id.btn_go_back);

        // Set click listeners for views
        imageProfile.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        editProfile.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        manageCategories.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ManageCategoryActivity.class);
            startActivity(intent);
        });
        myWishlist.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, WishlistActivity.class);
            startActivity(intent);
        });
        myWallets.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, WalletActivity.class);
            startActivity(intent);
        });
        chooseCurrency.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, CurrencyActivity.class);
            startActivity(intent);
        });
        dropdownEditProfile.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        dropdownCategories.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, ManageCategoryActivity.class);
            startActivity(intent);
        });
        dropdownWishlist.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, WishlistActivity.class);
            startActivity(intent);
        });
        dropdownWallet.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, WalletActivity.class);
            startActivity(intent);
        });
        dropdownCurrency.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, CurrencyActivity.class);
            startActivity(intent);
        });
        logoutButton.setOnClickListener(view -> {
            mAuth.signOut();

            Intent intent = new Intent(SettingsActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Metode onResume untuk memastikan username dan foto profil terbaru di-load setiap kali kembali ke SettingsActivity
    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

            // Memuat username dari Firebase Authentication (display name)
            String currentUsername = currentUser.getDisplayName();
            if (currentUsername != null && !currentUsername.isEmpty()) {
                usernameView.setText(currentUsername);
            } else {
                usernameView.setText(R.string.user); // Default jika display name kosong
            }

            // Memuat foto profil dari Firebase Realtime Database
            userRef.child("profileImageBase64").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String profileImageBase64 = snapshot.getValue(String.class); // Ambil sebagai Base64 string
                    if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
                        // Dekode Base64 string menjadi Bitmap
                        Bitmap bitmap = decodeBase64ToBitmap(profileImageBase64);
                        if (bitmap != null) {
                            imageProfile.setImageBitmap(bitmap); // Set Bitmap ke ImageView
                        } else {
                            imageProfile.setImageResource(R.drawable.def_profile_img); // Tampilkan default jika decode gagal
                        }
                    } else {
                        imageProfile.setImageResource(R.drawable.def_profile_img); // Tampilkan default jika tidak ada Base64
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SettingsActivity.this, "Gagal memuat foto profil: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    imageProfile.setImageResource(R.drawable.def_profile_img);
                }
            });
        } else {
            usernameView.setText(R.string.guest);
            imageProfile.setImageResource(R.drawable.def_profile_img);
        }
    }

    private Bitmap decodeBase64ToBitmap(String base64String) {
        if (base64String == null || base64String.isEmpty()) return null;

        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}