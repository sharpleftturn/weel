package com.weel.mobile.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.weel.mobile.R;
import com.weel.mobile.android.adapter.MakeSpinnerAdapter;
import com.weel.mobile.android.adapter.ModelSpinnerAdapter;
import com.weel.mobile.android.adapter.YearSpinnerAdapter;
import com.weel.mobile.android.model.Make;
import com.weel.mobile.android.model.Model;
import com.weel.mobile.android.model.ModelYear;
import com.weel.mobile.android.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy.beckman on 2015-10-22.
 */
public class AddVehicleFragment extends Fragment {
    public static final String ACTION_GET_MAKE = "com.weel.mobile.action.GET_MAKE";
    public static final String ACTION_GET_MODEL = "com.weel.mobile.action.GET_MODEL";
    public static final String ACTION_GET_YEAR = "com.weel.mobile.action.GET_YEAR";
    public static final String ACTION_GET_MAKES = "com.weel.mobile.action.GET_MAKES";
    private static final String ARG_USERDATA = "com.weel.mobile.fragments.arguments.USERDATA";
    private static final String ARG_MAKEDATA = "com.weel.mobile.fragments.arguments.MAKEDATA";
    private static final String ARG_MODELDATA = "com.weel.mobile.fragments.arguments.MODELDATA";
    private static final String ARG_MODELYEARDATA = "com.weel.mobile.fragments.arguments.MODELYEARDATA";

    private User user;
    private List<Make> makes;
    private List<Model> models;
    private List<ModelYear> modelYears;
    private Spinner makeSpinner;
    private Spinner modelSpinner;
    private Spinner yearSpinner;

    private OnFragmentInteractionListener mListener;

    public static AddVehicleFragment newInstance(User user, ArrayList<Make> makes, ArrayList<Model> models, ArrayList<ModelYear> modelYears) {
        AddVehicleFragment fragment = new AddVehicleFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_USERDATA, user);
        args.putSerializable(ARG_MAKEDATA, makes);
        args.putSerializable(ARG_MODELDATA, models);
        args.putSerializable(ARG_MODELYEARDATA, modelYears);

        fragment.setArguments(args);
        return fragment;
    }

    public AddVehicleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(ARG_USERDATA);
            makes = (ArrayList<Make>) getArguments().getSerializable(ARG_MAKEDATA);
            models = (ArrayList<Model>) getArguments().getSerializable(ARG_MODELDATA);
            modelYears = (ArrayList<ModelYear>) getArguments().getSerializable(ARG_MODELYEARDATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_vehicle, container, false);

        makeSpinner = (Spinner) view.findViewById(R.id.make_spinner);
        makeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Make make = (Make) adapterView.getItemAtPosition(i);
                mListener.onAddVehicleFragmentInteraction(ACTION_GET_MAKE, make);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        updateMakes(makes);

        modelSpinner = (Spinner) view.findViewById(R.id.model_spinner);
        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Model model = (Model) adapterView.getItemAtPosition(i);
                mListener.onAddVehicleFragmentInteraction(ACTION_GET_MODEL, model);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        updateModels(models);

        yearSpinner = (Spinner) view.findViewById(R.id.year_spinner);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ModelYear modelYear = (ModelYear) adapterView.getItemAtPosition(i);
                mListener.onAddVehicleFragmentInteraction(ACTION_GET_YEAR, modelYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        updateModelYears(modelYears);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);

        mListener.onAddVehicleFragmentInteraction(ACTION_GET_MAKES, null);

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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onAddVehicleFragmentInteraction(String action, Object data);
    }

    public void updateMakes(List<Make> makeList) {
        MakeSpinnerAdapter makeAdapter = new MakeSpinnerAdapter(getActivity(), makeList);
        makeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        makeSpinner.setAdapter(makeAdapter);
    }

    public void updateModels(List<Model> modelList) {
        ModelSpinnerAdapter modelAdapter = new ModelSpinnerAdapter(getActivity(), modelList);
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelSpinner.setAdapter(modelAdapter);
    }

    public void updateModelYears(List<ModelYear> modelYearList) {
        YearSpinnerAdapter yearAdapter = new YearSpinnerAdapter(getActivity(), modelYearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
    }
}
