package me.phoboslabs.illuminati.backup.enums;

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

    public static IlluminatiStorageType getEnumType(final String type) throws Exception {
        switch (type) {
            case "org.h2.Driver" :
                return H2;
            case "mysql" :
                return MYSQL;
            case "file" :
                return FILE;
            case "broker" :
                return BROKER;
            default :
                throw new Exception(type + " is not support yet.");
        }
    }
}