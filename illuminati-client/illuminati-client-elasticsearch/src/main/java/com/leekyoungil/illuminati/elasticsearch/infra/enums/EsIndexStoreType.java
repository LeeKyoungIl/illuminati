package com.leekyoungil.illuminati.elasticsearch.infra.enums;

/**
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules-store.html
 */
public enum EsIndexStoreType {

    FS("fs"),
    SIMPLEFS("simplefs"),
    NIOFS("niofs"),
    MMAPFS("mmapfs");

    private final String type;

    EsIndexStoreType (final String type) {
        this.type = type;
    }

    public String getType () {
        return this.type;
    }

    public static EsIndexStoreType getEnumType(final String type) {
        if ("fs".equals(type)) {
            return FS;
        } else if ("simplefs".equals(type)) {
            return SIMPLEFS;
        } else if ("niofs".equals(type)) {
            return NIOFS;
        } else if ("mmapfs".equals(type)) {
            return MMAPFS;
        }

        return null;
    }
}
