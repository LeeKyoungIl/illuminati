package com.leekyoungil.illuminati.common.dto.impl;

import com.leekyoungil.illuminati.common.dto.IlluminatiInterfaceModel;
import com.leekyoungil.illuminati.common.dto.enums.IlluminatiInterfaceType;

import java.util.List;

public class IlluminatiFileBackupInterfaceModelImpl implements IlluminatiInterfaceModel {

    private List<String> dataList;
    private String data;
    private IlluminatiInterfaceType illuminatiInterfaceType;

    @Override public List<String> getDataList() {
        return this.dataList;
    }

    @Override public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    @Override public IlluminatiInterfaceType getInterfaceType() {
        return this.illuminatiInterfaceType;
    }

    @Override public void setIlluminatiInterfaceType(IlluminatiInterfaceType illuminatiInterfaceType) {
        this.illuminatiInterfaceType = illuminatiInterfaceType;
    }

    @Override public String getData() {
        return data;
    }

    @Override public void setData(String data) {
        this.data = data;
    }
}
