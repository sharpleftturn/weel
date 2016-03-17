package com.weel.mobile.android.model;

import java.io.Serializable;

/**
 * Created by jeremy.beckman on 2015-10-07.
 */
public class Photo implements Serializable {
    static final long serialVersionUID = 0L;

    private Long id;
    private String thumbnailUrl;
    private String fullUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }
}
