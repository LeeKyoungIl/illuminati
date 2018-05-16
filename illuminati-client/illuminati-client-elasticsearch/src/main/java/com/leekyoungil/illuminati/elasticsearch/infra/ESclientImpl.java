package com.leekyoungil.illuminati.elasticsearch.infra;

import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.elasticsearch.model.IlluminatiEsModel;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
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
import java.util.HashMap;
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

    public ESclientImpl (final HttpClient httpClient, final String esUrl, final int esPort) {
        this.httpClient = httpClient;
        this.esUrl = "http://" + esUrl + ":" + String.valueOf(esPort);
    }

    public void setOptionalIndex (String optionalIndex) {
        this.optionalIndex = optionalIndex;
    }

    @Override public HttpResponse save (final IlluminatiEsModel entity) {
        final HttpRequestBase httpPutRequest = new HttpPut(entity.getEsUrl(this.esUrl + this.optionalIndex));

        if (entity.isSetUserAuth() == true) {
            try {
                httpPutRequest.setHeader("Authorization", "Basic " + entity.getEsAuthString());
            } catch (Exception ex) {
                this.logger.error("Sorry. something is wrong in encoding es user auth info. ("+ex.toString()+")");
            }
        }

        ((HttpPut) httpPutRequest).setEntity(this.getHttpEntity(entity.getJsonString()));

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

    @Override public String getDataByParam(Map<String, Object> param) {
        if (param == null || param.size() == 0) {
            return null;
        }

        final HttpRequestBase httpPostRequest = new HttpPost(this.getSearchRequestUrl());
        ((HttpPost) httpPostRequest).setEntity(this.getHttpEntity(IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(this.generateRequestParam(param))));
        HttpResponse httpResponse = null;
        try {
            httpResponse = this.httpClient.execute(httpPostRequest);
        } catch (IOException e) {
            this.logger.error("Sorry. something is wrong in Http Request. ("+e.toString()+")");
            try {
                httpPostRequest.releaseConnection();
            } catch (Exception ignored) {}
        }

        try {
            return EntityUtils.toString(httpResponse.getEntity(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                httpPostRequest.releaseConnection();
            } catch (Exception ignored) {}
        }
    }

    private String getSearchRequestUrl () {
        StringBuilder requestEsUrl = new StringBuilder();
        requestEsUrl.append(this.esUrl);
        requestEsUrl.append("/");
        requestEsUrl.append(this.optionalIndex);
        requestEsUrl.append("/");
        requestEsUrl.append("_search?pretty");

        return requestEsUrl.toString();
    }

    private Map<String, Object> generateRequestParam (Map<String, Object> param) {
        Map<String, Object> queryParam = new HashMap<String, Object>();

        if (param.containsKey("match") == true) {
            queryParam.put("match", param.get("match"));
        } else {
            queryParam.put("match_all", new HashMap<String, Object>());
        }
        if (param.containsKey("range") == true) {
            queryParam.put("range", param.get("range"));
        }

        Map<String, Object> requestParam = new HashMap<String, Object>();
        requestParam.put("query", queryParam);

        if (param.containsKey("source") == true) {
            requestParam.put("_source", param.get("source"));
        }
        if (param.containsKey("from") == true) {
            requestParam.put("from", param.get("from"));
        }
        if (param.containsKey("size") == true) {
            requestParam.put("size", param.get("size"));
        }

        return requestParam;
    }

    private HttpEntity getHttpEntity(final String entityString) {
        return EntityBuilder.create().setText(entityString).setContentType(this.contentType).build();
    }

    private HttpResponse getHttpResponseByData (final int httpStatus, final String message) {
        HttpResponseFactory factory = new DefaultHttpResponseFactory();
        return factory.newHttpResponse(new BasicStatusLine(this.httpVersion, httpStatus, message), null);
    }
}
