package com.leekyoungil.illuminati.elasticsearch.infra.param;

import com.google.gson.annotations.Expose;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsOrderType;

import java.util.HashMap;
import java.util.Map;

public class RequestEsSortParam {

    @Expose
    private Map<String, String> sort = new HashMap<String, String>();

    public RequestEsSortParam (EsOrderType orderType, String key) {
        String orderByString = orderType.getOrderType();

        if (StringObjectUtils.isValid(orderByString) == true) {
            this.sort.put(key, orderByString);
        }
    }

    public Map<String, String> getSort () {
        return this.sort.size() > 0 ? this.sort : null;
    }
}
