package com.example.moneytrackapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHelper {

    private static String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static DatabaseReference getUserCategoryRef() {
        return FirebaseDatabase.getInstance("https://moneytrackapp-56fdd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")
                .child(getCurrentUserId())
                .child("categories");
    }

    public static void addCategory(Category category) {
        String id = FirebaseDatabase.getInstance("https://moneytrackapp-56fdd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users")
                .child(getCurrentUserId())
                .child("categories")
                .push()
                .getKey();

        if (id != null) {
            category.setId(id);
            getUserCategoryRef().child(id).setValue(category);
        }
    }

    public static void updateCategoryById(String id, Category category) {
        getUserCategoryRef().child(id).setValue(category);
    }

    public static void deleteCategoryById(String id) {
        getUserCategoryRef().child(id).removeValue();
    }

    public static void fetchCategories(ValueEventListener listener) {
        getUserCategoryRef().addValueEventListener(listener);
    }

    public static void fetchOnce(ValueEventListener listener) {
        getUserCategoryRef().addListenerForSingleValueEvent(listener);
    }
}
