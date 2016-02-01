package com.weel.mobile.android.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy.beckman on 2016-01-28.
 */
public class RoadsideIncident implements Serializable {
    private long id;
    private String address;
    private String routing;
    private int rating;
    private String feedback;
    private Double latitude;
    private Double longitude;
    private String type;
    private List<IncidentSource> source = new ArrayList<>();

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public String getRouting() {
        return routing;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public List<IncidentSource> getSource() {
        return source;
    }
}
