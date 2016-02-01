package com.weel.mobile.android.view.properties;

import android.view.View;
import android.widget.TextView;

/**
 * Created by jeremy.beckman on 2016-01-16.
 */
public class NavigationViewProperties {

    public final TextView label;

    public NavigationViewProperties(View view) {
        label = (TextView) view.findViewWithTag("drawer");
    }
}
