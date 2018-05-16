package com.leekyoungil.illuminati.elasticsearch.infra;

import java.util.Map;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public interface EsClient<T, V> {

    V save(T entity);

    String getDataByParam(Map<String, Object> param);

//    V save(T entity, EsRefreshType esRefreshType);

    // V update(T entity);

    // V update(T entity, EsRefreshType esRefreshType);
}
