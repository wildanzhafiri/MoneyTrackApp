package com.example.moneytrackapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddCategoryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private RecyclerView iconRecyclerView;
    private EditText nameInput;
    private ImageView selectedIcon;
    private IconGridAdapter adapter;

    private List<Integer> defaultIconList;
    private List<String> uploadedIconUrls = new ArrayList<>();
    private int selectedIconRes = -1;
    private String selectedImageUrl = null;

    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.add_transaction);

        nameInput = findViewById(R.id.et_category_name);
        iconRecyclerView = findViewById(R.id.icon_recycler);
        selectedIcon = findViewById(R.id.selected_icon);

        storageRef = FirebaseStorage.getInstance().getReference("category_icons");

        defaultIconList = Arrays.asList(
                R.drawable.ic_groceries, R.drawable.ic_cafe, R.drawable.ic_electronics,
                R.drawable.ic_gifts, R.drawable.ic_laundry, R.drawable.ic_party,
                R.drawable.ic_liquor, R.drawable.ic_fuel, R.drawable.ic_maintenance,
                R.drawable.ic_education, R.drawable.ic_self_development, R.drawable.ic_money,
                R.drawable.ic_health, R.drawable.ic_transportation, R.drawable.ic_restaurant,
                R.drawable.ic_sport, R.drawable.ic_savings, R.drawable.ic_institute,
                R.drawable.ic_donate
        );

        adapter = new IconGridAdapter(this, defaultIconList, uploadedIconUrls);
        iconRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        iconRecyclerView.setAdapter(adapter);

        adapter.setOnIconSelectedListener((iconRes, imageUrl) -> {
            selectedIconRes = iconRes;
            selectedImageUrl = imageUrl;

            if (iconRes != -1) {
                selectedIcon.setImageResource(iconRes);
            } else if (imageUrl != null) {
                Glide.with(this).load(imageUrl).into(selectedIcon);
            }
        });

        Button btnUploadImage = findViewById(R.id.btn_upload_image);
        btnUploadImage.setOnClickListener(v -> openImagePicker());

        Button btnAddCategory = findViewById(R.id.btn_add_category);
        btnAddCategory.setOnClickListener(v -> saveCategory());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        String base64Image = convertImageToBase64(imageUri);
        if (base64Image == null) {
            ToastUtils.showStaticToast(this, "Failed to convert image");
            return;
        }

        uploadedIconUrls.add(base64Image);
        adapter.notifyDataSetChanged();

        selectedIconRes = -1;
        selectedImageUrl = base64Image;

        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        selectedIcon.setImageBitmap(decodedBitmap);

        ToastUtils.showStaticToast(this, "Image uploaded (Base64)!");
    }



    private void saveCategory() {
        String categoryName = nameInput.getText().toString().trim();

        if (categoryName.isEmpty() || (selectedIconRes == -1 && selectedImageUrl == null)) {
            ToastUtils.showStaticToast(this, "Please fill all fields");
            return;
        }

        Category newCategory = new Category();
        newCategory.setName(categoryName);
        newCategory.setOrderIndex(CategoryData.getCategories().size());

        if (selectedImageUrl != null) {
            newCategory.setIconRes(-1);
            newCategory.setIconUrl(selectedImageUrl);
        } else {
            newCategory.setIconRes(selectedIconRes);
        }


        CategoryData.addCategory(newCategory);
        ToastUtils.showStaticToast(this, "Category added successfully!");
        finish();
    }
}
