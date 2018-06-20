package com.leekyoungil.illuminati.elasticsearch.infra;

import com.google.gson.JsonSyntaxException;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import com.leekyoungil.illuminati.elasticsearch.model.IlluminatiEsModel;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class ESclientImpl implements EsClient<IlluminatiEsModel, HttpResponse> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private HttpClient httpClient;
    private String esUrl;
    private String optionalIndex = "";
    private String esAuthString;

    private final static String IS_RESPONSE_JSON = "pretty";

    private final static String INDEX_IS_NOT_EXISTS_STATUS_OF_KEY = "status";
    private final static double INDEX_IS_NOT_EXISTS_KEY_IS_STATUS_VALUE = 404d;

    private final static String ES_SEARCH_KEYWORD = "search";
    private final static String ES_MAPPING_KEYWORD = "mapping";

    public ESclientImpl (final HttpClient httpClient, final String esUrl, final int esPort) {
        this.httpClient = httpClient;
        this.esUrl = "http://" + esUrl + ":" + String.valueOf(esPort);
    }

    public void setOptionalIndex (String optionalIndex) {
        this.optionalIndex = optionalIndex;
    }

    @Override public HttpResponse save (final IlluminatiEsModel entity) {
        this.esAuthString = entity.getEsAuthString();
        this.checkIndexAndGenerate(entity);
        return this.saveToEs(entity.getEsUrl(this.esUrl + this.optionalIndex), entity.getJsonString());
    }

    private HttpResponse saveToEs (String esRequestUrl, String jsonString) {
        final HttpRequestBase httpPutRequest = new HttpPut(esRequestUrl);

        if (StringObjectUtils.isValid(this.esAuthString)) {
            try {
                httpPutRequest.setHeader("Authorization", "Basic " + this.esAuthString);
            } catch (Exception ex) {
                this.logger.error("Sorry. something is wrong in encoding es user auth info. ("+ex.toString()+")");
            }
        }

        ((HttpPut) httpPutRequest).setEntity(this.getHttpEntity(jsonString));

        HttpResponse httpResponse = null;

        try {
            httpResponse = this.httpClient.execute(httpPutRequest);
        } catch (IOException e) {
            this.logger.error("Sorry. something is wrong in Http Request. ("+e.toString()+")");
        } finally {
            try {
                httpPutRequest.releaseConnection();
            } catch (Exception ignored) {}
        }

        if (httpResponse == null) {
            httpResponse = getHttpResponseByData(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Sorry. something is wrong in Http Request.");
        }

        return httpResponse;
    }

    @Override public String getDataByJson(final IlluminatiEsModel entity, final String jsonRequestString) {
        if (StringObjectUtils.isValid(jsonRequestString) == false) {
            return null;
        }
        final HttpRequestBase httpPostRequest = new HttpPost(this.getRequestUrl(entity, ES_SEARCH_KEYWORD));
        ((HttpPost) httpPostRequest).setEntity(this.getHttpEntity(jsonRequestString));

        return this.requestToEsByHttp(httpPostRequest);
    }

    @Override public String getMappingByIndex(final IlluminatiEsModel entity) {
        return this.requestToEsByHttp(new HttpGet(this.getRequestUrl(entity, ES_MAPPING_KEYWORD)));
    }

    private String getRequestUrl (final IlluminatiEsModel entity, String command) {
        StringBuilder baseEsHttpUrl = new StringBuilder(this.esUrl);
        if (StringObjectUtils.isValid(this.optionalIndex)) {
            baseEsHttpUrl.append(this.optionalIndex);
        }

        String baseEsUrl;
        if (entity != null) {
            baseEsUrl = entity.getBaseEsUrl(baseEsHttpUrl.toString());
        } else {
            baseEsUrl = baseEsHttpUrl.toString();
        }

        StringBuilder requestEsUrl = new StringBuilder(baseEsUrl);
        requestEsUrl.append("/_");
        requestEsUrl.append(command);
        requestEsUrl.append("?");
        requestEsUrl.append(IS_RESPONSE_JSON);

        return requestEsUrl.toString();
    }

    private String requestToEsByHttp (HttpRequestBase httpRequestBase) {
        HttpResponse httpResponse = null;
        try {
            httpResponse = this.httpClient.execute(httpRequestBase);
        } catch (IOException e) {
            this.logger.error("Sorry. something is wrong in Http Request. ("+e.toString()+")");
            try {
                httpRequestBase.releaseConnection();
            } catch (Exception ignored) {}
        }

        try {
            return EntityUtils.toString(httpResponse.getEntity(), Charset.forName(IlluminatiConstant.BASE_CHARSET));
        } catch (IOException e) {
            this.logger.error("Sorry. something is wrong in Parse on Http Response. ("+e.toString()+")");
            return null;
        } finally {
            try {
                httpRequestBase.releaseConnection();
            } catch (Exception ignored) {}
        }
    }

    private HttpEntity getHttpEntity(final String entityString) {
        return EntityBuilder.create().setText(entityString).setContentType(ContentType.APPLICATION_JSON).build();
    }

    private HttpResponse getHttpResponseByData (final int httpStatus, final String message) {
        HttpResponseFactory factory = new DefaultHttpResponseFactory();
        return factory.newHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, httpStatus, message), null);
    }

    private void checkIndexAndGenerate (final IlluminatiEsModel entity) {
        try {
            Map<String, Object> indexMappingResult = IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(this.getMappingByIndex(entity), IlluminatiConstant.TYPE_FOR_TYPE_TOKEN);
            if (indexMappingResult.containsKey(INDEX_IS_NOT_EXISTS_STATUS_OF_KEY)
                    && indexMappingResult.get(INDEX_IS_NOT_EXISTS_STATUS_OF_KEY).equals(INDEX_IS_NOT_EXISTS_KEY_IS_STATUS_VALUE)) {
                this.saveToEs(entity.getBaseEsUrl(this.esUrl), entity.getIndexMapping());
            }
        } catch (JsonSyntaxException ignore) {}
    }
}
