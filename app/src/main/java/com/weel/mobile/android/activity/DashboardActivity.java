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
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
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

import com.weel.mobile.android.R;
import com.weel.mobile.android.adapter.DrawerListAdapter;
import com.weel.mobile.android.config.Constants;
import com.weel.mobile.android.fragment.DashboardPanelsFragment;
import com.weel.mobile.android.model.DrawerItem;
import com.weel.mobile.android.model.Photo;
import com.weel.mobile.android.model.User;
import com.weel.mobile.android.model.Vehicle;
import com.weel.mobile.android.resource.ApplicationResources;
import com.weel.mobile.android.service.AuthenticationService;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.weel.mobile.android.account.Account.AUTHTOKEN_TYPE_API_ACCESS;

/**
 * Created by jeremy.beckman on 16-03-14.
 */
public class DashboardActivity extends WeeLActivity implements DashboardPanelsFragment.OnFragmentInteractionListener {
    public static final String EXTRA_LOGOUT = "com.weel.mobile.android.extras.dashobard.LOGOUT";

    private ArrayList<DrawerItem> drawerItems;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(Constants.USER_DATA);
        vehicle = (Vehicle) intent.getSerializableExtra(Constants.VEHICLE_DATA);
        authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

        addToolbar();
        addDrawer();
        showDashboard();
    }

    @Override
    protected void addToolbar() {
        super.addToolbar();

        toolbar.setTitle("");

        showActionBar();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar,
                R.string.action_drawer_open,
                R.string.action_drawer_close) {

        };

        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    protected void setView() {

    }

    @Override
    protected void callRemoteAPI(String uri, Bundle params) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration conf) {
        super.onConfigurationChanged(conf);
        if (drawerToggle != null) {
            drawerToggle.onConfigurationChanged(conf);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    public void onDashboardPanelsInteraction(Class activityClass) {
        if (user != null && vehicle != null && authToken != null) {
            Intent intent = new Intent(this, activityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(Constants.USER_DATA, user);
            intent.putExtra(Constants.VEHICLE_DATA, vehicle);
            intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
            startActivity(intent);
        }
    }

    private void addDrawer() {
        Resources resources = getResources();
        int width = (int) resources.getDimension(R.dimen.activity_drawer_width);

        DrawerLayout.LayoutParams layoutParams = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.MATCH_PARENT);
        layoutParams.width = width;
        layoutParams.gravity = Gravity.START;

        ListView drawerListView = new ListView(this);
        drawerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        drawerListView.setBackgroundColor(ContextCompat.getColor(this, R.color.weel_light_gray));
        drawerListView.setOnItemClickListener(new DrawerItemClickListener());
        setDrawerList(drawerListView);

        drawerLayout.addView(drawerListView, layoutParams);
    }

    private void setDrawerList(ListView listView) {
        drawerItems = new ArrayList<DrawerItem>();

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

        navItem = new DrawerItem();
        navItem.setLabel(getString(R.string.action_service_records));
        navItem.setIcon(R.mipmap.ic_maintenance_records_black_24dp);
        navItem.setActivity(ServiceHistoryActivity.class);
        drawerItems.add(navItem);

        addDrawerHeader(listView);
        addDrawerFooter(listView);

        listView.setAdapter(new DrawerListAdapter(this, drawerItems));
    }

    private void loadDrawerHeaderContent(ViewGroup header) {
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

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, VehicleProfileActivity.class);
                intent.putExtra(VehicleProfileActivity.EXTRA_USERDATA, user);
                intent.putExtra(VehicleProfileActivity.EXTRA_VEHICLEDATA, vehicle);
                intent.putExtra(VehicleProfileActivity.EXTRA_AUTHTOKEN, authToken);
                startActivity(intent);
            }
        });
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

    private void showDashboard() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        if (fragmentManager.getBackStackEntryCount() == 0) {

                        }
                    }
                });

        try {
            DashboardPanelsFragment panelsFragment = new DashboardPanelsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.main, panelsFragment, "panels")
                    .commit();
        } catch (Exception e) {
            showSingleButtonAlert(e.getMessage());
        }
    }

    private void showActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            DrawerItem item = drawerItems.get(position - 1);

            if (vehicle != null) {
                Bundle extras = new Bundle();
                extras.putSerializable(Constants.USER_DATA, user);
                extras.putSerializable(Constants.VEHICLE_DATA, vehicle);
                extras.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                item.setExtras(extras);
            }

            Intent intent = new Intent(DashboardActivity.this, item.getActivity());
            intent.putExtras(item.getExtras());
            startActivity(intent);
        }
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

    private class SignOutWorkerTask extends AsyncTask<Void, Void, Bundle> {

        public SignOutWorkerTask() {

        }

        @Override
        protected Bundle doInBackground(Void... params) {
            Bundle results = null;

            AuthenticationService service = new AuthenticationService();

            try {
                String url = getString(R.string.api_url) + getString(R.string.api_logout_uri);
                Boolean isloggedOut = service.logout(url, authToken);

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

        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
