package com.weel.mobile.android.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by jeremy.beckman on 2016-02-09.
 */
public class ServiceRecord implements Serializable {
    public static final long serialVersionUID = 0L;

    private Long id;
    private String description;
    private Double cost;
    private String location;
    private Date serviceDate;
    private Integer mileage;
    private Boolean processed;
    private Long vehicleId;
    private Date created;
    private Date lastUpdated;
    private List<ServiceAttachment> attachments;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getVehicleId() {
        return this.vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public Date getCreated() {
        return lastUpdated;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<ServiceAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ServiceAttachment> attachments) {
        this.attachments = attachments;
    }
}
