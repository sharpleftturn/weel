package com.weel.mobile.android.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jeremy.beckman on 2015-10-07.
 */
public class UserSession implements Serializable {
    static final long serialVersionUID = 0L;

    private long uid;
    private Date created;
    private String session;
    private Date expires;

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getUid() {
        return uid;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCreated() {
        return created;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSession() {
        return session;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public Date getExpires() {
        return expires;
    }
}
