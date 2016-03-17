package com.weel.mobile.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weel.mobile.android.R;
import com.weel.mobile.android.activity.DealsActivity;
import com.weel.mobile.android.activity.MaintenanceActivity;
import com.weel.mobile.android.activity.MechanicActivity;
import com.weel.mobile.android.activity.RoadsideActivity;

/**
 * Created by jeremy.beckman on 2015-10-21.
 */
public class DashboardPanelsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_panels, container, false);
        return view;
    }

    public interface OnFragmentInteractionListener {
        public void onDashboardPanelsInteraction(Class aClass);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View roadsideView = (View) getActivity().findViewById(R.id.top_left_panel);
        setOnClickListener(roadsideView);

        View adviceView = (View) getActivity().findViewById(R.id.top_right_panel);
        setOnClickListener(adviceView);

        View dealsView = (View) getActivity().findViewById(R.id.bottom_left_panel);
        setOnClickListener(dealsView);

        View maintenanceView = (View) getActivity().findViewById(R.id.bottom_right_panel);
        setOnClickListener(maintenanceView);
    }

    @Override
    public void onPause() {
        super.onPause();
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

    private void setOnClickListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Class activityClass = null;
                switch (view.getId()) {
                    case R.id.top_left_panel:
                        activityClass = MechanicActivity.class;
                        break;
                    case R.id.top_right_panel:
                        activityClass = RoadsideActivity.class;
                        break;
                    case R.id.bottom_left_panel:
                        activityClass = DealsActivity.class;
                        break;
                    case R.id.bottom_right_panel:
                        activityClass = MaintenanceActivity.class;
                        break;
                }

                mListener.onDashboardPanelsInteraction(activityClass);
            }
        });
    }
}
