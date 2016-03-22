package com.weel.mobile.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.weel.mobile.R;
import com.weel.mobile.android.model.ModelYear;
import com.weel.mobile.android.view.properties.AddVehicleSpinnerViewProperties;

import java.util.List;

/**
 * Created by jeremy.beckman on 2015-08-24.
 */
public class YearSpinnerAdapter extends ArrayAdapter<ModelYear> {

    private LayoutInflater inflater;
    private List<ModelYear> modelYearList;

    public YearSpinnerAdapter(Context context, List<ModelYear> modelYearList) {
        super(context, R.layout.add_vehicle_spinner_item, modelYearList);
        this.inflater = LayoutInflater.from(context);
        this.modelYearList = modelYearList;
    }

    @Override
    public int getCount() {
        return modelYearList.size();
    }

    @Override
    public ModelYear getItem(int position) {
        return modelYearList.get(position);
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
        bind(modelYearList.get(position), view);
        return view;
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.add_vehicle_spinner_item, parent, false);
            view.setTag(new AddVehicleSpinnerViewProperties(view));
        }
        return view;
    }

    private void bind(ModelYear modelYear, View view) {
        AddVehicleSpinnerViewProperties properties = (AddVehicleSpinnerViewProperties) view.getTag();
        properties.displayNameView.setText(modelYear.getModelYear().toString());
    }
}
