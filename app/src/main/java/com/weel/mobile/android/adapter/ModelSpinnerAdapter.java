package com.weel.mobile.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.weel.mobile.R;
import com.weel.mobile.android.model.Model;
import com.weel.mobile.android.view.properties.AddVehicleSpinnerViewProperties;

import java.util.List;

/**
 * Created by jeremy.beckman on 2015-08-18.
 */
public class ModelSpinnerAdapter extends ArrayAdapter<Model> {

    private LayoutInflater inflater;
    private List<Model> modelList;

    public ModelSpinnerAdapter(Context context, List<Model> modelList) {
        super(context, R.layout.add_vehicle_spinner_item, modelList);
        this.inflater = LayoutInflater.from(context);
        this.modelList = modelList;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Model getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflateIfRequired(view, position, parent);
        bind(modelList.get(position), view);
        return view;
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.add_vehicle_spinner_item, parent, false);
            view.setTag(new AddVehicleSpinnerViewProperties(view));
        }
        return view;
    }

    private void bind(Model model, View view) {
        AddVehicleSpinnerViewProperties properties = (AddVehicleSpinnerViewProperties) view.getTag();
        properties.displayNameView.setText(model.getDisplayName());
    }
}