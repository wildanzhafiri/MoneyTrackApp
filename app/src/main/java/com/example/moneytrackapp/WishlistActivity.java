package com.example.moneytrackapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackapp.adapter.WishlistAdapter;
import com.example.moneytrackapp.model.WishlistItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WishlistAdapter adapter;
    private ArrayList<WishlistItem> itemList = new ArrayList<>();
    private ImageButton buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        recyclerView = findViewById(R.id.recyclerViewWishlist);
        buttonAdd = findViewById(R.id.buttonAddWishlist);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WishlistAdapter(itemList);
        recyclerView.setAdapter(adapter);

        buttonAdd.setOnClickListener(v -> {
            Intent intent = new Intent(WishlistActivity.this, EditWishlistItemActivity.class);
            startActivity(intent);
        });

        loadWishlistItems();
    }

    private void loadWishlistItems() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference wishlistRef = FirebaseDatabase.getInstance("https://moneytrackapp-56fdd-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users")
                .child(user.getUid())
                .child("wishlist");

        wishlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    WishlistItem item = itemSnapshot.getValue(WishlistItem.class);
                    if (item != null) {
                        itemList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WishlistActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
