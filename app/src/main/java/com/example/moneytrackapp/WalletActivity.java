package com.example.moneytrackapp;

import android.content.Intent; // Tambahkan import ini untuk redirect ke LoginActivity
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackapp.adapters.WalletAdapter;
import com.example.moneytrackapp.models.Wallet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletActivity extends AppCompatActivity implements WalletAdapter.OnItemClickListener {

    private RecyclerView rvWallets;
    private WalletAdapter walletAdapter;
    private List<Wallet> walletList;
    private FloatingActionButton fabAddWallet;

    private FirebaseAuth mAuth;
    private DatabaseReference walletsRef;
    private FirebaseUser currentUser;

    // Tambahkan TextView untuk pesan "No Wallets"
    private TextView tvNoWalletsMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Handle jika user belum login
        if (currentUser == null) {
            Toast.makeText(this, "Authentication required. Please log in.", Toast.LENGTH_LONG).show();
            // Ganti LoginActivity.class dengan nama kelas aktivitas login kamu
            Intent intent = new Intent(WalletActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Bersihkan back stack
            startActivity(intent);
            finish();
            return;
        }

        // Inisialisasi DatabaseReference hanya jika currentUser tidak null
        walletsRef = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUser.getUid())
                .child("wallets");

        rvWallets = findViewById(R.id.rv_wallets);
        fabAddWallet = findViewById(R.id.fab_add_wallet);
        tvNoWalletsMessage = findViewById(R.id.tv_no_wallets);

        walletList = new ArrayList<>();
        walletAdapter = new WalletAdapter(walletList);
        walletAdapter.setOnItemClickListener(this);

        rvWallets.setLayoutManager(new LinearLayoutManager(this));
        rvWallets.setAdapter(walletAdapter);

        fabAddWallet.setOnClickListener(v -> showAddEditWalletDialog(null));
        loadWallets();
    }

    private void showAddEditWalletDialog(Wallet walletToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_wallet, null);
        builder.setView(dialogView);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        EditText etWalletName = dialogView.findViewById(R.id.et_wallet_name);
        EditText etWalletAmount = dialogView.findViewById(R.id.et_wallet_amount);
        Spinner spinnerWalletCategory = dialogView.findViewById(R.id.spinner_wallet_categories); // Perbaiki ID di sini!
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSave = dialogView.findViewById(R.id.btn_save);

        // Setup Spinner for categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.wallet_categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWalletCategory.setAdapter(adapter);


        boolean isEditMode = (walletToEdit != null);
        if (isEditMode) {
            dialogTitle.setText(R.string.edit_wallet);
            etWalletName.setText(walletToEdit.getName());
            // Gunakan String.valueOf() untuk double agar tidak ada masalah format
            etWalletAmount.setText(String.valueOf(walletToEdit.getAmount()));

            String category = walletToEdit.getCategory();
            if (category != null && !category.isEmpty()) {
                int spinnerPosition = adapter.getPosition(category);
                if (spinnerPosition != -1) { // Hanya set jika kategori ditemukan di adapter
                    spinnerWalletCategory.setSelection(spinnerPosition);
                } else {
                    // Jika kategori dari DB tidak ada di daftar spinner, bisa pilih item pertama atau "Other"
                    spinnerWalletCategory.setSelection(0); // Pilih item pertama
                }
            }
        } else {
            dialogTitle.setText(R.string.add_wallet);
        }

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etWalletName.getText().toString().trim();
            String amountStr = etWalletAmount.getText().toString().trim();
            String category = spinnerWalletCategory.getSelectedItem().toString(); // Get selected item from Spinner

            // Validasi yang lebih baik
            if (name.isEmpty()) {
                Toast.makeText(WalletActivity.this, "Wallet name cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (amountStr.isEmpty()) {
                Toast.makeText(WalletActivity.this, "Amount cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(WalletActivity.this, "Invalid amount format. Please enter a valid number.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEditMode) {
                updateWallet(walletToEdit.getId(), name, amount, category);
            } else {
                addWallet(name, amount, category);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void loadWallets() {
        walletsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("WalletActivity", "onDataChange triggered. Snapshot exists: " + snapshot.exists() + ", Children count: " + snapshot.getChildrenCount());
                walletList.clear(); // Bersihkan list setiap kali data berubah

                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    Log.d("WalletActivity", "No wallets found for this user. Displaying empty message.");
                    tvNoWalletsMessage.setVisibility(View.VISIBLE); // Tampilkan pesan kosong
                    rvWallets.setVisibility(View.GONE); // Sembunyikan RecyclerView
                } else {
                    tvNoWalletsMessage.setVisibility(View.GONE); // Sembunyikan pesan kosong
                    rvWallets.setVisibility(View.VISIBLE); // Tampilkan RecyclerView

                    for (DataSnapshot walletSnapshot : snapshot.getChildren()) {
                        Wallet wallet = walletSnapshot.getValue(Wallet.class);
                        if (wallet != null) {
                            // Penting: Pastikan ID dari snapshot key diset ke objek Wallet
                            wallet.setId(walletSnapshot.getKey());
                            walletList.add(wallet);
                            Log.d("WalletActivity", "Loaded wallet: " + wallet.getName() + " (ID: " + wallet.getId() + ")");
                        } else {
                            Log.w("WalletActivity", "Could not parse wallet data for key: " + walletSnapshot.getKey() + ". Data: " + walletSnapshot.getValue());
                        }
                    }
                }
                walletAdapter.updateWallets(walletList); // Selalu panggil ini agar adapter terupdate
                Log.d("WalletActivity", "Adapter updated. Total wallets in list: " + walletList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WalletActivity.this, "Failed to load wallets: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("WalletActivity", "Firebase Database Error: " + error.getMessage() + ", Code: " + error.getCode(), error.toException());
                // Jika ada error saat load, tampilkan pesan error dan sembunyikan RecyclerView
                tvNoWalletsMessage.setText(R.string.failed_to_load_wallets);
                tvNoWalletsMessage.setVisibility(View.VISIBLE);
                rvWallets.setVisibility(View.GONE);
            }
        });
    }

    private void addWallet(String name, double amount, String category) {
        DatabaseReference newWalletRef = walletsRef.push();
        String walletId = newWalletRef.getKey();
        if (walletId == null) {
            Toast.makeText(this, "Failed to generate wallet ID. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        Wallet newWallet = new Wallet(walletId, name, amount, category);

        newWalletRef.setValue(newWallet)
                .addOnSuccessListener(aVoid -> Toast.makeText(WalletActivity.this, "Wallet added successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(WalletActivity.this, "Failed to add wallet: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateWallet(String walletId, String name, double amount, String category) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("amount", amount);
        updates.put("category", category);
        // updates.put("id", walletId); // ID tidak perlu diupdate, karena ini adalah key di Firebase

        walletsRef.child(walletId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(WalletActivity.this, "Wallet updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(WalletActivity.this, "Failed to update wallet: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteWallet(Wallet walletToDelete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_delete_confirmation, null); // Inflate layout kustom
        builder.setView(dialogView);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_delete_title);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_delete_message);
        Button btnCancel = dialogView.findViewById(R.id.btn_delete_cancel);
        Button btnConfirm = dialogView.findViewById(R.id.btn_delete_confirm);

        // Set teks sesuai item yang akan dihapus
        dialogTitle.setText(getString(R.string.delete_confirmation_title));
        dialogMessage.setText("Are you sure you want to delete wallet '" + walletToDelete.getName() + "'?");

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            // Logika penghapusan saat tombol konfirmasi diklik
            walletsRef.child(walletToDelete.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(WalletActivity.this, "Wallet deleted successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(WalletActivity.this, "Failed to delete wallet: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            dialog.dismiss(); // Tutup dialog setelah aksi
        });

        dialog.show();
    }

    @Override
    public void onEditClick(Wallet wallet) {
        showAddEditWalletDialog(wallet);
    }

    @Override
    public void onDeleteClick(Wallet wallet) {
        deleteWallet(wallet);
    }
}