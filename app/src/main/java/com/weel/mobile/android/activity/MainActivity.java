package com.weel.mobile.android.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.weel.mobile.R;
import com.weel.mobile.android.config.Constants;
import com.weel.mobile.android.model.User;
import com.weel.mobile.android.model.Vehicle;
import com.weel.mobile.android.resource.ApplicationResources;
import com.weel.mobile.android.service.AccountService;
import com.weel.mobile.android.service.VehicleService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by jeremy.beckman on 2015-10-06.
 */
public class MainActivity extends WeeLActivity {

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SignupActivity.REQUEST_CODE) {
            authenticate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_splash);
        authenticate();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void callRemoteAPI(String uri, Bundle params) {

    }

    private void authenticate() {
        Account[] accounts = accountManager.getAccountsByType(getString(R.string.app_account_type));

        if (accounts.length > 0) {
            account = accounts[0];
            getAuthToken(accountManager, getAuthCallback);
        } else {
            addAccount(accountManager, addAccountCallback);
        }
    }

    private void startSignupActivity() {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, SignupActivity.REQUEST_CODE);
    }

    final AccountManagerCallback<Bundle> getAuthCallback = new AccountManagerCallback<Bundle>() {

        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            try {
                Bundle bundle = future.getResult();
                authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);

                if (authToken == null) {
                    showSingleButtonAlert(getString(R.string.authentication_error));
                } else {
                    setView();
                }
            } catch (OperationCanceledException e) {

            } catch (IOException e) {

            } catch (AuthenticatorException e) {
                Log.d("exception", e.getMessage());
            }
        }
    };

    final AccountManagerCallback<Bundle> addAccountCallback = new AccountManagerCallback<Bundle>() {

        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            try {
                Bundle bundle = future.getResult();
                authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);

                if (bundle.getInt(RESULT_CODE) == ApplicationResources.REQUEST_SIGNUP) {
                    startSignupActivity();
                    return;
                }

                if (authToken == null) {
                    showSingleButtonAlert(getString(R.string.authentication_error));
                } else {
                    setView();
                }
            } catch (OperationCanceledException e) {

            } catch (IOException e) {

            } catch (AuthenticatorException e) {

            }
        }
    };

    @Override
    protected void setView() {
        AsyncTask<String, Void, User> getUserTask = new GetUserWorkerTask().execute(authToken);

        try {
            user = getUserTask.get();

            if (user != null) {
                AsyncTask<Void, Void, ArrayList<Vehicle>> getVehicleTask = new GetVehicleWorkerTask().execute();
                vehicles = getVehicleTask.get();

                Vehicle vehicle = null;

                if (vehicles != null && vehicles.size() > 0) {
                    for (Vehicle item : vehicles) {
                        if (item.getActive()) {
                            vehicle = item;
                        }
                    }
                    startDashboard(vehicle);
                } else {
                    startAddVehicle();
                }
            }
        } catch (ExecutionException ee) {

        } catch (InterruptedException ie) {

        } catch (IOException ioe) {
            showSingleButtonAlert(ioe.getMessage());
        }
    }

    private void startDashboard(Vehicle vehicle) throws IOException {
        if (vehicle == null) {
            throw new IOException(getString(R.string.no_vehicle_exception_message));
        }

        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Bundle data = new Bundle();
        data.putSerializable(Constants.VEHICLE_DATA, vehicle);
        data.putSerializable(Constants.USER_DATA, user);
        data.putString(AccountManager.KEY_AUTHTOKEN, authToken);

        intent.putExtras(data);
        startActivity(intent);

        finish();
    }

    private void startAddVehicle() {
        Intent intent = new Intent(this, AddVehicleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Bundle data = new Bundle();
        data.putSerializable(AddVehicleActivity.EXTRA_USERDATA, user);
        data.putString(AddVehicleActivity.EXTRA_AUTHTOKEN, authToken);

        intent.putExtras(data);
        startActivity(intent);

        finish();
    }

    private class GetUserWorkerTask extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... params) {
            String token = params[0];
            String url = getString(R.string.api_url) + getString(R.string.api_user_uri);

            AccountService accountService = new AccountService();
            User result = accountService.getUser(url, token);

            return result;
        }
    }

    private class GetVehicleWorkerTask extends AsyncTask<Void, Void, ArrayList<Vehicle>> {

        @Override
        protected ArrayList<Vehicle> doInBackground(Void... params) {
            String url = getString(R.string.api_url) + getString(R.string.api_user_vehicles_uri);

            VehicleService vehicleService = new VehicleService();
            ArrayList<Vehicle> results = vehicleService.getUserVehicles(url, authToken);

            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<Vehicle> vehicles) {

        }
    }
}
