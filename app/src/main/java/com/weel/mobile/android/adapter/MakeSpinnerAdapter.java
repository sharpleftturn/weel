package com.weel.mobile.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.weel.mobile.android.R;
import com.weel.mobile.android.model.Make;
import com.weel.mobile.android.view.properties.AddVehicleSpinnerViewProperties;

import java.util.List;

/**
 * Created by jeremy.beckman on 2015-08-18.
 */
public class MakeSpinnerAdapter extends ArrayAdapter<Make> {

    private LayoutInflater inflater;
    private List<Make> makeList;

    public MakeSpinnerAdapter(Context context, List<Make> makeList) {
        super(context, R.layout.add_vehicle_spinner_item, makeList);
        this.inflater = LayoutInflater.from(context);
        this.makeList = makeList;
    }

    @Override
    public int getCount() {
        return makeList.size();
    }

    @Override
    public Make getItem(int position) {
        return makeList.get(position);
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
        bind(makeList.get(position), view);
        return view;
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.add_vehicle_spinner_item, parent, false);
            view.setTag(new AddVehicleSpinnerViewProperties(view));
        }
        return view;
    }

    private void bind(Make make, View view) {
        AddVehicleSpinnerViewProperties properties = (AddVehicleSpinnerViewProperties) view.getTag();
        properties.displayNameView.setText(make.getDisplayName());
    }
}
