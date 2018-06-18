package com.leekyoungil.illuminati.elasticsearch.infra;

import com.google.gson.reflect.TypeToken;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import com.leekyoungil.illuminati.elasticsearch.model.IlluminatiEsModel;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.*;
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

    private final HttpVersion httpVersion = HttpVersion.HTTP_1_1;
    private final int errorCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    private final ContentType contentType = ContentType.APPLICATION_JSON;

    private HttpClient httpClient;
    private String esUrl;
    private String optionalIndex = "";
    private String esAuthString;

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

        if (StringObjectUtils.isValid(this.esAuthString) == Boolean.TRUE) {
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
            httpResponse = getHttpResponseByData(this.errorCode, "Sorry. something is wrong in Http Request.");
        }

        return httpResponse;
    }

    @Override public String getDataByJson(final IlluminatiEsModel entity, String jsonRequestString) {
        if (StringObjectUtils.isValid(jsonRequestString) == Boolean.FALSE) {
            return null;
        }
        final HttpRequestBase httpPostRequest = new HttpPost(this.getRequestUrl(entity.getBaseEsUrl(this.esUrl + this.optionalIndex), "search"));
        ((HttpPost) httpPostRequest).setEntity(this.getHttpEntity(jsonRequestString));
        String resultFromEs = null;
        try {
            resultFromEs = this.requestToEsByHttp(httpPostRequest);
        } catch (Exception e) {
            this.logger.error("Sorry. something is wrong in Http Request. ("+e.toString()+")");
            try {
                httpPostRequest.releaseConnection();
            } catch (Exception ignored) {}
        } finally {
            try {
                httpPostRequest.releaseConnection();
            } catch (Exception ignored) {}
        }

        return resultFromEs;
    }

    @Override public String getMappingByIndex(final IlluminatiEsModel entity) {
        final HttpRequestBase httpGetRequest = new HttpGet(this.getRequestUrl(entity.getBaseEsUrl(this.esUrl + this.optionalIndex), "mapping"));
        String resultFromEs = null;
        try {
            resultFromEs = this.requestToEsByHttp(httpGetRequest);
        } catch (Exception e) {
            this.logger.error("Sorry. something is wrong in Http Request. ("+e.toString()+")");
            try {
                httpGetRequest.releaseConnection();
            } catch (Exception ignored) {}
        } finally {
            try {
                httpGetRequest.releaseConnection();
            } catch (Exception ignored) {}
        }

        return resultFromEs;
    }

    private String getRequestUrl (String baseEsUrl, String command) {
        StringBuilder requestEsUrl = new StringBuilder();
        requestEsUrl.append(baseEsUrl);
        requestEsUrl.append("/");
        requestEsUrl.append(this.optionalIndex);
        requestEsUrl.append("/_");
        requestEsUrl.append(command);
        requestEsUrl.append("?pretty");

        return requestEsUrl.toString();
    }

    private String requestToEsByHttp (HttpUriRequest httpUriRequest) throws Exception {
        HttpResponse httpResponse = null;
        try {
            httpResponse = this.httpClient.execute(httpUriRequest);
        } catch (IOException e) {
            throw e;
        }

        try {
            return EntityUtils.toString(httpResponse.getEntity(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw e;
        }
    }

    private HttpEntity getHttpEntity(final String entityString) {
        return EntityBuilder.create().setText(entityString).setContentType(this.contentType).build();
    }

    private HttpResponse getHttpResponseByData (final int httpStatus, final String message) {
        HttpResponseFactory factory = new DefaultHttpResponseFactory();
        return factory.newHttpResponse(new BasicStatusLine(this.httpVersion, httpStatus, message), null);
    }

    private void checkIndexAndGenerate (final IlluminatiEsModel entity) {
        Map<String, Object> indexMappingResult = IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(this.getMappingByIndex(entity), new TypeToken<Map<String, Object>>(){}.getType());
        if (indexMappingResult.containsKey("status") == Boolean.TRUE) {
            this.saveToEs(entity.getBaseEsUrl(this.esUrl), entity.getIndexMapping());
        }
    }
}
