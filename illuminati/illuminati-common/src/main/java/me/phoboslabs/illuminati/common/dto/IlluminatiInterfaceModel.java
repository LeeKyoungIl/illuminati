package me.phoboslabs.illuminati.common.dto;

import me.phoboslabs.illuminati.common.dto.enums.IlluminatiInterfaceType;

public interface IlluminatiInterfaceModel {

    static final String ILLUMINATI_GPROC_ID_KEY = "illuminatiGProcId";
    static final String ILLUMINATI_SPROC_ID_KEY = "illuminatiSProcId";
    static final String ILLUMINATI_UNIQUE_USER_ID_KEY = "illuminatiUniqueUserId";

    IlluminatiInterfaceType getInterfaceType() throws Exception;

    void setIlluminatiInterfaceType(IlluminatiInterfaceType illuminatiInterfaceType) throws Exception;
}
