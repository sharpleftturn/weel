package com.weel.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weel.mobile.android.R;

/**
 * Created by jeremy.beckman on 2016-02-24.
 */
public class ServiceHistoryEmptyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_history_empty, container, false);
        return view;
    }
}
