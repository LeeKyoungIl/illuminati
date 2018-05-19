package com.leekyoungil.illuminati.gatekeeper.api.controller.param;

public enum OrderType {

    ASC("asc"),
    DESC("desc");

    private String orderType;

    OrderType (String orderType) {
        this.orderType = orderType;
    }

    public String getOrderType () {
        return this.orderType;
    }

    public static OrderType getOrderType (String orderType) {
        switch (orderType.toLowerCase()) {
            case "asc" :
                return OrderType.ASC;

            case "desc" :
                return OrderType.DESC;

            default :
                return null;
        }
    }
}
