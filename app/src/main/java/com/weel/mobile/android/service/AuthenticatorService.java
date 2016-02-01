package com.weel.mobile.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.weel.mobile.android.account.AccountAuthenticator;

/**
 * Created by jeremy.beckman on 2015-10-06.
 */
public class AuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return new AccountAuthenticator(this).getIBinder();
    }
}
