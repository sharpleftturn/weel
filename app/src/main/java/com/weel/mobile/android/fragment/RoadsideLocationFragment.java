package com.weel.mobile.android.fragment;

import android.app.Activity;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import com.weel.mobile.android.R;
import com.weel.mobile.android.service.LocationAddressService;

/**
 * Created by jeremy.beckman on 2016-01-28.
 */
public class RoadsideLocationFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String ARG_LOCATION = "com.weel.mobile.fragments.arguments.LOCATION";

    // TODO: Rename and change types of parameters
    private Location location;
    private String mParam2;
    private AddressResultReceiver mResultReceiver;
    private GoogleApiClient googleApiClient;
    private OnFragmentInteractionListener mListener;
    private Boolean addressRequested;
    private String addressOutput;

    public static RoadsideLocationFragment newInstance(Location location) {
        RoadsideLocationFragment fragment = new RoadsideLocationFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    public RoadsideLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            location = getArguments().getParcelable(ARG_LOCATION);
        }

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roadside_location, container, false);

        if (googleApiClient.isConnected() && location != null) {
            startLocationService();
        }

        addressRequested = true;

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Gets the best and most recent location currently available,
        // which may be null in rare cases when a location is not available.
        location = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);

        if (location != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(getActivity(), R.string.no_geocoder_available,
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (addressRequested) {
                startLocationService();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onConnectionSuspended(int cause) {

    }

    public void updateLocation(Location location) {
        this.location = location;

        mResultReceiver = new AddressResultReceiver(new Handler());
        LocationAddressService.startActionFetchAddress(getActivity(), mResultReceiver, location);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            addressOutput = resultData.getString(LocationAddressService.RESULT_DATA_KEY);
            // displayAddressOutput();

            if (resultCode == LocationAddressService.SUCCESS_RESULT) {
                showLocationAddress(resultData);
            }

        }
    }

    protected void startLocationService() {
        mResultReceiver = new AddressResultReceiver(new Handler());
        LocationAddressService.startActionFetchAddress(getActivity(), mResultReceiver, location);
    }

    private void showLocationAddress(Bundle data) {
        String locationAddress = data.getString(LocationAddressService.RESULT_DATA_KEY);

        View view = getView();

        TextView addressView = (TextView) view.findViewById(R.id.location_address);
        addressView.setText(locationAddress);
    }
}
