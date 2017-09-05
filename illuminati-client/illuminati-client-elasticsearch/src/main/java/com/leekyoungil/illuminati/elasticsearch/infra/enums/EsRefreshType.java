package com.leekyoungil.illuminati.elasticsearch.infra.enums;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/5.3/docs-refresh.html#docs-refresh
 */
public enum EsRefreshType {
    FALSE("false"),         // ?refresh=false ( elastic search default)
    WAIT_FOR("wait_for"),   // ?refresh=wait_for
    TRUE("true");            // ?refresh=true

    String value;

    EsRefreshType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
