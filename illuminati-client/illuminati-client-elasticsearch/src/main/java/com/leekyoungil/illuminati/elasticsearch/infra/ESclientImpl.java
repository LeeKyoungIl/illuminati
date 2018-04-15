package com.leekyoungil.illuminati.elasticsearch.infra;

import com.google.gson.reflect.TypeToken;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.util.IlluminatiStringBuilder;
import com.leekyoungil.illuminati.elasticsearch.model.IlluminatiEsModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
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

    public ESclientImpl (final HttpClient httpClient, final String esUrl, final int esPort) {
        this.httpClient = httpClient;
        this.esUrl = "http://" + esUrl + ":" + String.valueOf(esPort);
    }

    @Override public HttpResponse save (final IlluminatiEsModel entity) {
        final HttpRequestBase httpPutRequest = new HttpPut(entity.getEsUrl(this.esUrl));

        if (entity.isSetUserAuth() == true) {
            try {
                httpPutRequest.setHeader("Authorization", "Basic " + entity.getEsAuthString());
            } catch (Exception ex) {
                this.logger.error("Sorry. something is wrong in encoding es user auth info. ("+ex.toString()+")");
            }
        }

        ((HttpPut) httpPutRequest).setEntity(this.getHttpEntity(entity));

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

    @Override public String getAllDataByFields(ArrayList<String> fields) {
        if (CollectionUtils.isEmpty(fields) == true) {
            return null;
        }
        IlluminatiStringBuilder fieldNames = new IlluminatiStringBuilder();
        for (String fieldName : fields) {
            fieldNames.appendString(fieldName);
        }

        final HttpRequestBase httpGetRequest = new HttpGet(this.esUrl + "/_search?_source=" + fieldNames.toStringWithDelimiter(","));

        HttpResponse httpResponse = null;
        try {
            httpResponse = this.httpClient.execute(httpGetRequest);
        } catch (IOException e) {
            this.logger.error("Sorry. something is wrong in Http Request. ("+e.toString()+")");
            try {
                httpGetRequest.releaseConnection();
            } catch (Exception ignored) {}
        }

        try {
            return EntityUtils.toString(httpResponse.getEntity(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                httpGetRequest.releaseConnection();
            } catch (Exception ignored) {}
        }
    }

    private HttpEntity getHttpEntity(final IlluminatiEsModel entity) {
        return EntityBuilder.create().setText(entity.getJsonString()).setContentType(this.contentType).build();
    }

    private HttpResponse getHttpResponseByData (final int httpStatus, final String message) {
        HttpResponseFactory factory = new DefaultHttpResponseFactory();
        return factory.newHttpResponse(new BasicStatusLine(this.httpVersion, httpStatus, message), null);
    }
}
