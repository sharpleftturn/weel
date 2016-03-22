package com.weel.mobile.android.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.weel.mobile.R;
import com.weel.mobile.android.model.User;
import com.weel.mobile.android.resource.ApplicationResources;
import com.weel.mobile.android.service.AuthenticationService;
import com.weel.mobile.android.util.Validator;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.weel.mobile.android.account.Account.AUTHTOKEN_TYPE_API_ACCESS;
/**
 * Created by jeremy.beckman on 2015-10-07.
 */
public class LoginActivity extends AccountAuthenticatorActivity {
    public static final String EXTRA_ACCOUNT_KEY = "com.weel.mobile.android.extras.loginActivity.ACCOUNT_KEY";
    public static final String EXTRA_USER_DATA = "com.weel.mobile.android.extras.loginActivity.USER_DATA";

    private boolean isEmailValid;
    private boolean isPasswordValid;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_signin);

        addListeners();
    }

    private void addListeners() {
        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                submit();
            }
        });

        Button signupButton = (Button) findViewById(R.id.signup);
        signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                Bundle data = new Bundle();
                data.putInt(WeeLActivity.RESULT_CODE, ApplicationResources.REQUEST_SIGNUP);

                intent.putExtras(data);

                setAccountAuthenticatorResult(data);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void submit() {
        final String username = ((TextView) findViewById(R.id.username)).getText().toString();
        final String pass = ((TextView) findViewById(R.id.password)).getText().toString();
        final String accountType = getString(R.string.app_account_type);
        final String url = getString(R.string.api_url);

        Bundle params = new Bundle();
        params.putString(AccountManager.KEY_ACCOUNT_NAME, username);
        params.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        params.putString(EXTRA_ACCOUNT_KEY, pass);
        params.putString(ApplicationResources.RESOURCE_API_URL, url);

        AsyncTask<Bundle, Void, Intent> task = new LoginWorkerTask().execute(params);

        try {
            Intent intent = task.get();

            Bundle data = intent.getExtras();

            setAccountAuthenticatorResult(data);
            setResult(RESULT_OK, intent);
        } catch (ExecutionException ee) {

        } catch (InterruptedException ie) {

        }
    }

    private void finishSignIn(Bundle data) {
        String accountName = data.getString(AccountManager.KEY_ACCOUNT_NAME);
        String accountKey = data.getString(AccountManager.KEY_PASSWORD);
        String accountType = data.getString(AccountManager.KEY_ACCOUNT_TYPE);

        final Account account = new Account(accountName, accountType);

        AccountManager accountManager = AccountManager.get(getApplicationContext());
        Account[] accounts = accountManager.getAccountsByType(getString(R.string.app_account_type));
        if (accounts.length == 0) {
            String authtoken = data.getString(AccountManager.KEY_AUTHTOKEN);

            accountManager.addAccountExplicitly(account, accountKey, null);
            accountManager.setAuthToken(account, AUTHTOKEN_TYPE_API_ACCESS, authtoken);
        } else {
            accountManager.setPassword(account, accountKey);
        }

        data.putParcelable(AccountManager.KEY_ACCOUNTS, account);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
        intent.putExtra(AccountManager.KEY_PASSWORD, accountKey);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountManager.KEY_ACCOUNTS, account);

        setAccountAuthenticatorResult(data);
        setResult(RESULT_OK, intent);

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);

        finish();
    }

    private class LoginWorkerTask extends AsyncTask<Bundle, Void, Intent> {
        String authToken;
        String accountType;

        final AuthenticationService service = new AuthenticationService();

        public LoginWorkerTask() {

        }

        @Override
        protected Intent doInBackground(Bundle... params) {
            Bundle param = params[0];
            String url = getString(R.string.api_url) + getString(R.string.api_authenticate_uri);
            String username = param.getString(AccountManager.KEY_ACCOUNT_NAME);
            String key = param.getString(EXTRA_ACCOUNT_KEY);
            accountType = param.getString(AccountManager.KEY_ACCOUNT_TYPE);

            Bundle data = new Bundle();

            try {
                User user = service.authenticate(url, username, key);

                if (user != null) {
                    authToken = user.getSession().getSession();

                    data.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                    data.putString(AccountManager.KEY_PASSWORD, key);
                    data.putSerializable(EXTRA_USER_DATA, user);
                }
            } catch (IOException ioe) {

            }

            final Intent results = new Intent();
            results.putExtras(data);

            return results;
        }

        @Override
        protected void onPostExecute(Intent results) {
            Bundle data = results.getExtras();

            if (service.hasError()) {
                showSingleButtonAlert(service.getErrorMessage());
            } else {
                finishSignIn(data);
            }
        }
    }

    private void addValidators() {
        TextView usernameView = (TextView) findViewById(R.id.username);
        usernameView.addTextChangedListener(new Validator(usernameView) {
            @Override
            public void validate(TextView textView, String text) {
                String pattern = "(.*)@(.*)\\.[a-zA-Z]{2,3}";
                isEmailValid = text.matches(pattern);
            }
        });

        TextView passwordView = (TextView) findViewById(R.id.password);
        passwordView.addTextChangedListener(new Validator(passwordView) {
            @Override
            public void validate(TextView textView, String text) {
                String pattern = "^[a-zA-Z0-9@!#$%&*?]{6,}";
                isPasswordValid = text.matches(pattern);
            }
        });
    }

    private void showSingleButtonAlert(String message) {
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
}
