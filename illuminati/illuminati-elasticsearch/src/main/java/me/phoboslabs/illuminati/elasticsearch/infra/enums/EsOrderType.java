package me.phoboslabs.illuminati.elasticsearch.infra.enums;

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

    public static EsOrderType getOrderType (final String orderType) {
        return "asc".equalsIgnoreCase(orderType) ? EsOrderType.ASC : EsOrderType.DESC;
    }
}
