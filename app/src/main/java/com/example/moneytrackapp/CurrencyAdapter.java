package com.example.moneytrackapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {

    private final List<Currency> currencies;
    private final OnCheckboxClickListener listener;

    public interface OnCheckboxClickListener {
        void onCheckboxClick(int position, boolean isChecked);
    }

    public CurrencyAdapter(List<Currency> currencies, OnCheckboxClickListener listener) {
        this.currencies = currencies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_currency, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Currency currency = currencies.get(position);
        holder.bind(currency, position);
    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox cbCurrency;
        private final TextView tvCurrencyName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbCurrency = itemView.findViewById(R.id.cbCurrency);
            tvCurrencyName = itemView.findViewById(R.id.tvCurrencyName);
        }

        public void bind(Currency currency, int position) {
            tvCurrencyName.setText(currency.getName());
            cbCurrency.setChecked(currency.isChecked());
            cbCurrency.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onCheckboxClick(position, isChecked);
                }
            });
        }
    }
}