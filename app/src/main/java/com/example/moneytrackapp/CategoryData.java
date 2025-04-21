package com.example.moneytrackapp;

import java.util.ArrayList;
import java.util.List;

public class CategoryData {
    private static final List<Category> categories = new ArrayList<>();

    static {
        categories.add(new Category("Groceries", R.drawable.ic_groceries));
        categories.add(new Category("Gifts", R.drawable.ic_gifts));
        categories.add(new Category("Bar & Cafe", R.drawable.ic_cafe));
        categories.add(new Category("Health", R.drawable.ic_health));
        categories.add(new Category("Commute", R.drawable.ic_transportation));
        categories.add(new Category("Electronics", R.drawable.ic_electronics));
        categories.add(new Category("Laundry", R.drawable.ic_laundry));
        categories.add(new Category("Liquor", R.drawable.ic_liquor));
        categories.add(new Category("Restaurant", R.drawable.ic_restaurant));
    }

    public static List<Category> getCategories() {
        return categories;
    }

    public static void addCategory(Category category) {
        categories.add(category);
    }

    public static void updateCategory(String oldName, String newName, int newIconRes) {
        for (Category category : categories) {
            if (category.getName().equals(oldName)) {
                category.setName(newName);
                category.setIconRes(newIconRes);
                break;
            }
        }
    }

    public static void deleteCategory(String name) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getName().equals(name)) {
                categories.remove(i);
                break;
            }
        }
    }
}
