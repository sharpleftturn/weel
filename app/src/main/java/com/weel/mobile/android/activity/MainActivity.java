package com.weel.mobile.android.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.weel.mobile.android.R;
import com.weel.mobile.android.adapter.DrawerListAdapter;
import com.weel.mobile.android.model.DrawerItem;
import com.weel.mobile.android.model.Photo;
import com.weel.mobile.android.model.User;
import com.weel.mobile.android.model.Vehicle;
import com.weel.mobile.android.resource.ApplicationResources;
import com.weel.mobile.android.service.AccountService;
import com.weel.mobile.android.service.AuthenticationService;
import com.weel.mobile.android.service.VehicleService;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by jeremy.beckman on 2015-10-06.
 */
public class MainActivity extends WeeLActivity {
    public static final String EXTRA_LOGOUT = "com.weel.mobile.android.extras.main.LOGOUT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //authenticate();
    }

    @Override
    public void onResume() {
        super.onResume();
        authenticate();
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
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    final AccountManagerCallback<Bundle> getAuthCallback = new AccountManagerCallback<Bundle>() {

        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            try {
                Bundle bundle = future.getResult();
                authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);

                if (authToken == null) {
                    showSingleButtonAlert(getString(R.string.authentication_error));
                    authenticate();
                } else {
                    setView();
                }
            } catch (OperationCanceledException e) {

            } catch (IOException e) {

            } catch (AuthenticatorException e) {

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
                    startDashboard();
                } else {
                    startAddVehicle();
                }
            }
        } catch (ExecutionException ee) {

        } catch (InterruptedException ie) {

        }
    }

    private void startDashboard() {
        addDrawer();

    }

    private void startAddVehicle() {
        Intent intent = new Intent(this, AddVehicleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Bundle data = new Bundle();
        data.putSerializable(AddVehicleActivity.EXTRA_USERDATA, user);
        data.putString(AddVehicleActivity.EXTRA_AUTHTOKEN, authToken);

        intent.putExtras(data);
        startActivity(intent);
    }

    private void addDrawer() {
        Resources resources = getResources();
        int width = (int) resources.getDimension(R.dimen.activity_drawer_width);

        DrawerLayout.LayoutParams layoutParams = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.MATCH_PARENT);
        layoutParams.width = width;
        layoutParams.gravity = Gravity.START;

        ListView drawerListView = new ListView(this);
        drawerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        drawerListView.setBackgroundColor(resources.getColor(R.color.weel_light_gray));

        setDrawerList(drawerListView);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.addView(drawerListView, layoutParams);
    }

    private void setDrawerList(ListView listView) {
        ArrayList<DrawerItem> drawerItems = new ArrayList<DrawerItem>();

        DrawerItem navItem = new DrawerItem();
        navItem.setLabel(getString(R.string.action_ask));
        navItem.setIcon(R.mipmap.ic_mechanic_black_24dp);
        navItem.setActivity(MechanicActivity.class);
        drawerItems.add(navItem);

        navItem = new DrawerItem();
        navItem.setLabel(getString(R.string.action_call));
        navItem.setIcon(R.mipmap.ic_roadside_assistance_black_28dp);
        navItem.setActivity(RoadsideActivity.class);
        drawerItems.add(navItem);

        navItem = new DrawerItem();
        navItem.setLabel(getString(R.string.action_deals));
        navItem.setIcon(R.mipmap.ic_weel_deals_black_24dp);
        navItem.setActivity(DealsActivity.class);
        drawerItems.add(navItem);

        navItem = new DrawerItem();
        navItem.setLabel(getString(R.string.action_maintenance));
        navItem.setIcon(R.mipmap.ic_maintenance_schedule_black_24dp);
        navItem.setActivity(MaintenanceActivity.class);
        drawerItems.add(navItem);

        addDrawerHeader(listView);
        addDrawerFooter(listView);

        listView.setAdapter(new DrawerListAdapter(this, drawerItems));
    }

    private void loadDrawerHeaderContent(ViewGroup header) {
        Vehicle vehicle = vehicles.get(0);

        if (vehicle.getPhoto() != null) {
            try {
                ImageView profileImageView = (ImageView) header.findViewById(R.id.profile_image);
                Photo vehiclePhoto = vehicle.getPhoto();

                URL url = new URL(vehiclePhoto.getFullUrl());

                AsyncTask<URL, Void, Bitmap> asyncTask = new BitmapWorkerTask(profileImageView).execute(url);
                Bitmap bitmap = asyncTask.get();

                if (bitmap != null) {
                    profileImageView.setImageBitmap(bitmap);
                }

                TextView profileTextView = (TextView) header.findViewById(R.id.profile_name);
                profileTextView.setText(vehicle.getName());
            } catch (IOException ioe) {
            } catch (ExecutionException ee) {
            } catch (InterruptedException ie) {
            }
        }
    }

    private void addDrawerHeader(ListView listView) {
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.view_drawer_header, listView, false);
        listView.addHeaderView(header);
        loadDrawerHeaderContent(header);
    }

    private void addDrawerFooter(ListView listView) {
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.view_drawer_footer, listView, false);
        TextView labelView = (TextView) footer.findViewById(R.id.footer_label);
        Drawable drawableLeft = getResources().getDrawable(R.mipmap.ic_exit_to_app_black_24dp);
        labelView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        listView.addFooterView(footer, null, false);
    }

    private void signOut() {
        if (authToken == null) {
            AccountManagerFuture<Bundle> future = getAuthToken(accountManager, null);

            try {
                Bundle bundle = future.getResult();
                authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            } catch (IOException ioe) {

            } catch (OperationCanceledException oce) {

            } catch (AuthenticatorException ae) {

            }
        }

        new SignOutWorkerTask().execute();
    }

    private class BitmapWorkerTask extends AsyncTask<URL, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewWeakReference;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(URL... params) {
            Bitmap bitmap = null;
            URL url = params[0];
            try {
                bitmap = loadBitmap(url);
            } catch (IOException ioe) {
            }
            return bitmap;
        }

        private Bitmap loadBitmap(URL url) throws IOException {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
            return bitmap;
        }
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

    private class SignOutWorkerTask extends AsyncTask<Void, Void, Bundle> {

        public SignOutWorkerTask() {

        }

        @Override
        protected Bundle doInBackground(Void... params) {
            Bundle results = null;

            AuthenticationService service = new AuthenticationService();

            try {
                Boolean isloggedOut = service.logout(getString(R.string.api_url), authToken);

                results = new Bundle();
                results.putBoolean(EXTRA_LOGOUT, isloggedOut);
                results.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            } catch (IOException ioe) {

            }
            return results;
        }

        @Override
        protected void onPostExecute(Bundle data) {
            Boolean isloggedOut = data.getBoolean(EXTRA_LOGOUT);
            if (isloggedOut) {
                finishSignOut(data);
            }
        }
    }

    private void finishSignOut(Bundle data) {
        Account[] accounts = accountManager.getAccountsByType(getString(R.string.app_account_type));
        if (accounts.length > 0) {
            authToken = data.getString(AccountManager.KEY_AUTHTOKEN);

            accountManager.invalidateAuthToken(getString(R.string.app_account_type), authToken);
            accountManager.clearPassword(accounts[0]);
        }

        getAuthToken(accountManager, getAuthCallback);
    }
}
