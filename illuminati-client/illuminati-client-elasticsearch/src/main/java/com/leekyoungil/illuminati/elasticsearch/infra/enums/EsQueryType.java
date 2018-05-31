package com.leekyoungil.illuminati.elasticsearch.infra.enums;

public enum EsQueryType {
    MATCH("match"),
    MATCH_ALL("match_all"),
    TERM("term");

    private String matchType;

    EsQueryType (String matchType) {
        this.matchType = matchType;
    }

    public String getMatchType () {
        return this.matchType;
    }

    public static EsQueryType getMatchType (String matchType) {
        switch (matchType.toLowerCase()) {
            case "match" :
                return EsQueryType.MATCH;

            case "match_all" :
                return EsQueryType.MATCH_ALL;

            case "term" :
                return EsQueryType.TERM;

            default :
                return null;
        }
    }
}
