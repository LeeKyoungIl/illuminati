package com.leekyoungil.illuminati.common.dto;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ChangedJsElement {

    private String illuminatiGProcId;
    @Expose private List<ChangedValue> changedValues;

    public void setIlluminatiGProcId(String illuminatiGProcId) {
        this.illuminatiGProcId = illuminatiGProcId;
    }

    public void setChangedValues(List<ChangedValue> changedValues) {
        this.changedValues = changedValues;
    }

    public String getIlluminatiGProcId() {
        return this.illuminatiGProcId;
    }
}
