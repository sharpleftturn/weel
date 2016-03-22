package com.weel.mobile.android.fragment;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weel.mobile.R;
import com.weel.mobile.android.adapter.MaintenancePagerAdapter;
import com.weel.mobile.android.model.Maintenance;
import com.weel.mobile.android.model.Vehicle;
import com.weel.mobile.android.service.MaintenancePlanService;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by jeremy.beckman on 2016-02-06.
 */
public class MaintenancePageFragment extends Fragment {
    public static final String ARG_VEHICLEDATA = "com.weel.mobile.fragments.arguments.VEHICLEDATA";

    private MaintenancePagerAdapter maintenancePagerAdapter;
    ViewPager pager;
    Vehicle vehicle;
    String authToken;

    public static MaintenancePageFragment newInstance(Vehicle vehicleParam, String token) {
        MaintenancePageFragment fragment = new MaintenancePageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VEHICLEDATA, vehicleParam);
        args.putString(AccountManager.KEY_AUTHTOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle extras = getArguments();
        vehicle = (Vehicle) extras.getSerializable(ARG_VEHICLEDATA);
        authToken = extras.getString(AccountManager.KEY_AUTHTOKEN);
        return inflater.inflate(R.layout.fragment_maintenance_pager, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadMaintenancePlan();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadMaintenancePlan() {
        AsyncTask<Bundle, Void, ArrayList<Maintenance>> asyncTask = new MaintenanceWorkerTask().execute(new Bundle());
        try {
            ArrayList<Maintenance> maintenanceList = asyncTask.get();

            maintenancePagerAdapter = new MaintenancePagerAdapter(getFragmentManager(), getActivity(), maintenanceList);
            pager = (ViewPager) getActivity().findViewById(R.id.pager);
            pager.setAdapter(maintenancePagerAdapter);
            pager.setCurrentItem(0);

        } catch (ExecutionException ee) {

        } catch (InterruptedException ie) {

        }
    }

    private class MaintenanceWorkerTask extends AsyncTask<Bundle, Void, ArrayList<Maintenance>> {
        private MaintenancePlanService service;

        public MaintenanceWorkerTask() {
            service = new MaintenancePlanService();
        }
        @Override
        protected ArrayList<Maintenance> doInBackground(Bundle... params) {
            service = new MaintenancePlanService();
            String url = getString(R.string.api_url) + getString(R.string.maintenance_uri);
            ArrayList<Maintenance> maintenanceList = service.getUserMaintenancePlan(url, vehicle.getId(), authToken);
            return maintenanceList;
        }

        @Override
        protected void onPostExecute(ArrayList<Maintenance> data) {
            if (service.hasError()) {
                showError(service.getErrorMessage());
            }
        }
    }

    private void showError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(getString(R.string.alert_dialog_button_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create();
        builder.show();
    }
}
