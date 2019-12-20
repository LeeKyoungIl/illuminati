package me.phoboslabs.illuminati.hdfs.enums;

public enum HDFSSecurityAuthorization {

    TRUE("true"),
    FALSE("false");

    private final String isAuthorization;

    HDFSSecurityAuthorization(final String isAuthorization) {
        this.isAuthorization = isAuthorization;
    }

    public String getIsAuthorization() {
        return this.isAuthorization;
    }
}
