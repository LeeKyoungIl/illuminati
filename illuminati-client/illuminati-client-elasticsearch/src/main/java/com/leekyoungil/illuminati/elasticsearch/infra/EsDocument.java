package com.leekyoungil.illuminati.elasticsearch.infra;

import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsIndexStoreType;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsRefreshType;

import java.lang.annotation.*;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EsDocument {

    String indexName();

    String type();

    EsIndexStoreType indexStoreType();

    int shards();

    int replicas();

    EsRefreshType refreshType();
}
