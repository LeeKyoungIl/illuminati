package com.leekyoungil.illuminati.common.dto;

import com.leekyoungil.illuminati.common.dto.enums.IlluminatiInterfaceType;

import java.util.List;

public interface IlluminatiInterfaceModel {

    IlluminatiInterfaceType getInterfaceType();

    void setIlluminatiInterfaceType (IlluminatiInterfaceType illuminatiInterfaceType);

    String getData();

    void setData(String data);

    List<String> getDataList();

    void setDataList(List<String> dataList);
}
