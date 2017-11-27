package com.leekyoungil.illuminati.elasticsearch.infra;

import com.leekyoungil.illuminati.elasticsearch.model.IlluminatiEsModel;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
            httpPutRequest.releaseConnection();
        }

        if (httpResponse == null) {
            httpResponse = getHttpResponseByData(this.errorCode, "Sorry. something is wrong in Http Request.");
        }

        return httpResponse;
    }

    private HttpEntity getHttpEntity(final IlluminatiEsModel entity) {
        return EntityBuilder.create().setText(entity.getJsonString()).setContentType(this.contentType).build();
    }

    private HttpResponse getHttpResponseByData (final int httpStatus, final String message) {
        HttpResponseFactory factory = new DefaultHttpResponseFactory();
        return factory.newHttpResponse(new BasicStatusLine(this.httpVersion, httpStatus, message), null);
    }
}
