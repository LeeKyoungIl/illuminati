package me.phoboslabs.illuminati.elasticsearch.infra.param.sort;

import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import me.phoboslabs.illuminati.elasticsearch.infra.enums.EsOrderType;

import java.util.Map;

public class EsSortBuilder {

    private final EsSort esSort = new EsSort();

    public static EsSortBuilder Builder() {
        return new EsSortBuilder();
    }

    private EsSortBuilder () {

    }

    public EsSortBuilder setSort (EsOrderType orderType, String key) {
        String orderByString = orderType.getOrderType();

        if (StringObjectUtils.isValid(orderByString)) {
            this.esSort.setOrderDataToMap(key, orderByString);
        }

        return this;
    }

    public Map<String, String> build () {
        return this.esSort.getSort();
    }
}
