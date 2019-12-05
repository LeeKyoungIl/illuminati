package me.phoboslabs.illuminati.processor.infra.backup.enums;

public enum TableDDLType {

    CREATE("create"),
    DROP("drop");

    private final String type;

    TableDDLType (String type) {
        this.type = type;
    }

    public String getType () {
        return this.type;
    }

    public static TableDDLType getEnumType(String type) throws Exception {
        switch (type.toLowerCase()) {
            case "create" :
                return CREATE;
            case "drop" :
                return DROP;
            default :
                throw new Exception(type + " is not support yet.");
        }
    }
}
