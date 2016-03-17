package com.weel.mobile.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.weel.mobile.android.config.Constants;

/**
 *
 * @author jeremy.beckman
 */
public final class LocationService extends Service implements LocationListener {

    private final Context context;

    private Location location;
    private final LocationManager locationManager;
    private static LocationService instance;

    private LocationService(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static LocationService getInstance(Context context) {
        if(instance == null) {
            instance = new LocationService(context);
        }
        return instance;
    }

    public void startService() {
        if (isNetworkEnabled()) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Constants.MIN_INTERVAL_UPDATES, Constants.MIN_DISTANCE_LOCATION_UPDATES, this);

            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }

        if (location == null) {

            if (isGPSEnabled()) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constants.MIN_INTERVAL_UPDATES, Constants.MIN_DISTANCE_LOCATION_UPDATES, this);

                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        }
    }

    public void stopService() {
        if(locationManager != null) {
            locationManager.removeUpdates(LocationService.this);
        }
    }

    public Location getLocation() {
        return location;
    }

    public boolean isGPSEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isNetworkEnabled() {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onStatusChanged(String status, int n, Bundle bundle) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
