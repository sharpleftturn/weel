package com.weel.mobile.android.view.properties;

import android.view.View;
import android.widget.TextView;

import com.weel.mobile.android.config.Constants;

import org.w3c.dom.Text;

/**
 * Created by jeremy.beckman on 2016-02-27.
 */
public class ServiceHistoryViewProperties {

    public TextView serviceHistoryName;
    public TextView serviceHistoryDate;
    public TextView serviceHistoryCost;
    public TextView serviceHistoryLocation;
    public TextView serviceHistoryStatus;

    public ServiceHistoryViewProperties(View view) {
        serviceHistoryName = (TextView) view.findViewWithTag(Constants.NAME);
        serviceHistoryDate = (TextView) view.findViewWithTag(Constants.DATE);
        serviceHistoryCost = (TextView) view.findViewWithTag(Constants.COST);
        serviceHistoryLocation = (TextView) view.findViewWithTag(Constants.LOCATION);
        serviceHistoryStatus = (TextView) view.findViewWithTag(Constants.STATUS);
    }
}
