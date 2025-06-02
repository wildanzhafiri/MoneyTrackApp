package com.example.moneytrackapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackapp.R;
import com.example.moneytrackapp.models.Wallet;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.WalletViewHolder> {

    private List<Wallet> walletList;
    private OnItemClickListener listener;

    public WalletAdapter(List<Wallet> walletList) {
        this.walletList = walletList;
    }

    public interface OnItemClickListener {
        void onEditClick(Wallet wallet);
        void onDeleteClick(Wallet wallet);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet, parent, false);
        return new WalletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletViewHolder holder, int position) {
        Wallet wallet = walletList.get(position);
        holder.tvWalletName.setText(wallet.getName());
        holder.tvWalletCategory.setText(wallet.getCategory());

        // Format amount to currency
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        holder.tvWalletAmount.setText(formatRupiah.format(wallet.getAmount()));

        holder.btnEditWallet.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(wallet);
            }
        });

        holder.btnDeleteWallet.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(wallet);
            }
        });
    }

    @Override
    public int getItemCount() {
        return walletList.size();
    }

    public static class WalletViewHolder extends RecyclerView.ViewHolder {
        TextView tvWalletName, tvWalletCategory, tvWalletAmount;
        ImageButton btnEditWallet, btnDeleteWallet;

        public WalletViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWalletName = itemView.findViewById(R.id.tv_wallet_name);
            tvWalletCategory = itemView.findViewById(R.id.tv_wallet_category);
            tvWalletAmount = itemView.findViewById(R.id.tv_wallet_amount);
            btnEditWallet = itemView.findViewById(R.id.btn_edit_wallet);
            btnDeleteWallet = itemView.findViewById(R.id.btn_delete_wallet);
        }
    }

    // Metode untuk memperbarui data di adapter
    public void updateWallets(List<Wallet> newWalletList) {
        this.walletList = newWalletList;
        notifyDataSetChanged(); // Memberi tahu RecyclerView bahwa data telah berubah
    }
}