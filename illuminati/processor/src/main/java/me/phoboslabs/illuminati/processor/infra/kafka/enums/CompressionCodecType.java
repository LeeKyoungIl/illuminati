/*
 * Copyright 2017 Phoboslabs.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.phoboslabs.illuminati.processor.infra.kafka.enums;

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

    CompressionCodecType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public static CompressionCodecType getCompressionCodecType(String type) {
        switch (type.toLowerCase()) {
            case "gzip":
                return GZIP;
            case "text/plain":
                return TEXT_PLAIN;
            case "snappy":
                return SNAPPY;
            case "lz4":
                return LZ4;
            case "zstd":
                return ZSTD;
            default:
                return NONE;
        }
    }
}
