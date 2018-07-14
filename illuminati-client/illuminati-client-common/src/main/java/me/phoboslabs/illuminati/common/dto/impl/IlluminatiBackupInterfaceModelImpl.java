package me.phoboslabs.illuminati.common.dto.impl;

import me.phoboslabs.illuminati.common.dto.IlluminatiInterfaceModel;
import me.phoboslabs.illuminati.common.dto.enums.IlluminatiInterfaceType;

public class IlluminatiBackupInterfaceModelImpl implements IlluminatiInterfaceModel {

    private IlluminatiInterfaceType illuminatiInterfaceType;

    @Override public IlluminatiInterfaceType getInterfaceType() {
        return this.illuminatiInterfaceType;
    }

    @Override public void setIlluminatiInterfaceType(IlluminatiInterfaceType illuminatiInterfaceType) {
        this.illuminatiInterfaceType = illuminatiInterfaceType;
    }
}
