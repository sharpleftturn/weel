package com.weel.mobile.android.model;

import java.io.Serializable;

/**
 * Created by jeremy.beckman on 2016-02-06.
 */
public class MaintenanceItem implements Serializable {
    public static final long serialVersionUID = 0L;
    public Boolean isSeparator = false;

    private Integer months;
    private Integer kilometres;
    private Integer miles;
    private String name;
    private String milestone;
    private String description;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public Integer getMonths() {
        return months;
    }

    public void setKilometres(Integer kilometres) {
        this.kilometres = kilometres;
    }

    public Integer getKilometres() {
        return kilometres;
    }

    public void setMiles(Integer miles) {
        this.miles = miles;
    }

    public Integer getMiles() {
        return miles;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }

    public String getMilestone() {
        return milestone;
    }
}
