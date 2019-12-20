package me.phoboslabs.illuminati.hdfs.enums;

public enum HDFSSecurityAuthentication {

    // simple : No authentication.
    SIMPLE("simple"),
    // kerberos : Enable authentication by Kerberos.
    KERBEROS("kerberos");

    private final String authType;

    HDFSSecurityAuthentication(final String authType) {
        this.authType = authType;
    }

    public String getAuthType() {
        return this.authType;
    }
}
