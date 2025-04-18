package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private Spinner spinner;
    private RecyclerView categoryRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        EditText etCategory = findViewById(R.id.et_category);

        spinner = findViewById(R.id.spinner_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.transaction_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        categoryRecycler = findViewById(R.id.categoryRecyclerView);
        categoryRecycler.setLayoutManager(new GridLayoutManager(this, 3));

        CategoryGridAdapter categoryAdapter = new CategoryGridAdapter(this, CategoryData.getCategories());
        categoryRecycler.setAdapter(categoryAdapter);

        categoryAdapter.setOnCategoryClickListener(categoryName -> {
            etCategory.setText(categoryName);
        });

        Button manageCategoryButton = findViewById(R.id.manage_category_button);
        manageCategoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageCategory.class);
            startActivity(intent);
        });

        Button manageContinueButton = findViewById(R.id.btn_continue);
        manageContinueButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TransactionActivity.class);
            EditText etAmount = findViewById(R.id.et_amount);
            EditText etDesc = findViewById(R.id.et_description);
            Spinner spinnerType = findViewById(R.id.spinner_type);

            String category = etCategory.getText().toString();
            String amount = "Rp " + etAmount.getText().toString();
            String desc = etDesc.getText().toString();
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            String type = spinnerType.getSelectedItem().toString();

            int colorResId = type.equals("Income") ?
                    R.drawable.category_label_background_green :
                    R.drawable.category_label_background;

            Transaction newTransaction = new Transaction(category, amount, desc, date, colorResId);
            TransactionRepository.addTransaction(newTransaction);

            startActivity(intent);
        });

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.add_transaction);
    }
}
