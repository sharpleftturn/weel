package com.weel.mobile.android.model;

import java.io.Serializable;

/**
 * Created by jeremy.beckman on 2015-10-07.
 */
public class Model implements Serializable {
    static final long serialVersionUID = 0L;

    private long id;
    private String name;
    private String displayName;
    private int year;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
