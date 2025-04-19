package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EditTransactionActivity extends AppCompatActivity {

    private Spinner spinner;
    private RecyclerView categoryRecycler;
    private Transaction originalTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        EditText etCategory = findViewById(R.id.et_category);
        EditText etAmount = findViewById(R.id.et_amount);
        EditText etDesc = findViewById(R.id.et_description);
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

        String date = getIntent().getStringExtra("date");
        String amount = getIntent().getStringExtra("amount");
        String category = getIntent().getStringExtra("category");
        String desc = getIntent().getStringExtra("description"); // optional

        etCategory.setText(category);
        etAmount.setText(amount.replace("Rp ", ""));
        etDesc.setText(desc != null ? desc : "");

        List<Transaction> list = TransactionRepository.getAllTransactions();
        for (Transaction t : list) {
            if (t.date.equals(date) && t.amount.equals(amount) && t.category.equals(category)) {
                originalTransaction = t;
                break;
            }
        }

        Button btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(v -> {
            if (originalTransaction == null) {
                Toast.makeText(this, "Transaction not found", Toast.LENGTH_SHORT).show();
                return;
            }

            String newCategory = etCategory.getText().toString();
            String newAmount = "Rp " + etAmount.getText().toString();
            String newDesc = etDesc.getText().toString();
            String type = spinner.getSelectedItem().toString();

            int colorResId = type.equals("Income") ?
                    R.drawable.category_label_background_green :
                    R.drawable.category_label_background;

            originalTransaction.category = newCategory;
            originalTransaction.amount = newAmount;
            originalTransaction.description = newDesc;
            originalTransaction.colorResId = colorResId;

            Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updated", true);
            Intent intent = new Intent(this, TransactionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        Button btnManageCategory = findViewById(R.id.manage_category_button);
        btnManageCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageCategory.class);
            startActivity(intent);
        });

        BottomNavbarView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setActiveIcon(R.id.add_transaction);
    }
}
