package com.example.moneytrackapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat; // Tambahkan import ini
import androidx.core.content.ContextCompat; // Tambahkan import ini

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    CircleImageView imageProfile;
    TextView uploadText;
    TextInputEditText editUsername;
    Button saveButton, deletePhotoButton, downloadPhotoButton; // downloadPhotoButton sudah ada, tapi memastikan

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
        deletePhotoButton = findViewById(R.id.btn_delete_profile_picture);
        downloadPhotoButton = findViewById(R.id.btn_download_profile_picture);

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                selectedImageUri = result.getData().getData();
                imageProfile.setImageURI(selectedImageUri);
                uploadProfilePhoto();
            }
        });

        loadUserProfile();

        saveButton.setOnClickListener(view -> saveUserProfile());
        uploadText.setOnClickListener(view -> openImageChooser());
        imageProfile.setOnClickListener(view -> openImageChooser());
        deletePhotoButton.setOnClickListener(view -> deleteProfilePhoto());

        downloadPhotoButton.setOnClickListener(view -> {
            // Periksa izin penyimpanan untuk versi Android yang berbeda
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Untuk Android 10 (API 29) dan di atasnya, tidak perlu WRITE_EXTERNAL_STORAGE
                downloadProfilePhoto();
            } else {
                // Untuk Android 9 (API 28) dan di bawahnya, minta izin
                if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                } else {
                    downloadProfilePhoto();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadProfilePhoto();
            } else {
                Toast.makeText(this, "storing permission denied. cannot download image.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void loadUserProfile() {
        if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            editUsername.setText(currentUser.getDisplayName());
        } else {
            editUsername.setText("");
            // memuat username dari database jika display name di Firebase Auth kosong
            userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String dbUsername = snapshot.getValue(String.class);
                    if (dbUsername != null && !dbUsername.isEmpty()) {
                        editUsername.setText(dbUsername);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Log error for debugging
                    // android.util.Log.e("ProfileActivity", "Failed to load username from DB: " + error.getMessage());
                }
            });
        }

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
                        // Log error for debugging
                        // android.util.Log.e("ProfileActivity", "Failed to decode profile image Base64 during load.");
                    }
                } else {
                    imageProfile.setImageResource(R.drawable.def_profile_img);
                    uploadText.setText(R.string.upload_profile_picture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "failed to load image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                imageProfile.setImageResource(R.drawable.def_profile_img);
                uploadText.setText(R.string.upload_profile_picture);
                // Log error for debugging
                // android.util.Log.e("ProfileActivity", "Firebase error loading profile image: " + error.getMessage(), error.toException());
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

    private void uploadProfilePhoto() {
        if (selectedImageUri != null) {
            Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                String base64Image = encodeBitmapToBase64(bitmap);

                if (base64Image != null) {
                    // Simpan string Base64 ke Firebase Realtime Database
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("profileImageBase64", base64Image); // Simpan di field baru

                    userRef.updateChildren(userMap).addOnSuccessListener(aVoid -> {
                        Toast.makeText(ProfileActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                        uploadText.setText(R.string.upload_profile_picture);
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(null) // set null karena tidak ada URL
                                .build();
                        currentUser.updateProfile(profileUpdates);
                    }).addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to update profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(this, "Failed to encode image to Base64", Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProfilePhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_delete_confirmation, null); // Inflate layout kustom
        builder.setView(dialogView);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_delete_title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_delete_message);
        Button btnCancel = dialogView.findViewById(R.id.btn_delete_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_delete_confirm);

        // Set teks untuk dialog
        dialogTitle.setText(getString(R.string.delete_confirmation_title));
        dialogMessage.setText(R.string.delete_photo_message);

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> userRef.child("profileImageBase64").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profileImageBase64 = snapshot.getValue(String.class);
                if (profileImageBase64 == null || profileImageBase64.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "No image to delete.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); // Tutup dialog bahkan jika tidak ada foto
                    return;
                }

                // Hapus string Base64 dari Firebase Realtime Database
                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("profileImageBase64", null); // Set ke null untuk menghapus field
                userRef.updateChildren(userMap).addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Photo deleted successfully!", Toast.LENGTH_SHORT).show();
                    imageProfile.setImageResource(R.drawable.def_profile_img);
                    uploadText.setText(R.string.upload_profile_picture);
                    // Set photo URL di Firebase Auth menjadi null
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(null).build();
                    currentUser.updateProfile(profileUpdates);
                }).addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                // android.util.Log.e("ProfileActivity", "Firebase error deleting profile image: " + error.getMessage(), error.toException());
                dialog.dismiss();
            }
        }));
        dialog.show();
    }

    // --- Metode untuk mengunduh foto profil ---
    private void downloadProfilePhoto() {
        userRef.child("profileImageBase64").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profileImageBase64 = snapshot.getValue(String.class);
                if (profileImageBase64 == null || profileImageBase64.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "No image to download.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap bitmap = decodeBase64ToBitmap(profileImageBase64);
                if (bitmap != null) {
                    saveImageToGallery(bitmap);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to decode image.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load image: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProfileActivity", "Firebase error downloading profile image: " + error.getMessage(), error.toException());
            }
        });
    }

    // --- Metode untuk menyimpan gambar ke galeri ---
    private void saveImageToGallery(Bitmap bitmap) {
        OutputStream fos = null;
        Uri imageUri = null;
        String fileName = "profile_photo_" + System.currentTimeMillis() + ".jpg"; // Nama file unik

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Untuk Android 10 (API 29) dan di atasnya (Scoped Storage)
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "MoneyTrackApp"); // Simpan di folder khusus

                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (imageUri == null) {
                    throw new IOException("Failed to create new MediaStore record.");
                }
                fos = resolver.openOutputStream(imageUri);
            } else {
                // Untuk Android 9 (API 28) dan di bawahnya
                File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + File.separator + "MoneyTrackApp");
                if (!imagesDir.exists()) {
                    if (!imagesDir.mkdirs()) {
                        throw new IOException("Failed to create directory: " + imagesDir.getAbsolutePath());
                    }
                }
                File image = new File(imagesDir, fileName);
                fos = new FileOutputStream(image);
                imageUri = Uri.fromFile(image);
            }

            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos); // Kompres gambar
                fos.flush();
                Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_LONG).show();
                Log.d("ProfileActivity", "Image saved to: " + imageUri);
            } else {
                throw new IOException("Failed to get output stream.");
            }
        } catch (IOException e) {
            Toast.makeText(this, "Failed to save image to gallery: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("ProfileActivity", "Error saving image to gallery: " + e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e("ProfileActivity", "Error closing output stream: " + e.getMessage(), e);
                }
            }
            // Untuk Android 9 dan di bawahnya, perlu melakukan media scan agar gambar muncul di galeri
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && imageUri != null) {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(imageUri);
                sendBroadcast(mediaScanIntent);
            }
        }
    }


    private String encodeBitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) return null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
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