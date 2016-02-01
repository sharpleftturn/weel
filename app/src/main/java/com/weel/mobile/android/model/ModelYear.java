package com.weel.mobile.android.model;

import java.io.Serializable;

/**
 * Created by jeremy.beckman on 2015-10-07.
 */
public class ModelYear implements Serializable {
    static final long serialVersionUID = 0L;

    private Long id;
    private Long modelId;
    private Integer modelYear;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelYear(Integer modelYear) {
        this.modelYear = modelYear;
    }

    public Integer getModelYear() {
        return modelYear;
    }

    @Override
    public String toString() {
        return modelYear.toString();
    }
}
