package com.weel.mobile.android.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by jeremy.beckman on 2016-02-29.
 */
public class Deal implements Serializable {
    static final long serialVersionUID = 0L;

    private long id;
    private String name;
    private String description;
    private String vendor;
    private String link;
    private String logoUrl;
    private String category;
    private Bitmap logoBitmap;

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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVendor() {
        return vendor;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setLogoBitmap(Bitmap logoBitmap) {
        this.logoBitmap = logoBitmap;
    }

    public Bitmap getLogoBitmap() {
        return logoBitmap;
    }
}
