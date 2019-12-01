package me.phoboslabs.illuminati.elasticsearch.infra.enums;

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

    public static EsRangeType getRangeType (final String rangeType) throws Exception {
        switch (rangeType) {
            case "gt" :
                return EsRangeType.GT;
            case "lt" :
                return EsRangeType.LT;
            case "gte" :
                return EsRangeType.GTE;
            case "lte" :
                return EsRangeType.LTE;
            default:
                throw new Exception("rangeType must not be null.");
        }
    }
}
