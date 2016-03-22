package com.weel.mobile.android.activity;

import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.weel.mobile.R;
import com.weel.mobile.android.config.Constants;
import com.weel.mobile.android.model.IncidentSource;
import com.weel.mobile.android.model.MechanicIncident;
import com.weel.mobile.android.model.RoadsideIncident;
import com.weel.mobile.android.model.Vehicle;
import com.weel.mobile.android.service.MechanicService;

import java.security.Security;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by jeremy.beckman on 2016-01-14.
 */
public class MechanicActivity extends WeeLActivity {

    private Vehicle vehicle;
    private MechanicService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic);

        addToolbar();

        Intent intent = getIntent();

        authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        vehicle = (Vehicle) intent.getSerializableExtra(Constants.VEHICLE_DATA);

        ImageButton callButton = (ImageButton) findViewById(R.id.action_call_mechanic);
        callButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                createMechanicIncident();
            }
        });
    }

    private void startCall(String phone) throws SecurityException {
        String mechanicPhone = phone;

        String path = "tel:" + mechanicPhone;
        Uri uri = Uri.parse(path);

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(uri);
        startActivity(callIntent);
    }

    private void createMechanicIncident() {

        service = new MechanicService();

        AsyncTask<Void, Void, MechanicIncident> task = new CallMechanicWorkerTask().execute();
    }

    private class CallMechanicWorkerTask extends AsyncTask<Void, Void, MechanicIncident> {

        @Override
        protected MechanicIncident doInBackground(Void... params) {
            String url = getString(R.string.api_url) + getString(R.string.mechanic_uri);
            MechanicIncident incident = service.addMechanicIncident(url, vehicle.getId(), authToken);
            return incident;
        }

        @Override
        protected void onPostExecute(MechanicIncident data) {
            if (service.hasError()) {
                showSingleButtonAlert(service.getErrorMessage());
            } else {
                IncidentSource source = data.getIncidentSource();
                if (source != null) {
                    try {
                        startCall(source.getPhone());
                    } catch (SecurityException se) {
                        showSingleButtonAlert(getString(R.string.security_exception_message));
                    }
                }
            }
        }
    }

    @Override
    protected void callRemoteAPI(String remoteUri, Bundle params) {

    }

    @Override
    protected void setView() {

    }

    @Override
    protected void addToolbar() {
        super.addToolbar();
        toolbar.setTitle(R.string.mechanic_toolbar_label);
    }
}
