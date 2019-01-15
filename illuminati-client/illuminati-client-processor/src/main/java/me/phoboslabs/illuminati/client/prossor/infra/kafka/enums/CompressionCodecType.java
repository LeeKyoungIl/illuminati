package me.phoboslabs.illuminati.client.prossor.infra.kafka.enums;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public enum CompressionCodecType {

    NONE("none"),
    GZIP("gzip"),
    TEXT_PLAIN("text/plain"),
    SNAPPY("snappy");

    private final String type;

    CompressionCodecType (String type) {
        this.type = type;
    }

    public String getType () {
        return this.type;
    }
}
