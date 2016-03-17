package com.weel.mobile.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.weel.mobile.android.adapter.DealsListAdapter;
import com.weel.mobile.android.model.Deal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy.beckman on 2016-02-29.
 */
public class DealsListFragment extends ListFragment {
    private static final String EXTRA_DEAL_LIST = "com.weel.mobile.extras.deals.DEAL_LIST";

    private List<Deal> deals;

    private OnFragmentInteractionListener mListener;

    public static DealsListFragment newInstance(ArrayList<Deal> deals) {
        DealsListFragment fragment = new DealsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DEAL_LIST, deals);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DealsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            deals = (ArrayList<Deal>) args.getSerializable(EXTRA_DEAL_LIST);
        }
        setListAdapter(new DealsListAdapter(getActivity(), deals));
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            mListener.onFragmentInteraction(l.getItemAtPosition(position));
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Object data);
    }
}
