package com.example.moneytrackapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.moneytrackapp.model.WishlistItem;

@Database(entities = {WishlistItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    
    public abstract WishlistDao wishlistDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "app_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
