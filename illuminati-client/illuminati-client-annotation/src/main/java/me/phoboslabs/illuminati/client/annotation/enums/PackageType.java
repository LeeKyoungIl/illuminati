package me.phoboslabs.illuminati.client.annotation.enums;

public enum PackageType {

    CONTROLLER("controller"),
    SERVICE("service"),
    COMPONENT("component"),
    JAVASCRIPT("javascript"),
    DEFAULT("default");

    private final String packageType;

    PackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getPackageType () {
        return this.packageType;
    }
}
