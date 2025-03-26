package com.example.moneytrackapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View;

import com.example.moneytrackapp.DashboardActivity;
import com.example.moneytrackapp.AddTransactionActivity;

public class BottomNavbarView extends LinearLayout {

    private ImageView iconHome, iconTransaction, iconAdd, iconCalculator, iconSetting;
    private View indicatorHome, indicatorTransaction, indicatorAdd, indicatorCalculator, indicatorSetting;

    public BottomNavbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.bottom_navbar, this, true);

        iconHome = findViewById(R.id.home);
        iconTransaction = findViewById(R.id.transaction);
        iconAdd = findViewById(R.id.add_transaction);
        iconCalculator = findViewById(R.id.calculator);
        iconSetting = findViewById(R.id.setting);

        indicatorHome = findViewById(R.id.indicator_home);
        indicatorTransaction = findViewById(R.id.indicator_transaction);
        indicatorAdd = findViewById(R.id.indicator_add_transaction);
        indicatorCalculator = findViewById(R.id.indicator_calculator);
        indicatorSetting = findViewById(R.id.indicator_setting);

        iconHome.setOnClickListener(v -> {
            if (!(context instanceof DashboardActivity)) {
                context.startActivity(new Intent(context, DashboardActivity.class));
            }
        });

        iconAdd.setOnClickListener(v -> {
            if (!(context instanceof AddTransactionActivity)) {
                context.startActivity(new Intent(context, AddTransactionActivity.class));
            }
        });

    }

    public void setActiveIcon(int id) {
        resetAll();

        if (id == R.id.home) {
            iconHome.setColorFilter(Color.parseColor("#000878"));
            indicatorHome.setVisibility(View.VISIBLE);
        } else if (id == R.id.transaction) {
            iconTransaction.setColorFilter(Color.parseColor("#000878"));
            indicatorTransaction.setVisibility(View.VISIBLE);
        } else if (id == R.id.add_transaction) {
            iconAdd.setColorFilter(Color.parseColor("#000878"));
            indicatorAdd.setVisibility(View.VISIBLE);
        } else if (id == R.id.calculator) {
            iconCalculator.setColorFilter(Color.parseColor("#000878"));
            indicatorCalculator.setVisibility(View.VISIBLE);
        } else if (id == R.id.setting) {
            iconSetting.setColorFilter(Color.parseColor("#000878"));
            indicatorSetting.setVisibility(View.VISIBLE);
        }
    }

    private void resetAll() {
        iconHome.setColorFilter(Color.WHITE);
        iconTransaction.setColorFilter(Color.WHITE);
        iconAdd.setColorFilter(Color.WHITE);
        iconCalculator.setColorFilter(Color.WHITE);
        iconSetting.setColorFilter(Color.WHITE);

        indicatorHome.setVisibility(View.GONE);
        indicatorTransaction.setVisibility(View.GONE);
        indicatorAdd.setVisibility(View.GONE);
        indicatorCalculator.setVisibility(View.GONE);
        indicatorSetting.setVisibility(View.GONE);
    }
}
