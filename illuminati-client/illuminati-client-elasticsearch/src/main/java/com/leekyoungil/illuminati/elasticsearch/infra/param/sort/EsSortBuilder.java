package com.leekyoungil.illuminati.elasticsearch.infra.param.sort;

import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsOrderType;

import java.util.Map;

public class EsSortBuilder {

    private final EsSort esSort = new EsSort();

    public EsSortBuilder () {

    }

    public EsSortBuilder setSort (EsOrderType orderType, String key) {
        String orderByString = orderType.getOrderType();

        if (StringObjectUtils.isValid(orderByString) == true) {
            this.esSort.setOrderDataToMap(key, orderByString);
        }

        return this;
    }

    public Map<String, String> build () {
        return this.esSort.getSort();
    }
}
