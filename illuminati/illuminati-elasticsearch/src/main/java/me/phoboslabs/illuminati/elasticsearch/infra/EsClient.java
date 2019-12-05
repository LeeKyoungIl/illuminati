package me.phoboslabs.illuminati.elasticsearch.infra;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public interface EsClient<T, V> {

    V save(T entity) throws Exception;

    String getDataByJson(String jsonRequestString) throws Exception;

    String getMappingByIndex(T type) throws Exception;

//    V save(T entity, EsRefreshType esRefreshType);

    // V update(T entity);

    // V update(T entity, EsRefreshType esRefreshType);
}
