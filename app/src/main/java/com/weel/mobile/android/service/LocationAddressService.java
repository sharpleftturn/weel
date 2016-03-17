package com.weel.mobile.android.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jeremy.beckman on 2016-01-28.
 */
public class LocationAddressService extends IntentService {

    private static final String TAG = "LocationAddressService";
    private static final String ACTION_FETCH_ADDRESS = "com.weel.mobile.service.action.FETCH_ADDRESS";
    private static final String EXTRA_LOCATION = "com.weel.mobile.service.extra.LOCATION";
    public static final String EXTRA_LOCATION_DATA = "com.weel.mobile.service.extra.LOCATIONDATA";

    public static final int SUCCESS_RESULT = 0;
    public static final String RESULT_DATA_KEY = "com.weel.mobile.service.result.RESULT_DATA_KEY";
    public static final String RECEIVER = "com.weel.mobile.service.RECEIVER";


    protected ResultReceiver mReceiver;

    /**
     * Starts this service to perform action FetchAddress with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFetchAddress(Context context, ResultReceiver resultReceiver, Location location) {
        Intent intent = new Intent(context, LocationAddressService.class);
        intent.setAction(ACTION_FETCH_ADDRESS);
        intent.putExtra(RECEIVER, resultReceiver);
        intent.putExtra(EXTRA_LOCATION, location);
        context.startService(intent);
    }

    public LocationAddressService() {
        super("LocationAddressService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_ADDRESS.equals(action)) {
                mReceiver = (ResultReceiver) intent.getParcelableExtra(RECEIVER);
                final Location location = (Location) intent.getParcelableExtra(EXTRA_LOCATION);
                handleActionFetchAddress(location);
            }
        }
    }

    private void handleActionFetchAddress(Location location) {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ioe) {
            Log.d(TAG, ioe.getMessage());
        } catch (IllegalArgumentException iae) {
            Log.d(TAG, iae.getMessage());
        }

        if (addresses == null || addresses.size() == 0) {

        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressLines = new ArrayList<String>();

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressLines.add(address.getAddressLine(i));
            }

            deliverResultToReceiver(SUCCESS_RESULT, TextUtils.join(System.getProperty("line.separator"), addressLines));
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}
