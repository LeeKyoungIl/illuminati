package me.phoboslabs.illuminati.gatekeeper.api.decorator;

import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.IlluminatiRestApiResult;

public class ApiJsonDecorator<T> implements ApiDecorator<T> {

    private final T data;

    public ApiJsonDecorator (T data) {
        this.data = data;
    }

    @Override public String getStringData () {
        return IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(this.data);
    }

    @Override public IlluminatiRestApiResult<T> getIlluminatiTypeObject() {
        IlluminatiRestApiResult<T> illuminatiRestApiResult = new IlluminatiRestApiResult<T>();
        if (this.data == null) {
            illuminatiRestApiResult.setCodeAndMessageOfResult(501);
        } else {
            illuminatiRestApiResult.setCodeAndMessageOfResult(200);
            illuminatiRestApiResult.setResult(this.data);
        }

        return illuminatiRestApiResult;
    }
}
