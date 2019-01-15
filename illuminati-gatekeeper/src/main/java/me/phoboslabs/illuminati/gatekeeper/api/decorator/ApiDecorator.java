package me.phoboslabs.illuminati.gatekeeper.api.decorator;

import me.phoboslabs.illuminati.common.dto.IlluminatiRestApiResult;

public interface ApiDecorator<T> {

    String getStringData ();

    IlluminatiRestApiResult<T> getIlluminatiTypeObject();
}
