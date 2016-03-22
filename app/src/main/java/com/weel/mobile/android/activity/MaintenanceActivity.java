package com.weel.mobile.android.activity;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.weel.mobile.R;
import com.weel.mobile.android.config.Constants;
import com.weel.mobile.android.fragment.MaintenancePageFragment;
import com.weel.mobile.android.model.Vehicle;

/**
 * Created by jeremy.beckman on 2016-01-14.
 */
public class MaintenanceActivity extends WeeLActivity {

    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        addToolbar();

        Intent intent = getIntent();
        vehicle = (Vehicle) intent.getSerializableExtra(Constants.VEHICLE_DATA);
        authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

        addFragment();
    }

    private void addFragment() {
        Fragment fragment = MaintenancePageFragment.newInstance(vehicle, authToken);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.maintenance_content_layout, fragment, "maintenancePlan")
                .commit();
    }

    @Override
    protected void addToolbar() {
        super.addToolbar();
        toolbar.setTitle(R.string.maintenance_toolbar_label);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.weel_maintenance_theme));
    }

    @Override
    protected void callRemoteAPI(String remoteUri, Bundle params) {

    }

    @Override
    protected void setView() {

    }
}
