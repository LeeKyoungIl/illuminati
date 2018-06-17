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
        if ("gt".equalsIgnoreCase(rangeType) == true) {
            return EsRangeType.GT;
        } else if ("gt".equalsIgnoreCase(rangeType) == true) {
            return EsRangeType.LT;
        } else if ("gt".equalsIgnoreCase(rangeType) == true) {
            return EsRangeType.GTE;
        } else if ("gt".equalsIgnoreCase(rangeType) == true) {
            return EsRangeType.LTE;
        } else {
            return null;
        }
    }
}
