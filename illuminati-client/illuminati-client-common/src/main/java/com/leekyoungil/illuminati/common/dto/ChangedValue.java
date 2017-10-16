package com.leekyoungil.illuminati.common.dto;

import com.google.gson.annotations.Expose;

public class ChangedValue {

    @Expose private String elementUniqueId;
    @Expose private String elementType;
    @Expose private String attributeName;
    @Expose private String newData;
    @Expose private String oldData;

    public void setElementUniqueId(String elementUniqueId) {
        this.elementUniqueId = elementUniqueId;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }

    public void setOldData(String oldData) {
        this.oldData = oldData;
    }
}

