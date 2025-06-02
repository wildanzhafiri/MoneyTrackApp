package com.example.moneytrackapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView imageProfile;
    TextView uploadText;
    TextInputEditText editUsername;
    Button saveButton, deletePhotoButton;

    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private Uri selectedImageUri;

    private ActivityResultLauncher<Intent> pickImageLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Anda belum login.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, WelcomeActivity.class));
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        imageProfile = findViewById(R.id.profile_image);
        uploadText = findViewById(R.id.upload_text);
        editUsername = findViewById(R.id.username_input);
        saveButton = findViewById(R.id.btn_save_profile);
        deletePhotoButton = findViewById(R.id.btn_delete_profile);

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                imageProfile.setImageURI(selectedImageUri); // Tampilkan di ImageView langsung
                // PENTING: Unggah foto profil di sini (setelah memilih gambar)
                uploadProfilePhoto();
            }
        });

        loadUserProfile();

        saveButton.setOnClickListener(view -> saveUserProfile());
        uploadText.setOnClickListener(view -> openImageChooser());
        imageProfile.setOnClickListener(view -> openImageChooser());
        deletePhotoButton.setOnClickListener(view -> deleteProfilePhoto());
    }

    private void loadUserProfile() {
        if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            editUsername.setText(currentUser.getDisplayName());
        } else {
            editUsername.setText("");
        }

        // MEMUAT FOTO PROFIL DARI BASE64 DI REALTIME DATABASE
        userRef.child("profileImageBase64").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profileImageBase64 = snapshot.getValue(String.class);
                if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
                    // Konversi Base64 string menjadi Bitmap
                    Bitmap bitmap = decodeBase64ToBitmap(profileImageBase64);
                    if (bitmap != null) {
                        imageProfile.setImageBitmap(bitmap);
                        uploadText.setText(R.string.upload_profile_picture);
                    } else {
                        imageProfile.setImageResource(R.drawable.def_profile_img);
                        uploadText.setText(R.string.upload_profile_picture);
                    }
                } else {
                    imageProfile.setImageResource(R.drawable.def_profile_img);
                    uploadText.setText(R.string.upload_profile_picture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "failed to load photo: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                imageProfile.setImageResource(R.drawable.def_profile_img);
                uploadText.setText(R.string.upload_profile_picture);
            }
        });
    }

    private void saveUserProfile() {
        String newUsername = Objects.requireNonNull(editUsername.getText()).toString().trim();

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(newUsername).build();

        currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("username", newUsername);
                userRef.updateChildren(userMap).addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "profile updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }).addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "failed to update username: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(ProfileActivity.this, "failed to update username: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    // METODE UNTUK MENGUNGGAH FOTO PROFIL KE REALTIME DATABASE (sebagai Base64)
    private void uploadProfilePhoto() {
        if (selectedImageUri != null) {
            Toast.makeText(this, "uploading photo...", Toast.LENGTH_SHORT).show();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                String base64Image = encodeBitmapToBase64(bitmap);

                if (base64Image != null) {
                    // Simpan string Base64 ke Firebase Realtime Database
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("profileImageBase64", base64Image); // Simpan di field baru

                    userRef.updateChildren(userMap).addOnSuccessListener(aVoid -> {
                        Toast.makeText(ProfileActivity.this, "photo updated", Toast.LENGTH_SHORT).show();
                        uploadText.setText(R.string.upload_profile_picture);
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(null) // set null karena tidak ada URL
                                .build();
                        currentUser.updateProfile(profileUpdates);
                    }).addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "failed to update photo: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(this, "failed to encode image to Base64", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "no image selected", Toast.LENGTH_SHORT).show();
        }
    }

    // METODE UNTUK MENGHAPUS FOTO PROFIL (Base64)
    private void deleteProfilePhoto() {
        userRef.child("profileImageBase64").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profileImageBase64 = snapshot.getValue(String.class);
                if (profileImageBase64 == null || profileImageBase64.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "no photo to delete", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Hapus string Base64 dari Firebase Realtime Database
                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("profileImageBase64", null); // Set ke null untuk menghapus field
                userRef.updateChildren(userMap).addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "photo deleted", Toast.LENGTH_SHORT).show();
                    imageProfile.setImageResource(R.drawable.def_profile_img);
                    uploadText.setText(R.string.upload_profile_picture);
                    // Set photo URL di Firebase Auth menjadi null
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(null).build();
                    currentUser.updateProfile(profileUpdates);
                }).addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "failed to delete photo: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "failed to load photo: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // --- Helper Methods untuk Konversi Base64 ---

    /**
     * Mengkonversi Bitmap menjadi Base64 string.
     *
     * @param bitmap Bitmap yang akan dikonversi.
     * @return String Base64 dari bitmap, atau null jika gagal.
     */
    private String encodeBitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) return null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Kompres gambar dengan format JPEG dan kualitas 70% (bisa disesuaikan)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Mengkonversi Base64 string menjadi Bitmap.
     *
     * @param base64String String Base64 yang akan dikonversi.
     * @return Bitmap dari string Base64, atau null jika gagal.
     */
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