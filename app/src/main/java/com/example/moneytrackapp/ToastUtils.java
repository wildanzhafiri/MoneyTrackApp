package com.example.moneytrackapp;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast toast;
    public static void showStaticToast(Context context, String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
