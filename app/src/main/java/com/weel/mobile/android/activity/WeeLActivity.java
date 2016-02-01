package com.weel.mobile.android.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.weel.mobile.android.R;
import com.weel.mobile.android.model.User;
import com.weel.mobile.android.model.Vehicle;
import com.weel.mobile.android.resource.ApplicationResources;

import java.util.ArrayList;

import static com.weel.mobile.android.account.Account.AUTHTOKEN_TYPE_API_ACCESS;

/**
 * Created by jeremy.beckman on 2015-10-06.
 */
public abstract class WeeLActivity extends FragmentActivity {
    public static final String EXTRA_USER_VEHICLE = "com.weel.mobile.android.extras.USER_VEHICLE";
    public static final String EXTRA_USER = "com.weel.mobile.android.extras.USER";
    public static final String RESULT_CODE = "com.weel.mobile.android.intent.RESULT_CODE";

    protected Account account;
    protected String authToken;
    protected User user;
    protected ArrayList<Vehicle> vehicles;
    protected AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new WeeLExceptionHandler(this));

        accountManager = AccountManager.get(getApplicationContext());
    }

    protected void showSingleButtonAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(getString(R.string.alert_dialog_single_button_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create();
        builder.show();
    }

    protected abstract void callRemoteAPI(String remoteUri, Bundle params);

    protected void addAccount(AccountManager accountManager, AccountManagerCallback addAccountCallback) {
        Bundle options = new Bundle();
        options.putString(ApplicationResources.RESOURCE_API_URL, getString(R.string.api_url));
        accountManager.addAccount(getString(R.string.app_account_type), AUTHTOKEN_TYPE_API_ACCESS, null, options, this, addAccountCallback, null);
    }

    protected AccountManagerFuture<Bundle> getAuthToken(AccountManager accountManager, @Nullable AccountManagerCallback getAuthCallback) {
        Bundle options = new Bundle();
        options.putString(ApplicationResources.RESOURCE_API_URL, getString(R.string.api_url));
        return accountManager.getAuthToken(account, AUTHTOKEN_TYPE_API_ACCESS, options, this, getAuthCallback, null);
    }

    protected void invalidateToken(AccountManager accountManager) {
        accountManager.invalidateAuthToken(getString(R.string.app_account_type), authToken);
    }

    protected void startActivity(Context context, Class aClass, @Nullable Bundle options) {
        Intent intent = new Intent(context, aClass);
        intent.putExtras(options);
        startActivity(intent);
    }

    protected abstract void setView();
}
