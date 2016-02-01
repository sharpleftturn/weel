package com.weel.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weel.mobile.android.R;

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
}
