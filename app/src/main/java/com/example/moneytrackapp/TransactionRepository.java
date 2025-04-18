package com.example.moneytrackapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionRepository {
    private static final List<Transaction> transactions = new ArrayList<>();

    public static void addTransaction(Transaction transaction) {
        transactions.add(0, transaction);
    }

    public static List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public static List<Transaction> getLatestTransactions(int limit) {
        return transactions.subList(0, Math.min(limit, transactions.size()));
    }

    public static void clearTransactions() {
        transactions.clear();
    }
}
