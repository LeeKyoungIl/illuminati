package com.leekyoungil.illuminati.elasticsearch.infra.enums;

public enum EsOrderType {

    ASC("asc"),
    DESC("desc");

    private String orderType;

    EsOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderType () {
        return this.orderType;
    }

    public static EsOrderType getOrderType (String orderType) {
        switch (orderType.toLowerCase()) {
            case "asc" :
                return EsOrderType.ASC;

            case "desc" :
                return EsOrderType.DESC;

            default :
                return null;
        }
    }
}
