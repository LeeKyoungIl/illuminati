package com.leekyoungil.illuminati.common.dto.enums;

public enum MappingType {

    TEXT("teXt"),
    KEYWORD("keyword");

    private String mappingType;

    MappingType(String mappingType) {
        this.mappingType = mappingType;
    }

    public String getMappingType () {
        return this.mappingType;
    }

    public static MappingType getMappingType (String mappingType) {
        if ("text".equalsIgnoreCase(mappingType) == true) {
            return MappingType.TEXT;
        } else if ("keyword".equalsIgnoreCase(mappingType) == true) {
            return MappingType.KEYWORD;
        } else {
            return null;
        }
    }
}
