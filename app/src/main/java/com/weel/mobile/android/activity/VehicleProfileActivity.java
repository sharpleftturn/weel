package com.weel.mobile.android.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.weel.mobile.android.R;
import com.weel.mobile.android.model.Photo;
import com.weel.mobile.android.model.User;
import com.weel.mobile.android.model.Vehicle;
import com.weel.mobile.android.service.VehicleService;

//import org.apache.http.impl.conn.SingleClientConnManager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author jeremy.beckman
 */
public class VehicleProfileActivity extends WeeLActivity {
    private static final String TAG = "VehicleProfileActivity";
    public static final String EXTRA_USERDATA = "com.weel.mobile.extras.USERDATA";
    public static final String EXTRA_VEHICLEDATA = "com.weel.mobile.extras.VEHICLEDATA";
    public static final String EXTRA_AUTHTOKEN = "com.weel.mobile.extras.AUTHTOKEN";

    private Vehicle vehicle;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_vehicle_profile);

     //   getActionBar().setTitle(getString(R.string.title_activity_vehicle_profile));

        Intent intent = getIntent();
        vehicle = (Vehicle) intent.getSerializableExtra(EXTRA_VEHICLEDATA);
        user = (User) intent.getSerializableExtra(EXTRA_USERDATA);
        authToken = intent.getStringExtra(EXTRA_AUTHTOKEN);

        Spinner spinner = (Spinner) findViewById(R.id.ownership_value);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ownership_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button saveButton = (Button) findViewById(R.id.action_save);
        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                saveVehicle();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.action_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setOwnershipType();
    }

    @Override
    public void onResume() {
        super.onResume();
        showVehicleProfile();
    }

    @Override
    protected void callRemoteAPI(@Nullable String remoteURI, Bundle params) {

    }

    @Override
    protected void setView() {

    }

    private void showVehicleProfile() {
        try {
            loadVehicleProfileImage();
        } catch (IOException ioe) {
            Log.d(TAG, ioe.getMessage());
        }

        TextView vehicleNameView = (TextView) findViewById(R.id.name_value);
        vehicleNameView.setText(vehicle.getName());
        vehicleNameView.addTextChangedListener(nameTextWatcher);

        TextView vinView = (TextView) findViewById(R.id.vin_value);
        if (vehicle.getVin() == null) {
            vinView.setEnabled(false);
        } else {
            vinView.setText(vehicle.getVin());
        }

        TextView annualTravelView = (TextView) findViewById(R.id.estimated_value);
        annualTravelView.setText(String.valueOf(vehicle.getAnnualDistance()));
        annualTravelView.addTextChangedListener(annualTextWatcher);

        TextView odometerView = (TextView) findViewById(R.id.odometer_value);
        odometerView.setText(String.valueOf(vehicle.getOdometer()));
        odometerView.addTextChangedListener(odometerTextWatcher);

        Spinner spinner = (Spinner) findViewById(R.id.ownership_value);
        String ownership = vehicle.getOwnership();

        if(ownership != null) {
            if(ownership.equals("New")) {
                spinner.setSelection(0);
            } else if(ownership.equals("Used")) {
                spinner.setSelection(1);
            }
        }
    }

    private void saveVehicle() {
        TextView nameView = (TextView) findViewById(R.id.name_value);
        TextView vinView = (TextView) findViewById(R.id.vin_value);
        TextView estimatedView = (TextView) findViewById(R.id.estimated_value);
        TextView odometerView = (TextView) findViewById(R.id.odometer_value);
        Spinner spinner = (Spinner) findViewById(R.id.ownership_value);
        String ownership = (String) spinner.getSelectedItem();

        try {
            int odometer = Integer.valueOf(odometerView.getText().toString());
            int annualDistance = Integer.valueOf(estimatedView.getText().toString());
            vehicle.setOwnership(ownership);
            vehicle.setOdometer(odometer);
            vehicle.setAnnualDistance(annualDistance);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }

        vehicle.setName(nameView.getText().toString());
        vehicle.setVin(vinView.getText().toString());

        updateVehicle();

    }

    private final TextWatcher odometerTextWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence cs, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence cs, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            String value = s.toString();

            try {
                int odometer = Integer.valueOf(value);
                vehicle.setAnnualDistance(odometer);
            } catch (NumberFormatException nfe) {
                Log.d(TAG, nfe.getMessage());
            }

        }
    };

    private final TextWatcher annualTextWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence cs, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence cs, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            String value = s.toString();

            try {
                int annualMileage = Integer.valueOf(value);
                vehicle.setAnnualDistance(annualMileage);
            } catch (NumberFormatException nfe) {
                Log.d(TAG, nfe.getMessage());
            }
        }
    };

    private final TextWatcher nameTextWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence cs, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence cs, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            String value = s.toString();

            if (value != null && !value.equals("")) {
                vehicle.setName(value);
            }

        }
    };

    private void setOwnershipType() {
        Spinner spinner = (Spinner) findViewById(R.id.ownership_value);
        String[] ownershipList = getResources().getStringArray(R.array.ownership_type);

        for (int n = 0; n < ownershipList.length; n++) {
            if (ownershipList[n].equals(vehicle.getOwnership())) {
                spinner.setSelection(n);
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> av, View view, int i, long l) {
                String value = (String) av.getItemAtPosition(i);
                vehicle.setOwnership(value);
            }

            public void onNothingSelected(AdapterView<?> av) {
                // Do nothing
            }
        });
    }

    private void loadVehicleProfileImage() throws IOException {
        try {
            ImageView profileImageView = (ImageView) findViewById(R.id.vehicle_image);

            Photo vehiclePhoto = vehicle.getPhoto();

            String photoUrl;

            if (vehiclePhoto.getFullUrl() == null) {
                Uri uri = Uri.parse("R.drawable.ic_local_phone_white_24dp");


            } else {

            }

            URL url = new URL(vehiclePhoto.getFullUrl());
            AsyncTask<URL, Void, Bitmap> asyncTask = new BitmapWorkerTask(profileImageView).execute(url);
            Bitmap bitmap = asyncTask.get();

            if (bitmap != null) {
                profileImageView.setImageBitmap(bitmap);
            }
        } catch (ExecutionException ee) {
            Log.d(TAG, ee.getMessage());
        } catch (InterruptedException ie) {
            Log.d(TAG, ie.getMessage());
        }
    }

    private class BitmapWorkerTask extends AsyncTask<URL, Void, Bitmap> {
        private final String TAG = "BitmapWorkerTask";
        private final WeakReference<ImageView> imageViewWeakReference;
        private int data = 0;

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
                Log.d(TAG, ioe.getMessage());
            }
            return bitmap;
        }

        private Bitmap loadBitmap(URL url) throws IOException {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) url.getContent());
            return bitmap;
        }
    }

    private void addVehicle() {
        try {
            AsyncTask<Bundle, Void, Vehicle> asyncTask = new AddVehicleWorkerTask(VehicleProfileActivity.this).execute(new Bundle());
            vehicle = asyncTask.get();

            if (vehicle != null) {

            }
        } catch (ExecutionException ee) {

        } catch (InterruptedException ie) {

        }
    }

    private void updateVehicle() {
        try {
            AsyncTask<Bundle, Void, Vehicle> updateTask = new UpdateVehicleWorkerTask(VehicleProfileActivity.this).execute(new Bundle());
            vehicle = updateTask.get();
        } catch (ExecutionException ee) {

        } catch (InterruptedException ie) {

        }
    }

    private class AddVehicleWorkerTask extends AsyncTask<Bundle, Void, Vehicle> {
        private final WeakReference<Context> contextRef;

        public AddVehicleWorkerTask(Context context) {
            contextRef = new WeakReference<Context>(context);
        }

        @Override
        protected Vehicle doInBackground(Bundle... params) {
            VehicleService vehicleService = new VehicleService();
            String url = getString(R.string.api_url) + getString(R.string.api_user_vehicles_uri);
            return vehicleService.addVehicle(vehicle, authToken, url);
        }

        @Override
        protected void onPostExecute(Vehicle vehicle) {
            // Context context = contextRef.get();

        }
    }

    private class UpdateVehicleWorkerTask extends AsyncTask<Bundle, Void, Vehicle> {
        private final WeakReference<Context> contextRef;

        public UpdateVehicleWorkerTask(Context context) {
            contextRef = new WeakReference<Context>(context);
        }

        @Override
        protected Vehicle doInBackground(Bundle... params) {
            VehicleService vehicleService = new VehicleService();
            String url = getString(R.string.api_url) + getString(R.string.api_user_vehicles_uri);
            return vehicleService.updateVehicle(url, vehicle, authToken);
        }

        @Override
        protected void onPostExecute(Vehicle vehicle) {
            finish();
        }
    }
}
