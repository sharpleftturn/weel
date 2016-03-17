package com.weel.mobile.android.model;

import java.io.Serializable;

/**
 * Created by jeremy.beckman on 2016-03-01.
 */
public class MechanicIncident implements Serializable {
    static final long serialVersionUID = 0L;

    private long id;
    private int rating;
    private String feedback;
    private IncidentSource source;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }


    public void setIncidentSource(IncidentSource source) {
        this.source = source;
    }

    public IncidentSource getIncidentSource() {
        return source;
    }
}
