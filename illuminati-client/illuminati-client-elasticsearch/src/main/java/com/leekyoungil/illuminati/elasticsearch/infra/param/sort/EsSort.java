package com.leekyoungil.illuminati.elasticsearch.infra.param.sort;

import com.google.gson.annotations.Expose;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsOrderType;

import java.util.HashMap;
import java.util.Map;

public class EsSort {

    @Expose
    private Map<String, String> sort = new HashMap<String, String>();

    public EsSort() {}

    public void setOrderDataToMap(String key, String orderByString) {
        if (StringObjectUtils.isValid(key) == true && StringObjectUtils.isValid(orderByString) == true) {
            this.sort.put(key, orderByString);
        }
    }

    public Map<String, String> getSort () {
        return this.sort.size() > 0 ? this.sort : null;
    }
}
