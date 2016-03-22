package com.weel.mobile.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.weel.mobile.R;
import com.weel.mobile.android.model.MaintenanceItem;
import com.weel.mobile.android.view.properties.MaintenanceViewProperties;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jeremy.beckman on 2016-02-07.
 */
public class MaintenanceListAdapter extends ArrayAdapter<MaintenanceItem> {

    private LayoutInflater inflater;
    private List<MaintenanceItem> scheduleList;
    private HashMap<Integer, Boolean> selections;

    public MaintenanceListAdapter(Context context, List<MaintenanceItem> scheduleList) {
        super(context, R.layout.adapter_maintenance_list_item, scheduleList);
        this.inflater = LayoutInflater.from(context);
        this.scheduleList = scheduleList;
        selections = new HashMap<Integer, Boolean>();
    }

    @Override
    public int getCount() {
        return scheduleList.size();
    }

    @Override
    public MaintenanceItem getItem(int position) {
        return scheduleList.get(position);
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
        bind(scheduleList.get(position), view);

        if (selections.get(position) != null) {
            view.setBackgroundColor(0xFFC0C0C0);
        } else {
            view.setBackgroundColor(0xFFFFFFFF);
        }

        return view;
    }

    public HashMap<Integer, Boolean> getSelections() {
        return selections;
    }

    public void updateSelection(int position, boolean checked) {
        if (checked) {
            selections.put(position, checked);
        } else {
            selections.remove(position);
        }
        notifyDataSetChanged();
    }

    public void clearSelections() {
        selections.clear();
        notifyDataSetChanged();
    }

    public void addSelections(List<MaintenanceItem> list) {
        for (int n = 0; n < list.size(); n++) {
            selections.put(n, true);
        }
        notifyDataSetChanged();
    }

    public int selected() {
        return selections.size();
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_maintenance_list_item, parent, false);
            view.setTag(new MaintenanceViewProperties(view));
        }
        return view;
    }

    private void bind(MaintenanceItem item, View view) {
        MaintenanceViewProperties properties = (MaintenanceViewProperties) view.getTag();
        properties.serviceTypeView.setText(item.getName());
        properties.descriptionView.setText(item.getDescription());
    }
}
