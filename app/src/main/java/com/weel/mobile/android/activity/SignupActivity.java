package com.weel.mobile.android.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.weel.mobile.android.R;
import com.weel.mobile.android.model.User;
import com.weel.mobile.android.service.AuthenticationService;

import java.io.IOException;

import static com.weel.mobile.android.account.Account.AUTHTOKEN_TYPE_API_ACCESS;

/**
 * Created by jeremy.beckman on 2015-10-07.
 */
public class SignupActivity extends WeeLActivity {
    public static final String EXTRA_ADD_ACCOUNT_RESULTS = "com.weel.mobile.extras.signupActivity.ADD_ACCOUNT_RESULTS";
    public static final String EXTRA_USER_DATA = "com.weel.mobile.android.extras.signupActivity.USER_DATA";
    public static final String EXTRA_FULLNAME = "com.weel.mobile.extras.FULLNAME";
    public static final String EXTRA_EMAIL = "com.weel.mobile.extras.EMAIL";
    public static final String EXTRA_PASSWORD = "com.weel.mobile.extras.PASSWORD";
    public static final String EXTRA_COUPON_CODE = "com.weel.mobile.extras.COUPON_CODE";
    public static final String EXTRA_VERSION = "com.weel.mobile.extras.VERSION";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_signup);

        addToolbar();

        Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit(view);
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void submit(View view) {
        EditText fullNameEdit = (EditText) findViewById(R.id.fullname);
        EditText emailEdit = (EditText) findViewById(R.id.email);
        EditText passwordEdit = (EditText) findViewById(R.id.password);

        String fullname = fullNameEdit.getText().toString();
        String username = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        Bundle params = new Bundle();
        params.putString(EXTRA_FULLNAME, fullname);
        params.putString(EXTRA_EMAIL, username);
        params.putString(EXTRA_PASSWORD, password);
        params.putString(EXTRA_VERSION, getString(R.string.app_version));
        params.putString(EXTRA_COUPON_CODE, getString(R.string.app_coupon_code));

        AsyncTask<Bundle, Void, Bundle> asyncTask = new SignupWorkerTask().execute(params);

    }

    @Override
    protected void addToolbar() {
        super.addToolbar();
        toolbar.setTitle(R.string.title_activity_signup);
    }

    protected void callRemoteAPI(String remoteUri, Bundle params) {

    }

    private void finishSignUp(Intent intent) {
        Bundle data = intent.getBundleExtra(EXTRA_ADD_ACCOUNT_RESULTS);
        User user = (User) data.getSerializable(EXTRA_USER_DATA);

        authToken = user.getSession().getSession();
        String accountName = user.getEmail();
        String accountKey = data.getString(AccountManager.KEY_PASSWORD);
        String accountType = getString(R.string.app_account_type);

        account = new Account(accountName, accountType);

        AccountManager accountManager = AccountManager.get(getApplicationContext());
        accountManager.addAccountExplicitly(account, accountKey, null);
        accountManager.setAuthToken(account, AUTHTOKEN_TYPE_API_ACCESS, authToken);

        finish();
    }

    private class SignupWorkerTask extends AsyncTask<Bundle, Void, Bundle> {

        final AuthenticationService service = new AuthenticationService();

        public SignupWorkerTask() {

        }

        @Override
        protected Bundle doInBackground(Bundle... params) {
            Bundle data = params[0];

            String url = getString(R.string.api_url) + getString(R.string.api_signup_uri);
            String username = data.getString(EXTRA_EMAIL);
            String password = data.getString(EXTRA_PASSWORD);
            String name = data.getString(EXTRA_FULLNAME);
            String version = data.getString(EXTRA_VERSION);
            String coupon = data.getString(EXTRA_COUPON_CODE);

            try {
                User user = service.addAccount(url, name, username, password, version, coupon);

                data.putSerializable(EXTRA_USER_DATA, user);
                data.putString(AccountManager.KEY_PASSWORD, password);
            } catch (IOException ioe) {
            }

            return data;
        }

        @Override
        protected void onPostExecute(Bundle results) {
            if (service.hasError()) {
                showSingleButtonAlert(service.getErrorMessage());
            } else {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_ADD_ACCOUNT_RESULTS, results);
                finishSignUp(intent);
            }
        }
    }

    protected void setView() {

    }
}
