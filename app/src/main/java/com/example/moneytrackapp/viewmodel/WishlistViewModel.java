package com.example.moneytrackapp.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.moneytrackapp.data.AppDatabase;
import com.example.moneytrackapp.data.WishlistDao;
import com.example.moneytrackapp.model.WishlistItem;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WishlistViewModel extends AndroidViewModel {
    private final WishlistDao wishlistDao;
    private final ExecutorService executorService;

    public WishlistViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        wishlistDao = database.wishlistDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<WishlistItem>> getAllItems() {
        return wishlistDao.getAllItems();
    }

    public LiveData<WishlistItem> getItemById(long id) {
        return wishlistDao.getItemById(id);
    }

    public void insert(WishlistItem item) {
        executorService.execute(() -> wishlistDao.insert(item));
    }

    public void update(WishlistItem item) {
        executorService.execute(() -> wishlistDao.update(item));
    }

    public void delete(WishlistItem item) {
        executorService.execute(() -> wishlistDao.delete(item));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
