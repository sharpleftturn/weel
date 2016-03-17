package com.weel.mobile.android.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy.beckman on 2016-02-06.
 */
public class Maintenance implements Serializable {
    public static final long serialVersionUID = 0L;
    public static final int FORMAT_MI = 1;
    public static final int FORMAT_KM = 2;

    private String milestone;
    private Integer intervalMonths;
    private Integer intervalKilometers;
    private Integer intervalMiles;
    private List<MaintenanceItem> items = new ArrayList<MaintenanceItem>();

    public void setIntervalMonths(Integer intervalMonths) {
        this.intervalMonths = intervalMonths;
    }

    public Integer getIntervalMonths() {
        return intervalMonths;
    }

    public void setIntervalKilometers(Integer intervalKilometers) {
        this.intervalKilometers = intervalKilometers;
    }

    public Integer getIntervalKilometers() {
        return intervalKilometers;
    }

    public void setIntervalMiles(Integer intervalMiles) {
        this.intervalMiles = intervalMiles;
    }

    public Integer getIntervalMiles() {
        return intervalMiles;
    }

    public void setItems(List<MaintenanceItem> items) {
        this.items = items;
    }

    public List<MaintenanceItem> getItems() {
        return items;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }

    public String getMilestone() {
        return milestone;
    }
}
