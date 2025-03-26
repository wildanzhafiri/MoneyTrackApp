package com.example.moneytrackapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context context;
    private List<Transaction> transactions;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction t = transactions.get(position);
        holder.category.setText(t.category);
        holder.amount.setText(t.amount);
        holder.description.setText(t.description);
        holder.date.setText(t.date);
        holder.category.setBackgroundResource(t.colorResId); // background label warna kategori
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView category, amount, description, date;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.labelCategory);
            amount = itemView.findViewById(R.id.labelAmount);
            description = itemView.findViewById(R.id.labelDescription);
            date = itemView.findViewById(R.id.labelDate);
        }
    }

    public void updateData(List<Transaction> newData) {
        this.transactions = newData;
        notifyDataSetChanged();
    }

}
