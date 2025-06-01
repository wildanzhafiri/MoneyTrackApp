package com.example.moneytrackapp;

import java.util.HashMap;
import java.util.Map;

public class TransactionMapper {
    public static Map<String, Object> toMap(TransactionFirebase tx) {
        Map<String, Object> map = new HashMap<>();
        if (tx.category != null) map.put("category", tx.category);
        if (tx.amount != null) map.put("amount", tx.amount);
        if (tx.description != null) map.put("description", tx.description);
        if (tx.imageBase64 != null) map.put("imageBase64", tx.imageBase64);
        return map;
    }
}
