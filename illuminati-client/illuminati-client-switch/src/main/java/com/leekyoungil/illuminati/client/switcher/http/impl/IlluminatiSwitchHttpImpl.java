package com.leekyoungil.illuminati.client.switcher.http.impl;

import com.leekyoungil.illuminati.client.switcher.http.IlluminatiSwitchHttp;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;

import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class IlluminatiSwitchHttpImpl implements IlluminatiSwitchHttp<String> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private HttpClient httpClient;
    private String url;

    public IlluminatiSwitchHttpImpl(final HttpClient httpClient, final String url) {
        this.httpClient = httpClient;
        this.url = url;
    }

    @Override public String getByGetMethod() {
        final HttpRequestBase httpGetRequest = new HttpGet(this.url);

        HttpResponse httpResponse = null;

        try {
            httpResponse = this.httpClient.execute(httpGetRequest);
        }
        catch (IOException e) {
            this.logger.error("Sorry. something is wrong in Http Request. (" + e.toString() + ")");
        }
        finally {
            httpGetRequest.releaseConnection();
        }

        String responseString = null;

        if (httpResponse == null) {
            return null;
        }

        if ("2".equals(String.valueOf(httpResponse.getStatusLine().getStatusCode()).substring(0, 1))) {
            HttpEntity entity = httpResponse.getEntity();
            try {
                responseString = EntityUtils.toString(entity);
            } catch (IOException e) {
                // ignore
            }
        }

        return responseString;
    }
}
