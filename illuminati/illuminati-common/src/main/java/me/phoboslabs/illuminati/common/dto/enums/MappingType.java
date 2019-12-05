package me.phoboslabs.illuminati.common.dto.enums;

public enum MappingType {

    TEXT("text"),
    KEYWORD("keyword");

    private String mappingType;

    MappingType(String mappingType) {
        this.mappingType = mappingType;
    }

    public String getMappingType () {
        return this.mappingType;
    }

    public static MappingType getMappingType (final String mappingType) throws Exception {
        switch (mappingType.toLowerCase()) {
            case "text" :
                return MappingType.TEXT;
            case "keyword" :
                return MappingType.KEYWORD;
            default :
                throw new Exception(mappingType + " is not support yet.");
        }
    }
}
