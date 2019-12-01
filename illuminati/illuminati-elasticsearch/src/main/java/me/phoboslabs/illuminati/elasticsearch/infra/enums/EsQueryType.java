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

    public static EsQueryType getMatchType (final String matchType) throws Exception {
        switch (matchType) {
            case "match" :
                return EsQueryType.MATCH;
            case "match_all" :
                return EsQueryType.MATCH_ALL;
            case "term" :
                return EsQueryType.TERM;
            default:
                throw new Exception("matchType must not be null.");
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
