package com.example.moneytrackapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
    public static void removeTransaction(String date, String amount, String category) {
        Iterator<Transaction> iterator = transactions.iterator();
        while (iterator.hasNext()) {
            Transaction t = iterator.next();
            if (t.date.equals(date) && t.amount.equals(amount) && t.category.equals(category)) {
                iterator.remove();
                break;
            }
        }
    }


    public static void clearTransactions() {
        transactions.clear();
    }
}
