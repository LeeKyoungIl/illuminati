package me.phoboslabs.illuminati.common.dto;

import com.google.gson.annotations.Expose;

public class ChangedValue {

    @Expose private String elementUniqueId;
    @Expose private String elementType;
    @Expose private String attributeName;
    @Expose private String newData;
    @Expose private String oldData;
    @Expose private String index;

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

    public void setIndex(String index) {
        this.index = index;
    }

    public String getElementUniqueId() {
        return this.elementUniqueId;
    }

    public String getElementType() {
        return this.elementType;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public String getNewData() {
        return this.newData;
    }

    public String getOldData() {
        return this.oldData;
    }

    public String getIndex() {
        return this.index;
    }
}

