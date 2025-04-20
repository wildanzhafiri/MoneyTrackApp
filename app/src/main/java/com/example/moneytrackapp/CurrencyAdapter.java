package com.example.moneytrackapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {
    List<Currency> currencies;
    Context context;
    int selectedPosition = -1;

    public CurrencyAdapter(List<Currency> currencies, Context context) {
        this.currencies = currencies;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_currency, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Currency currency = currencies.get(position);
        holder.tvCurrencyName.setText(currency.getName());

        holder.rbCurrency.setChecked(position == selectedPosition);

        holder.rbCurrency.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();

            // Tampilkan toast langsung saat klik
            Toast.makeText(context, "Selected: " + currency.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return currencies.size(); // penting!
    }

    public Currency getSelectedCurrency() {
        if (selectedPosition != -1) {
            return currencies.get(selectedPosition);
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton rbCurrency;
        TextView tvCurrencyName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rbCurrency = itemView.findViewById(R.id.rbCurrency);
            tvCurrencyName = itemView.findViewById(R.id.tvCurrencyName);
        }
    }
}
