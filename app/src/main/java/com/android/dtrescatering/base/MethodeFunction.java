package com.android.dtrescatering.base;

import android.content.Context;
import android.widget.Toast;

public class MethodeFunction {
    public static void longToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void shortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
