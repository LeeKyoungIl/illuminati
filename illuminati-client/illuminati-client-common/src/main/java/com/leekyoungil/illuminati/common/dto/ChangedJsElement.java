package com.leekyoungil.illuminati.common.dto;

import com.google.gson.annotations.Expose;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangedJsElement {

    private String illuminatiSProcId;
    private String illuminatiGProcId;
    private List<ChangedValue> changedValues;
    @Expose private Map<Integer, ChangedValue> changedValueMap;

    public void setIlluminatiGProcId(String illuminatiGProcId) {
        this.illuminatiGProcId = illuminatiGProcId;
    }

    public void setIlluminatiSProcId(String illuminatiSProcId) {
        this.illuminatiSProcId = illuminatiSProcId;
    }

    public void setChangedValues(List<ChangedValue> changedValues) {
        this.changedValues = changedValues;
    }

    public String getIlluminatiGProcId() {
        return this.illuminatiGProcId;
    }

    public String getIlluminatiSProcId() {
        return this.illuminatiSProcId;
    }

    public void convertListToMap () {
        if (CollectionUtils.isNotEmpty(this.changedValues) == true) {
            if (this.changedValueMap == null) {
                this.changedValueMap = new HashMap<Integer, ChangedValue>();
            }

            for (int i=0; i<this.changedValues.size(); i++) {
                this.changedValueMap.put(i, this.changedValues.get(i));
            }

            this.changedValues = null;
        }
    }
}
