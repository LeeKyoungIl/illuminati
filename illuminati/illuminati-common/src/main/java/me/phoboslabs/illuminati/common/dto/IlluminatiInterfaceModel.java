package me.phoboslabs.illuminati.common.dto;

import me.phoboslabs.illuminati.common.dto.enums.IlluminatiInterfaceType;

public interface IlluminatiInterfaceModel {

    String ILLUMINATI_GPROC_ID_KEY = "illuminatiGProcId";
    String ILLUMINATI_SPROC_ID_KEY = "illuminatiSProcId";
    String ILLUMINATI_UNIQUE_USER_ID_KEY = "illuminatiUniqueUserId";

    IlluminatiInterfaceType getInterfaceType() throws Exception;

    void setIlluminatiInterfaceType(IlluminatiInterfaceType illuminatiInterfaceType) throws Exception;
}
