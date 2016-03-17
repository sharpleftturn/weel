package com.weel.mobile.android.view.properties;

import android.view.View;
import android.widget.TextView;

/**
 * Created by jeremy.beckman on 2016-02-07.
 */
public class MaintenanceViewProperties {
    public final TextView descriptionView;
    public final TextView serviceTypeView;

    public MaintenanceViewProperties(View view) {
        descriptionView = (TextView) view.findViewWithTag("description");
        serviceTypeView = (TextView) view.findViewWithTag("service_type");
    }
}
