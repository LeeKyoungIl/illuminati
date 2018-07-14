package me.phoboslabs.illuminati.client.prossor.infra.backup.enums;

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

    public static TableDDLType getEnumType(String type) {
        if ("create".equals(type)) {
            return CREATE;
        } else if ("drop".equals(type)) {
            return DROP;
        }

        return null;
    }
}
