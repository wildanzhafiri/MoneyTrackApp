package com.example.moneytrackapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackapp.EditWishlistItemActivity;
import com.example.moneytrackapp.R;
import com.example.moneytrackapp.model.WishlistItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private final ArrayList<WishlistItem> itemList;

    public WishlistAdapter(ArrayList<WishlistItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        WishlistItem item = itemList.get(position);
        Context context = holder.itemView.getContext();

        holder.textName.setText(item.getName());
        holder.textPrice.setText(String.format("Rp %.0f", item.getPrice()));
        holder.textNotes.setText(item.getNotes());

        String base64 = item.getImageBase64();
        if (base64 != null && !base64.isEmpty()) {
            byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            holder.imageView.setImageBitmap(bitmap);
        } else {
            holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditWishlistItemActivity.class);
            intent.putExtra("id", item.getId());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete \"" + item.getName() + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseDatabase.getInstance("https://moneytrackapp-56fdd-default-rtdb.asia-southeast1.firebasedatabase.app")
                                .getReference("users")
                                .child(uid)
                                .child("wishlist")
                                .child(item.getId())
                                .removeValue()
                                .addOnSuccessListener(unused -> Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textPrice, textNotes;
        ImageView imageView;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textPrice = itemView.findViewById(R.id.textPrice);
            textNotes = itemView.findViewById(R.id.textNotes);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
