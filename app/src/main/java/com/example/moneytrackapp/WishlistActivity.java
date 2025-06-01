package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moneytrackapp.adapter.WishlistAdapter;
import com.example.moneytrackapp.model.WishlistItem;
import com.example.moneytrackapp.viewmodel.WishlistViewModel;
import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {
    private static final int ADD_ITEM_REQUEST = 1;
    private static final int EDIT_ITEM_REQUEST = 2;
    
    private WishlistAdapter adapter;
    private WishlistViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        
        adapter = new WishlistAdapter();
        RecyclerView recyclerView = findViewById(R.id.wishlistRecyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<WishlistItem> dummyWishlist = new ArrayList<>();
        dummyWishlist.add(new WishlistItem("New Laptop", 1299.99, "MacBook Pro with 16GB RAM", ""));
        dummyWishlist.add(new WishlistItem("Gaming Console", 499.99, "PlayStation 5 with extra controller", ""));
        dummyWishlist.add(new WishlistItem("Wireless Headphones", 199.99, "Noise cancelling with 30hr battery life", ""));
        adapter.setItems(dummyWishlist);
        
        viewModel = new ViewModelProvider(this).get(WishlistViewModel.class);
        viewModel.getAllItems().observe(this, items -> {
            if (items == null || items.isEmpty()) {
                adapter.setItems(dummyWishlist);
            } else {
                adapter.setItems(items);
            }
        });

        adapter.setOnItemInteractionListener(new WishlistAdapter.OnItemInteractionListener() {
            @Override
            public void onItemClick(WishlistItem item) {
                Toast.makeText(WishlistActivity.this, "Selected: " + item.getName(), Toast.LENGTH_SHORT).show();
                
                Intent intent = new Intent(WishlistActivity.this, EditWishlistItemActivity.class);
                intent.putExtra(EditWishlistItemActivity.EXTRA_ID, item.getId());
                intent.putExtra(EditWishlistItemActivity.EXTRA_NAME, item.getName());
                intent.putExtra(EditWishlistItemActivity.EXTRA_PRICE, item.getPrice());
                intent.putExtra(EditWishlistItemActivity.EXTRA_NOTES, item.getNotes());
                intent.putExtra(EditWishlistItemActivity.EXTRA_IMAGE_URI, item.getImageUri());
                startActivityForResult(intent, EDIT_ITEM_REQUEST);
            }

            @Override
            public void onItemLongClick(View itemView, WishlistItem item) {
                new AlertDialog.Builder(WishlistActivity.this)
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        viewModel.delete(item);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        });

        Button addButton = findViewById(R.id.btnAddWishlistItem);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(WishlistActivity.this, EditWishlistItemActivity.class);
            startActivityForResult(intent, ADD_ITEM_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (data.getBooleanExtra("DELETE", false) && requestCode == EDIT_ITEM_REQUEST) {
                long id = data.getLongExtra(EditWishlistItemActivity.EXTRA_ID, -1);
                if (id != -1) {
                    WishlistItem itemToDelete = new WishlistItem();
                    itemToDelete.setId(id);
                    viewModel.delete(itemToDelete);
                }
                return;
            }

            String name = data.getStringExtra(EditWishlistItemActivity.EXTRA_NAME);
            double price = data.getDoubleExtra(EditWishlistItemActivity.EXTRA_PRICE, 0.0);
            String notes = data.getStringExtra(EditWishlistItemActivity.EXTRA_NOTES);
            String imageUri = data.getStringExtra(EditWishlistItemActivity.EXTRA_IMAGE_URI);
            
            if (requestCode == ADD_ITEM_REQUEST) {
                WishlistItem newItem = new WishlistItem(name, price, notes, imageUri);
                viewModel.insert(newItem);
            } else if (requestCode == EDIT_ITEM_REQUEST) {
                long id = data.getLongExtra(EditWishlistItemActivity.EXTRA_ID, -1);
                if (id != -1) {
                    WishlistItem updatedItem = new WishlistItem(name, price, notes, imageUri);
                    updatedItem.setId(id);
                    viewModel.update(updatedItem);
                }
            }
        }
    }
}
