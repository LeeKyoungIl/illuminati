package me.phoboslabs.illuminati.elasticsearch.infra.enums;

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
        if ("match".equalsIgnoreCase(matchType)) {
            return EsQueryType.MATCH;
        } else if ("match_all".equalsIgnoreCase(matchType)) {
            return EsQueryType.MATCH_ALL;
        } else if ("term".equalsIgnoreCase(matchType)) {
            return EsQueryType.TERM;
        } else {
            return null;
        }
    }

    public static String getMatchText () {
        return EsQueryType.MATCH.name().toLowerCase();
    }
    public static String getMatchAllText () {
        return EsQueryType.MATCH_ALL.name().toLowerCase();
    }
    public static String getTermText () {
        return EsQueryType.TERM.name().toLowerCase();
    }
}
