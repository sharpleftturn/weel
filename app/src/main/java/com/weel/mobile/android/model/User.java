package com.weel.mobile.android.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jeremy.beckman on 2015-10-07.
 */
public class User implements Serializable {
    static final long serialVersionUID = 0L;

    private long id;
    private String fullname;
    private String firstname;
    private String email;
    private Boolean active;
    private Date lastUpdated;
    private Date created;
    private String postalCode;
    private String uom;
    private int admin;
    private UserSession session;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCreated() {
        return created;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getUom() {
        return uom;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public int getAdmin() {
        return admin;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public UserSession getSession() {
        return session;
    }
}
