package com.example.moneytrackapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private final Context context;
    private final List<BudgetModel> items;

    public BudgetAdapter(Context context, List<BudgetModel> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        BudgetModel item = items.get(position);

        holder.category.setText(item.getCategoryName());
        holder.etAmount.setText(String.valueOf(item.getAmount()));
        holder.progressText.setText(item.getProgress() + "%");
        holder.progressBar.setProgress(item.getProgress());
        holder.spent.setText("~Rp." + item.getSpent() + " spent");
        holder.left.setText("Rp." + item.getLeft() + " left");

        holder.btnSave.setVisibility(View.GONE);
        holder.etAmount.setEnabled(false);
        holder.etAmount.setFocusable(false);
        holder.etAmount.setFocusableInTouchMode(false);

        // Tampilkan gambar dari Base64 jika ada
        if (item.getImageBase64() != null) {
            try {
                byte[] imageBytes = Base64.decode(item.getImageBase64(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                holder.imagePreview.setImageBitmap(bitmap);
            } catch (Exception e) {
                holder.imagePreview.setImageResource(android.R.color.darker_gray);
            }
        } else if (item.getImageUri() != null) {
            holder.imagePreview.setImageURI(item.getImageUri());
        } else {
            holder.imagePreview.setImageResource(android.R.color.darker_gray);
        }

        // Tombol Upload Gambar
        holder.btnUploadImage.setOnClickListener(v -> {
            if (context instanceof BudgetingActivity) {
                ((BudgetingActivity) context).launchImagePicker(holder.getBindingAdapterPosition());
            }
        });

        // Tombol Edit
        holder.btnEdit.setOnClickListener(v -> {
            holder.etAmount.setEnabled(true);
            holder.etAmount.setFocusable(true);
            holder.etAmount.setFocusableInTouchMode(true);
            holder.etAmount.requestFocus();

            holder.btnSave.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.GONE);

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(holder.etAmount, InputMethodManager.SHOW_IMPLICIT);
        });

        // Tombol Simpan
        holder.btnSave.setOnClickListener(v -> {
            String input = holder.etAmount.getText().toString().trim();
            if (!input.isEmpty()) {
                try {
                    int newAmount = Integer.parseInt(input);
                    item.setAmount(newAmount); // update model lokal

                    // UPDATE FIREBASE
                    String budgetId = item.getBudgetId();
                    if (budgetId != null) {
                        FirebaseDatabase.getInstance("https://moneytrackapp-56fdd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("budgets")
                                .child(budgetId)
                                .setValue(item);
                    }

                    Toast.makeText(context, "Nominal berhasil disimpan", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Masukkan nominal valid", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            holder.etAmount.setEnabled(false);
            holder.etAmount.setFocusable(false);
            holder.etAmount.setFocusableInTouchMode(false);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnSave.setVisibility(View.GONE);

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(holder.etAmount.getWindowToken(), 0);

            notifyItemChanged(holder.getBindingAdapterPosition());
        });


        // Tombol Hapus
        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                BudgetModel itemToDelete = items.get(currentPosition);
                String budgetId = itemToDelete.getBudgetId();

                if (budgetId != null) {
                    FirebaseDatabase.getInstance("https://moneytrackapp-56fdd-default-rtdb.asia-southeast1.firebasedatabase.app/")
                            .getReference("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("budgets")
                            .child(budgetId)
                            .removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Item dihapus dari database", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView category, progressText, spent, left;
        EditText etAmount;
        ProgressBar progressBar;
        Button btnSave;
        ImageButton btnEdit, btnDelete;
        ImageView imagePreview;
        Button btnUploadImage;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.tv_category);
            etAmount = itemView.findViewById(R.id.et_amount);
            progressText = itemView.findViewById(R.id.tv_progress);
            spent = itemView.findViewById(R.id.tv_spent);
            left = itemView.findViewById(R.id.tv_left);
            progressBar = itemView.findViewById(R.id.progressBar);
            btnEdit = itemView.findViewById(R.id.edit_budget);
            btnSave = itemView.findViewById(R.id.btn_save);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            imagePreview = itemView.findViewById(R.id.image_preview);
            btnUploadImage = itemView.findViewById(R.id.btn_upload_image);
        }
    }
}
