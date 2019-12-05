package me.phoboslabs.illuminati.switcher.http.impl;

import me.phoboslabs.illuminati.switcher.http.IlluminatiSwitchHttp;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class IlluminatiSwitchHttpImpl implements IlluminatiSwitchHttp<String> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private HttpClient httpClient;
    private String url;

    private final static int RETRY_COUNT = 3;

    public IlluminatiSwitchHttpImpl(final HttpClient httpClient, final String url) {
        this.httpClient = httpClient;
        this.url = url;
    }

    @Override public String getByGetMethod() throws Exception {
        final HttpRequestBase httpGetRequest = new HttpGet(this.url);

        HttpResponse httpResponse = null;

        try {
            httpResponse = this.httpClient.execute(httpGetRequest);
        } catch (IOException e) {
            final String errorMessage = "Sorry. something is wrong in Http Request. (" + e.getMessage() + ")";
            this.logger.error(errorMessage, e);
            throw new Exception(errorMessage);
        } finally {
            httpGetRequest.releaseConnection();
        }

        final String responseData = String.valueOf(httpResponse.getStatusLine().getStatusCode()).substring(0, 1);
        if ("2".equals(responseData)) {
            HttpEntity entity = httpResponse.getEntity();
            try {
                return EntityUtils.toString(entity);
            } catch (IOException ignore) {}
        }

        throw new Exception("check the value in the Http Response body. ("+responseData+")");
    }
}
