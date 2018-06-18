package com.leekyoungil.illuminati.elasticsearch.infra;

import com.leekyoungil.illuminati.elasticsearch.model.IlluminatiEsModel;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public interface EsClient<T, V> {

    V save(T entity);

    String getDataByJson(T type, String jsonRequestString);

    String getMappingByIndex(T type);

//    V save(T entity, EsRefreshType esRefreshType);

    // V update(T entity);

    // V update(T entity, EsRefreshType esRefreshType);
}
