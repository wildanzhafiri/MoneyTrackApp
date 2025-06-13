package com.example.moneytrackapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.moneytrackapp.model.WishlistItem;
import java.util.List;

@Dao
public interface WishlistDao {
    @Query("SELECT * FROM wishlist_items")
    LiveData<List<WishlistItem>> getAllItems();

    @Query("SELECT * FROM wishlist_items WHERE id = :id")
    LiveData<WishlistItem> getItemById(long id);

    @Insert
    void insert(WishlistItem item);

    @Update
    void update(WishlistItem item);

    @Delete
    void delete(WishlistItem item);
}
