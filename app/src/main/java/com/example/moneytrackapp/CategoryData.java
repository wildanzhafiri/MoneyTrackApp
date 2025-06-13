package com.example.moneytrackapp;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CategoryData {
    private static final List<Category> categories = new ArrayList<>();

    public interface OnDataLoaded {
        void onLoaded();
    }

    public static List<Category> getCategories() {
        return categories;
    }

    public static void loadCategories(OnDataLoaded callback) {
        FirebaseHelper.fetchCategories(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Category category = data.getValue(Category.class);
                    if (category != null) {
                        category.setId(data.getKey());
                        categories.add(category);
                    }
                }

                categories.sort(Comparator.comparingInt(Category::getOrderIndex));
                callback.onLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onLoaded();
            }
        });
    }


    public static void addCategory(Category category) {
        category.setOrderIndex(categories.size());

        String id = FirebaseHelper.getUserCategoryRef().push().getKey();
        if (id != null) {
            category.setId(id);
            FirebaseHelper.addCategory(category);
        }
    }

    public static void updateCategory(String id, String newName, int newIconRes) {
        for (Category cat : categories) {
            if (cat.getId().equals(id)) {
                cat.setName(newName);
                cat.setIconRes(newIconRes);
                FirebaseHelper.updateCategoryById(id, cat);
                break;
            }
        }
    }

    public static void deleteCategory(String id) {
        FirebaseHelper.deleteCategoryById(id);
    }

    public static void setCategories(List<Category> newList) {
        categories.clear();
        categories.addAll(newList);
    }
}
