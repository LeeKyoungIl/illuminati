package me.phoboslabs.illuminati.processor.infra.kafka.enums;

import me.phoboslabs.illuminati.processor.infra.backup.enums.TableDDLType;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public enum CompressionCodecType {

    NONE("none"),
    GZIP("gzip"),
    TEXT_PLAIN("text/plain"),
    SNAPPY("snappy"),
    LZ4("lz4"),
    ZSTD("zstd");

    private final String type;

    CompressionCodecType (String type) {
        this.type = type;
    }

    public String getType () {
        return this.type;
    }

    public static CompressionCodecType getCompressionCodecType(String type) {
        switch (type.toLowerCase()) {
            case "gzip" :
                return GZIP;
            case "text/plain" :
                return TEXT_PLAIN;
            case "snappy" :
                return SNAPPY;
            case "lz4" :
                return LZ4;
            case "zstd" :
                return ZSTD;
            default :
                return NONE;
        }
    }
}
