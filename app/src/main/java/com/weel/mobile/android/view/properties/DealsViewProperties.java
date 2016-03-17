package com.weel.mobile.android.view.properties;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jeremy.beckman on 2016-02-29.
 */
public class DealsViewProperties {
    public final ImageView logoView;
    public final TextView nameView;
    public final TextView descriptionView;

    public DealsViewProperties(View view) {
        logoView = (ImageView) view.findViewWithTag("logo");
        nameView = (TextView) view.findViewWithTag("name");
        descriptionView = (TextView) view.findViewWithTag("description");
    }
}
