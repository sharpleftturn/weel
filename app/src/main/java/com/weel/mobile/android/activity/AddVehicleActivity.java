package com.weel.mobile.android.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.weel.mobile.R;
import com.weel.mobile.android.fragment.AddVehicleFragment;
import com.weel.mobile.android.model.Make;
import com.weel.mobile.android.model.Model;
import com.weel.mobile.android.model.ModelYear;
import com.weel.mobile.android.model.User;
import com.weel.mobile.android.model.Vehicle;
import com.weel.mobile.android.service.VehicleService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by jeremy.beckman on 2015-10-21.
 */
public class AddVehicleActivity extends WeeLActivity implements AddVehicleFragment.OnFragmentInteractionListener {
    public static final String EXTRA_MAKEDATA = "com.weel.android.extras.addvehicle.MAKEDATA";
    public static final String EXTRA_MODELDATA = "com.weel.android.extras.addvehicle.MODELDATA";
    public static final String EXTRA_VEHICLEDATA = "com.weel.android.extras.addvehicle.VEHICLEDATA";
    public static final String EXTRA_VEHICLEVIN = "com.weel.android.extras.addvehicle.VEHICLEVIN";
    public static final String EXTRA_USERDATA = "com.weel.mobile.android.extras.addvehicle.USERDATA";
    public static final String EXTRA_AUTHTOKEN = "com.weel.mobile.android.extras.addvehicle.AUTHTOKEN";
    public static final String TAG_ADD_MANUAL = "com.weel.mobile.tag.ADD_MANUAL";

    private static final int INVALID = -1;

