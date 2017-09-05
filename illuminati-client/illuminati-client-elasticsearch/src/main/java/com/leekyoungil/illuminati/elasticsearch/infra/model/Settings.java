package com.leekyoungil.illuminati.elasticsearch.infra.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class Settings {

    @Expose @SerializedName("index.store.type") private String indexStoreType;

    public Settings (final String indexStoreType) {
        this.indexStoreType = indexStoreType;
    }
}
