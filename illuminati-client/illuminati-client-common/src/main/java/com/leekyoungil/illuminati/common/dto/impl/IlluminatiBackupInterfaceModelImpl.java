package com.leekyoungil.illuminati.common.dto.impl;

import com.leekyoungil.illuminati.common.dto.IlluminatiInterfaceModel;
import com.leekyoungil.illuminati.common.dto.enums.IlluminatiInterfaceType;

import java.util.List;

public class IlluminatiBackupInterfaceModelImpl implements IlluminatiInterfaceModel {

    private IlluminatiInterfaceType illuminatiInterfaceType;

    @Override public IlluminatiInterfaceType getInterfaceType() {
        return this.illuminatiInterfaceType;
    }

    @Override public void setIlluminatiInterfaceType(IlluminatiInterfaceType illuminatiInterfaceType) {
        this.illuminatiInterfaceType = illuminatiInterfaceType;
    }
}
