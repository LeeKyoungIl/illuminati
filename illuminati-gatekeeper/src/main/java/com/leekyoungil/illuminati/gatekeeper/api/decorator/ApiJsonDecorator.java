package com.leekyoungil.illuminati.gatekeeper.api.decorator;

import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.dto.IlluminatiJsonResult;

public class ApiJsonDecorator<T> {

    private final T data;

    ApiJsonDecorator (T data) {
        this.data = data;
    }

    public String getJsonString () {
        return IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(this.data);
    }

    public IlluminatiJsonResult<T> getIlluminatiJsonObject() {
        IlluminatiJsonResult<T> illuminatiJsonResult = new IlluminatiJsonResult<T>();
        if (this.data == null) {
            illuminatiJsonResult.setCodeAndMessageOfResult(501);
        } else {
            illuminatiJsonResult.setCodeAndMessageOfResult(200);
            illuminatiJsonResult.setResult(this.data);
        }

        return illuminatiJsonResult;
    }
}
