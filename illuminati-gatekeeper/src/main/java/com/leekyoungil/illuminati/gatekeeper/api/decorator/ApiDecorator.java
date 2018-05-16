package com.leekyoungil.illuminati.gatekeeper.api.decorator;

import com.leekyoungil.illuminati.common.dto.IlluminatiRestApiResult;

public interface ApiDecorator<T> {

    String getStringData ();

    IlluminatiRestApiResult<T> getIlluminatiTypeObject();
}
