package com.leekyoungil.illuminati.elasticsearch.infra.enums;

public enum EsRangeType {

    GT("gt"), // >
    LT("lt"), // <
    GTE("gte"), // >=
    LTE("lte"); // <=

    private String rangeType;

    EsRangeType (String rangeType) {
        this.rangeType = rangeType;
    }

    public String getRangeType () {
        return this.rangeType;
    }

    public static EsRangeType getRangeType (String rangeType) {
        if ("gt".equalsIgnoreCase(rangeType)) {
            return EsRangeType.GT;
        } else if ("lt".equalsIgnoreCase(rangeType)) {
            return EsRangeType.LT;
        } else if ("gte".equalsIgnoreCase(rangeType)) {
            return EsRangeType.GTE;
        } else if ("lte".equalsIgnoreCase(rangeType)) {
            return EsRangeType.LTE;
        } else {
            return null;
        }
    }
}
