package com.example.moneytrackapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        // Button Edit
        holder.btnEdit.setOnClickListener(v -> {
            holder.etAmount.setEnabled(true);
            holder.etAmount.setFocusable(true);
            holder.etAmount.setFocusableInTouchMode(true);
            holder.etAmount.requestFocus();

            holder.btnSave.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.GONE); // jika ingin sembunyikan icon edit

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(holder.etAmount, InputMethodManager.SHOW_IMPLICIT);
        });

        // Button Save
        holder.btnSave.setOnClickListener(v -> {
            String input = holder.etAmount.getText().toString().trim();

            if (!input.isEmpty()) {
                try {
                    int newAmount = Integer.parseInt(input);
                    item.setAmount(newAmount); // simpan ke model
                    Toast.makeText(context, "Data disimpan", Toast.LENGTH_SHORT).show();
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

        // Button Delete
        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                items.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
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
        }
    }
}
