package com.weel.mobile.android.model;

import java.io.Serializable;

/**
 * Created by jeremy.beckman on 2016-01-28.
 */
public class IncidentSource implements Serializable {
    private String program;
    private String description;
    private String phone;

    public void setProgram(String program) {
        this.program = program;
    }

    public String getProgram() {
        return program;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
