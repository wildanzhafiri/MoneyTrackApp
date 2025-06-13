package com.example.moneytrackapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionRepository {

    private static final List<Transaction> transactions = new ArrayList<>();

    // Tambahkan satu transaksi
    public static void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    // Perbarui transaksi berdasarkan ID
    public static void updateTransaction(Transaction updated) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).id.equals(updated.id)) {
                transactions.set(i, updated);
                break;
            }
        }
    }

    // Hapus transaksi berdasarkan ID
    public static void removeTransactionById(String transactionId) {
        transactions.removeIf(t -> t.id.equals(transactionId));
    }

    // Ambil semua transaksi
    public static List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions); // Kembalikan salinan
    }

    // Ambil transaksi terbaru (misalnya 5 terakhir)
    public static List<Transaction> getLatestTransactions(int count) {
        int size = transactions.size();
        if (size <= count) {
            return new ArrayList<>(transactions);
        } else {
            return new ArrayList<>(transactions.subList(size - count, size));
        }
    }

    // Reset seluruh daftar transaksi
    public static void clear() {
        transactions.clear();
    }

    // Sinkronisasi data dari Firebase
    public static void syncFromFirebase(List<Transaction> firebaseData) {
        transactions.clear();
        transactions.addAll(firebaseData);
    }
}
