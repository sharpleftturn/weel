package com.weel.mobile.android.model;

import android.os.Bundle;

/**
 * Created by jeremy.beckman on 2015-10-17.
 */
public class DrawerItem {
    private int icon;
    private String label;
    private Class activity;
    private Bundle extras;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setExtras(Bundle extras) {
        this.extras = extras;
    }

    public Bundle getExtras() {
        return extras;
    }

    public Class getActivity() {
        return activity;
    }

    public void setActivity(Class activity) {
        this.activity = activity;
    }
}
