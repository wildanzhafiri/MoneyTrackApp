package com.example.moneytrackapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moneytrackapp.R;
import com.example.moneytrackapp.model.WishlistItem;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    public interface OnItemInteractionListener {
        void onItemClick(WishlistItem item);
        void onItemLongClick(View itemView, WishlistItem item);
    }

    private List<WishlistItem> items = new ArrayList<>();
    private OnItemInteractionListener listener;

    public WishlistAdapter() {
        this(new ArrayList<>());
    }

    public WishlistAdapter(List<WishlistItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    @Override
    public WishlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wishlist, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WishlistViewHolder holder, int position) {
        WishlistItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<WishlistItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setOnItemInteractionListener(OnItemInteractionListener listener) {
        this.listener = listener;
    }

    public class WishlistViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView nameText;
        private final TextView priceText;
        private final TextView notesText;

        public WishlistViewHolder(View view) {
            super(view);
            cardView = (MaterialCardView) view;
            nameText = view.findViewById(R.id.wishlistItemName);
            priceText = view.findViewById(R.id.wishlistItemPrice);
            notesText = view.findViewById(R.id.wishlistItemNotes);

            // Add click listener for editing
            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(items.get(position));
                }
            });

            // Keep existing long-click listener for deletion
            cardView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemLongClick(cardView, items.get(position));
                }
                return true;
            });
        }

        public void bind(WishlistItem item) {
            nameText.setText(item.getName());
            priceText.setText(String.valueOf(item.getPrice()));
            notesText.setText(item.getNotes());
        }
    }
}
