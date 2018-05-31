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
        switch (rangeType.toLowerCase()) {
            case "gt" :
                return EsRangeType.GT;

            case "lt" :
                return EsRangeType.LT;

            case "gte" :
                return EsRangeType.GTE;

            case "lte" :
                return EsRangeType.LTE;

            default :
                return null;
        }
    }
}
