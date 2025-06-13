package com.example.moneytrackapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneytrackapp.adapters.CurrencyAdapter;
import com.example.moneytrackapp.model.Currency;

import java.util.ArrayList;
import java.util.List;

public class CurrencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_currency);

        List<Currency> currencies = new ArrayList<>();
        currencies.add(new Currency("United States Dollar (USD)"));
        currencies.add(new Currency("Euro (EUR)"));
        currencies.add(new Currency("Japanese Yen (JPY)"));
        currencies.add(new Currency("British Pound (GBP)"));
        currencies.add(new Currency("Australian Dollar (AUD)"));
        currencies.add(new Currency("Indonesian Rupiah (IDR)"));

        RecyclerView rvCurrency = findViewById(R.id.rv_currency);
        rvCurrency.setLayoutManager(new LinearLayoutManager(this));

        CurrencyAdapter adapter = new CurrencyAdapter(currencies, this); // kirim context
        rvCurrency.setAdapter(adapter);
    }
}