    private Vehicle vehicle;
    private ArrayList<Make> makes;
    private ArrayList<Model> models;
    private ArrayList<ModelYear> modelYears;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_add_vehicle);

        addToolbar();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        user = (User) extras.get(EXTRA_USERDATA);
        authToken = extras.getString(EXTRA_AUTHTOKEN);

        vehicle = new Vehicle();

        addControlListeners();

        showAddVehicleFragment();
    }

    @Override
    protected void callRemoteAPI(@Nullable String remoteURI, Bundle params) {
        Vehicle selectedVehicle = (Vehicle) params.get(EXTRA_VEHICLEDATA);
        AsyncTask<Vehicle, Void, Vehicle> asyncTask = new AddVehicleWorkerTask().execute(selectedVehicle);
    }

    @Override
    protected void setView() {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void onAddVehicleFragmentInteraction(String action, @Nullable Object data) {
        if (action.equals(AddVehicleFragment.ACTION_GET_MAKE)) {
            Make make = (Make) data;
            vehicle.setMake(make);

            if (make.getId() != -1) {
                getModelData(make);
            }
        } else if (action.equals(AddVehicleFragment.ACTION_GET_MODEL)) {
            Model model = (Model) data;
            vehicle.setModel(model);

            if (model.getId() != -1) {
                getModelYearData(model);
            }
        } else if (action.equals(AddVehicleFragment.ACTION_GET_YEAR)) {
            ModelYear modelYear = (ModelYear) data;

            if (modelYear.getId() != -1) {
                // Still need to determine how best to handle this data. For now, populate all three attributes.
                vehicle.getModel().setYear(modelYear.getModelYear());
                vehicle.setYear(modelYear.getModelYear());
                vehicle.setYearId(modelYear.getId());
            }
        }
    }

    @Override
    protected void addToolbar() {
        super.addToolbar();
        toolbar.setTitle(R.string.title_activity_add_vehicle);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.weel_theme));
    }

    private void addControlListeners() {
        Button continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVehicle();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showAddVehicleFragment() {
        getMakeData();

        Make make = new Make();
        make.setId(-1);
        make.setName("MAKE");
        make.setDisplayName("Make");
        makes.add(0, make);

        models = new ArrayList<Model>();
        Model model = new Model();
        model.setId(-1);
        model.setName("MODEL");
        model.setDisplayName("Model");
        models.add(0, model);

        modelYears = new ArrayList<>();
        ModelYear modelYear = new ModelYear();
        modelYear.setId(-1L);
        modelYear.setModelId(-1L);
        modelYear.setModelYear(Calendar.getInstance().get(Calendar.YEAR));
        modelYears.add(0, modelYear);

        Fragment fragment = AddVehicleFragment.newInstance(user, makes, models, modelYears);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, TAG_ADD_MANUAL)
                .commit();
    }

    private void getMakeData() {
        try {
            AsyncTask<String, Void, ArrayList<Make>> asyncTask = new GetMakesWorkerTask().execute(authToken);
            makes = asyncTask.get();
        } catch (ExecutionException ee) {

        } catch (InterruptedException ie) {

        }
    }

    private void getModelData(Make make) {
        Bundle data = new Bundle();
        data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        data.putSerializable(EXTRA_MAKEDATA, make);

        try {
            AsyncTask<Bundle, Void, ArrayList<Model>> asyncTask = new GetModelsWorkerTask().execute(data);
            models = asyncTask.get();

            if (models != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag(TAG_ADD_MANUAL);
                ((AddVehicleFragment) fragment).updateModels(models);
            }
        } catch (ExecutionException ee) {

        } catch (InterruptedException ie) {

        }
    }

    private void getModelYearData(Model model) {
        Bundle data = new Bundle();
        data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        data.putSerializable(EXTRA_MODELDATA, model);

        try {
            AsyncTask<Bundle, Void, ArrayList<ModelYear>> asyncTask = new GetModelYearsWorkerTask().execute(data);
            ArrayList<ModelYear> modelYears = asyncTask.get();

            if (modelYears != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag(TAG_ADD_MANUAL);
                ((AddVehicleFragment) fragment).updateModelYears(modelYears);
            }
        } catch (ExecutionException ee) {

        } catch (InterruptedException ie) {

        }
    }

    private void addVehicle() {
        if (vehicleSelected()) {
            Bundle data = new Bundle();
            data.putSerializable(EXTRA_VEHICLEDATA, vehicle);
            data.putString(AccountManager.KEY_AUTHTOKEN, authToken);

            saveVehicle(data);
        } else {
            showError(getString(R.string.vehicle_selection_error_message));
        }
    }

    private Vehicle saveVehicle(Bundle params) {
        Vehicle selectedVehicle = (Vehicle) params.getSerializable(EXTRA_VEHICLEDATA);
        AsyncTask<Vehicle, Void, Vehicle> asyncTask = new AddVehicleWorkerTask().execute(selectedVehicle);

        try {
            vehicle = asyncTask.get();
            showVehicleProfile(vehicle);
        } catch (ExecutionException ee) {
        } catch (InterruptedException ie) {
        }

        return vehicle = selectedVehicle;
    }

    private void showVehicleProfile(Vehicle vehicleReturned) {
        if (vehicleReturned != null) {
            Intent vehicleProfileIntent = new Intent(AddVehicleActivity.this, VehicleProfileActivity.class);
            vehicleProfileIntent.putExtra(VehicleProfileActivity.EXTRA_VEHICLEDATA, vehicleReturned);
            vehicleProfileIntent.putExtra(VehicleProfileActivity.EXTRA_USERDATA, user);
            vehicleProfileIntent.putExtra(VehicleProfileActivity.EXTRA_AUTHTOKEN, authToken);
            startActivity(vehicleProfileIntent);
        }
    }

    private boolean vehicleSelected() {
        if (vehicle.getMake().getId() != INVALID && vehicle.getModel().getId() != INVALID) {
            return true;
        }

        return false;
    }

    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message)
                .setPositiveButton(getString(R.string.alert_dialog_button_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create();
        builder.show();
    }

    private class GetVehicleWorkerTask extends AsyncTask<Bundle, Void, Vehicle> {

        VehicleService vehicleService;

        @Override
        protected Vehicle doInBackground(Bundle... params) {
            Bundle param = params[0];
            String vin = param.getString(EXTRA_VEHICLEVIN);
            String token = param.getString(AccountManager.KEY_AUTHTOKEN);

            vehicleService = new VehicleService();
            String url = getString(R.string.api_url) + getString(R.string.vin_vehicle_uri);
            Vehicle aVehicle = vehicleService.getVehicleByVin(vin, token, url);
            return aVehicle;
        }

        @Override
        protected void onPostExecute(Vehicle aVehicle) {
            if (vehicleService.hasError()) {
                showError(vehicleService.getErrorMessage());

            //    updateView(null);
            }
        }
    }

    private class GetMakesWorkerTask extends AsyncTask<String, Void, ArrayList<Make>> {

        @Override
        protected ArrayList<Make> doInBackground(String... params) {
            String token = params[0];
            VehicleService vehicleService = new VehicleService();
            String url = getString(R.string.api_url) + getString(R.string.all_makes_uri);
            ArrayList<Make> makes = vehicleService.getMakes(url, token);
            return makes;
        }
    }

    private class GetModelsWorkerTask extends AsyncTask<Bundle, Void, ArrayList<Model>> {

        @Override
        protected ArrayList<Model> doInBackground(Bundle... params) {
            Bundle data = params[0];
            String token = data.getString(AccountManager.KEY_AUTHTOKEN);
            Make make = (Make) data.getSerializable(EXTRA_MAKEDATA);
            VehicleService vehicleService = new VehicleService();
            String url = getString(R.string.api_url) + getString(R.string.all_models_uri);
            ArrayList<Model> models = vehicleService.getModels(url, make.getId(), token);

            return models;
        }
    }

    private class GetModelYearsWorkerTask extends AsyncTask<Bundle, Void, ArrayList<ModelYear>> {

        @Override
        protected ArrayList<ModelYear> doInBackground(Bundle... params) {
            Bundle data = params[0];
            String token = data.getString(AccountManager.KEY_AUTHTOKEN);
            Model model = (Model) data.getSerializable(EXTRA_MODELDATA);
            VehicleService vehicleService = new VehicleService();
            String url = getString(R.string.api_url) + getString(R.string.all_model_years_uri);
                    ArrayList<ModelYear> modelYears = vehicleService.getModelYears(url , model.getId(), token);
            return modelYears;
        }
    }

    private class AddVehicleWorkerTask extends AsyncTask<Vehicle, Void, Vehicle> {

        public AddVehicleWorkerTask() {

        }

        @Override
        protected Vehicle doInBackground(Vehicle... params) {
            String url = getString(R.string.api_url) + getString(R.string.api_user_vehicles_uri);
            Vehicle selectedVehicle = params[0];

            VehicleService vehicleService = new VehicleService();
            vehicle = vehicleService.addVehicle(selectedVehicle, authToken, url);

            return vehicle;
        }

        @Override
        protected void onPostExecute(Vehicle vehicle) {

        }
    }
}
