package me.phoboslabs.illuminati.common.dto.enums;

public enum IlluminatiStorageType {

    H2("org.h2.Driver"),
    MYSQL("mysql"),
    FILE("file"),
    BROKER("broker");

    private final String type;

    IlluminatiStorageType(String type) {
        this.type = type;
    }

    public String getType () {
        return this.type;
    }

    public static IlluminatiStorageType getEnumType(String type) {
        if ("org.h2.Driver".equals(type)) {
            return H2;
        } else if ("mysql".equals(type)) {
            return MYSQL;
        } else if ("file".equals(type)) {
            return FILE;
        } else if ("broker".equals(type)) {
            return BROKER;
        }

        return null;

    }
}
