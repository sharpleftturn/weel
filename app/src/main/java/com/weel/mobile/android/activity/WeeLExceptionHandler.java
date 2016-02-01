package com.weel.mobile.android.activity;

import android.content.Context;

/**
 * Created by jeremy.beckman on 2016-01-28.
 */
public class WeeLExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context context;

    public WeeLExceptionHandler(Context context) {
        this.context = context;
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        String message = ex.getMessage();

    }
}
