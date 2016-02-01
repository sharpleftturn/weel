package com.weel.mobile.android.view.properties;

import android.view.View;
import android.widget.TextView;

/**
 * Created by jeremy.beckman on 2015-08-18.
 */
public class AddVehicleSpinnerViewProperties {

    public final TextView displayNameView;

    public AddVehicleSpinnerViewProperties(View view) {
        displayNameView = (TextView) view.findViewWithTag("displayName");
    }
}
