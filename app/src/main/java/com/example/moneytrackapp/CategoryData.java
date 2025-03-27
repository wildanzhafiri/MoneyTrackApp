package com.example.moneytrackapp;

import java.util.ArrayList;
import java.util.List;

public class CategoryData {
    public static List<Category> getCategories() {
        List<Category> list = new ArrayList<>();
        list.add(new Category("Groceries", R.drawable.ic_groceries));
        list.add(new Category("Gifts", R.drawable.ic_gifts));
        list.add(new Category("Bar & Cafe", R.drawable.ic_cafe));
        list.add(new Category("Health", R.drawable.ic_health));
        list.add(new Category("Commute", R.drawable.ic_transportation));
        list.add(new Category("Electronics", R.drawable.ic_electronics));
        list.add(new Category("Laundry", R.drawable.ic_laundry));
        list.add(new Category("Liquor", R.drawable.ic_liquor));
        list.add(new Category("Restaurant", R.drawable.ic_restaurant));
        return list;
    }
}
