package com.leekyoungil.illuminati.gatekeeper.api.decorator;

import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;

import java.util.List;
import java.util.Map;

public class ApiJsonDecorator {

    private final List<Map<String, Object>> objectList;

    ApiJsonDecorator (List<Map<String, Object>> objectList) {
        this.objectList = objectList;
    }

    public String getJsonString () {
        return IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(this.objectList);
    }
}
