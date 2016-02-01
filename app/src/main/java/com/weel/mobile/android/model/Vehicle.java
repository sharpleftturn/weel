package com.weel.mobile.android.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jeremy.beckman on 2015-10-07.
 */
public class Vehicle implements Serializable {
    static final long serialVersionUID = 0L;

    private Long id;
    private String username;
    private String name;
    private String vin;
    private Make make = new Make();
    private Model model = new Model();
    private Integer year = 1973;
    private Long yearId;
    private Integer odometer = 0;
    private Integer annualDistance = 0;
    private String ownership;
    private Photo photo;
    private Boolean active;
    private Date inServiceDate;
    private Date created;
    private Date lastUpdated;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getVin() {
        return vin;
    }

    public void setMake(Make make) {
        this.make = make;
    }

    public Make getMake() {
        return make;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getYear() {
        return year;
    }

    public void setYearId(Long yearId) {
        this.yearId = yearId;
    }

    public Long getYearId() {
        return yearId;
    }

    public void setOdometer(Integer odometer) {
        this.odometer = odometer;
    }

    public Integer getOdometer() {
        return odometer;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setAnnualDistance(Integer annualDistance) {
        this.annualDistance = annualDistance;
    }

    public Integer getAnnualDistance() {
        return annualDistance;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public Date getInServiceDate() {
        return inServiceDate;
    }

    public void setInServiceDate(Date inServiceDate) {
        this.inServiceDate = inServiceDate;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCreated() {
        return created;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }
}
