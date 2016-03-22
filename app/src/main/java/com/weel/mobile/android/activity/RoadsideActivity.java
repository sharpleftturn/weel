package com.weel.mobile.android.activity;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.weel.mobile.R;
import com.weel.mobile.android.config.Constants;
import com.weel.mobile.android.fragment.RoadsideListFragment;
import com.weel.mobile.android.fragment.RoadsideLocationFragment;
import com.weel.mobile.android.model.IncidentSource;
import com.weel.mobile.android.model.RoadsideIncident;
import com.weel.mobile.android.model.Vehicle;
import com.weel.mobile.android.service.LocationService;
import com.weel.mobile.android.service.RoadsideService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy.beckman on 2016-01-14.
 */
public class RoadsideActivity extends WeeLActivity implements RoadsideListFragment.OnFragmentInteractionListener, RoadsideLocationFragment.OnFragmentInteractionListener, View.OnClickListener {
    public static final String EXTRA_USERDATA = "com.weel.mobile.extras.USERDATA";
    public static final String EXTRA_VEHICLEDATA = "com.weel.mobile.extras.VEHICLEDATA";
    private static final String EXTRA_VEHICLE_ID = "com.weel.mobiel.extras.roadside.VEHICLE_ID";
    private static final String EXTRA_INCIDENT = "com.weel.mobile.extras.roadside.INCIDENT";

    private LocationService locationService;
    private GoogleMap map;
    private Vehicle vehicle;
    private String authToken;
    private Location latestLocation;
    private RoadsideIncident incident;
    private ImageButton roadsideButton;
    private TextView roadsideCallTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadside);

        addToolbar();

        incident = new RoadsideIncident();

        Intent intent = getIntent();

        authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
        vehicle = (Vehicle) intent.getSerializableExtra(Constants.VEHICLE_DATA);

        RoadsideListFragment fragment = RoadsideListFragment.newInstance(vehicle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.roadside, fragment, "roadside_list")
                .commit();

        roadsideCallTextView = (TextView) findViewById(R.id.instruct_call_mechanic);
        roadsideCallTextView.setVisibility(View.GONE);

        roadsideButton = (ImageButton) findViewById(R.id.roadside_phone);
        roadsideButton.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationService();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // locationService = LocationService.getInstance(this);
        latestLocation = locationService.getLocation();

        if (latestLocation != null) {
            initializeMap(latestLocation);

            incident.setLatitude(latestLocation.getLatitude());
            incident.setLongitude(latestLocation.getLongitude());
        } else {
            showLocationError();
        }
    }

    @Override
    protected void callRemoteAPI(String remoteUri, Bundle params) {

    }

    @Override
    protected void setView() {

    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count > 0) {
            roadsideCallTextView.setVisibility(View.GONE);
            roadsideButton.setVisibility(View.GONE);
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void onFragmentInteraction(Object data) {
        incident.setType(String.valueOf(data));

        RoadsideLocationFragment fragment = RoadsideLocationFragment.newInstance(latestLocation);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.roadside, fragment, "roadside_location")
                .addToBackStack("roadside_location")
                .commit();

        roadsideCallTextView.setVisibility(View.VISIBLE);

        roadsideButton.setVisibility(View.VISIBLE);
        roadsideButton.setOnClickListener(this);
    }

    public void onFragmentInteraction() {

    }

    @Override
    public void onClick(View view) {
        createRoadsideIncident();
    }

    @Override
    protected void addToolbar() {
        super.addToolbar();
        toolbar.setTitle(R.string.roadside_toolbar_label);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.weel_roadside_theme));
    }

    private void initializeMap(final Location location) {
        FragmentManager manager = getSupportFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) manager.findFragmentById(R.id.map);
        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap map) {
                final LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                map.addMarker(new MarkerOptions().position(latlng).title(getString(R.string.roadside_map_location)).draggable(true));

                map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        LatLng position = marker.getPosition();

                        latestLocation.setLatitude(position.latitude);
                        latestLocation.setLongitude(position.longitude);

                        updateLocation(latestLocation);
                    }
                });
            }
        });


    }

    private void startLocationService() {
        locationService = LocationService.getInstance(this);
        locationService.startService();
    }

    private void stopLocationService() {
        locationService = LocationService.getInstance(this);
        locationService.stopService();
    }

    private void updateLocation(Location location) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        RoadsideLocationFragment fragment = (RoadsideLocationFragment) fragmentManager.findFragmentByTag("roadside_location");
        fragment.updateLocation(location);
    }


    private void createRoadsideIncident() {
        Bundle params = new Bundle();
        params.putLong(EXTRA_VEHICLE_ID, vehicle.getId());
        new RoadsideWorkerTask().execute(params);
    }

    private void startCall(String roadsidePhone) throws SecurityException {
        String path = "tel:" + roadsidePhone;
        Uri uri = Uri.parse(path);

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(uri);
        startActivity(callIntent);
    }

    private void openCallOptions(final List<IncidentSource> source) {
        List<String> options = new ArrayList<String>();

        for (IncidentSource item : source) {
            String option = item.getProgram();
            options.add(option);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.roadside_oem_item, options);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT); // THEME_HOLO_LIGHT deprecated in API level 23
        builder.setTitle(getString(R.string.roadside_oem_title));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                IncidentSource src = (IncidentSource) source.get(i);
                startCall(src.getPhone());
            }
        });
        builder.show();
    }

    private String getRoadsideOEMMessage() {
        String segment = getString(R.string.roadside_oem_message);
        String message = String.format(segment,
                vehicle.getMake().getDisplayName(),
                vehicle.getModel().getDisplayName(),
                vehicle.getMake().getDisplayName(),
                vehicle.getMake().getDisplayName());
        return message;
    }

    private class RoadsideWorkerTask extends AsyncTask<Bundle, Void, RoadsideIncident> {

        RoadsideService service;

        @Override
        protected RoadsideIncident doInBackground(Bundle... params) {
            Bundle data = params[0];
            long vehicleId = data.getLong(EXTRA_VEHICLE_ID);

            service = new RoadsideService();

            String url = String.format(getString(R.string.api_url) + getString(R.string.roadside_uri), vehicleId);
            return service.addRoadsideIncident(url, vehicleId, authToken, incident.getLatitude(), incident.getLongitude(), incident.getType());
        }

        @Override
        protected void onPostExecute(RoadsideIncident data) {
            if (service.hasError()) {
                showSingleButtonAlert(service.getErrorMessage());
            } else {
                List<IncidentSource> source = data.getSource();
                if (source.size() > 1) {
                    openCallOptions(source);
                } else if (source.size() == 1) {
                    startCall(source.get(0).getPhone());
                }
            }
        }
    }

    private void showLocationError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.error_roadside_location))
                .setPositiveButton(getString(R.string.alert_dialog_button_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create();
        builder.show();
    }
}
