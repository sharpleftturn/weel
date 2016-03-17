package com.weel.mobile.android.fragment;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.weel.mobile.android.R;
import com.weel.mobile.android.model.User;
import com.weel.mobile.android.model.Vehicle;

/**
 * Created by jeremy.beckman on 2016-01-28.
 */
public class RoadsideListFragment extends ListFragment implements View.OnClickListener {
    private static final String ARG_USERDATA = "com.weel.mobile.fragments.arguments.USERDATA";
    private static final String ARG_VEHICLEDATA = "com.weel.mobile.fragments.arguments.VEHICLEDATA";

    private User user;
    private Vehicle vehicle;
    private Location latestLocation;

    private OnFragmentInteractionListener mListener;

    public static RoadsideListFragment newInstance(Vehicle vehicleParam) {
        RoadsideListFragment fragment = new RoadsideListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VEHICLEDATA, vehicleParam);
        fragment.setArguments(args);
        return fragment;
    }

    public RoadsideListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vehicle = (Vehicle) getArguments().getSerializable(ARG_VEHICLEDATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roadside_list, container, false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.roadside_service_items, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.map);
        // layoutParams.setMargins(18, 18, 18, 18);

        view.setLayoutParams(layoutParams);

        addListHeaderView(view);

        return view;
    }

    @Override
    public void onClick(View view) {
        mListener.onFragmentInteraction(view);
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
        v.setSelected(true);
        String item = (String) l.getItemAtPosition(position);
        mListener.onFragmentInteraction(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Object data);
    }

    private void addListHeaderView(View view) {
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.fragment_roadside_list_header, listView, false);
        listView.addHeaderView(header);
    }
}
