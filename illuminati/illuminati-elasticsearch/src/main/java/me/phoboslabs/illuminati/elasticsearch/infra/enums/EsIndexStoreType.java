package me.phoboslabs.illuminati.elasticsearch.infra.enums;

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

    public static EsIndexStoreType getEnumType(final String type) throws Exception {
        switch (type) {
            case "fs" :
                return FS;
            case "simplefs" :
                return SIMPLEFS;
            case "niofs" :
                return NIOFS;
            case "mmapfs" :
                return MMAPFS;
            default:
                throw new Exception("check type value.");
        }
    }
}
