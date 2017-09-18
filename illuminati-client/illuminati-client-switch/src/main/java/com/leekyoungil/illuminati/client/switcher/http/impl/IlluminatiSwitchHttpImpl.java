package com.leekyoungil.illuminati.client.switcher.http.impl;

import com.leekyoungil.illuminati.client.switcher.http.IlluminatiSwitchHttp;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class IlluminatiSwitchHttpImpl implements IlluminatiSwitchHttp<HttpResponse> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final HttpVersion httpVersion = HttpVersion.HTTP_1_1;
	private final int errorCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;

	private HttpClient httpClient;
	private String url;

	public IlluminatiSwitchHttpImpl (final HttpClient httpClient, final String url) {
		this.httpClient = httpClient;
		this.url = url;

	}

	@Override public HttpResponse getByGetMethod () {
		final HttpRequestBase httpGetRequest = new HttpGet(this.url);

		HttpResponse httpResponse = null;

		try {
			httpResponse = this.httpClient.execute(httpGetRequest);
		} catch (IOException e) {
			this.logger.error("Sorry. something is wrong in Http Request. ("+e.toString()+")");
		} finally {
			httpGetRequest.releaseConnection();
		}

		if (httpResponse == null) {
			httpResponse = getHttpResponseByData(this.errorCode, "Sorry. something is wrong in Http Request.");
		}

		return httpResponse;
	}

	private HttpResponse getHttpResponseByData (final int httpStatus, final String message) {
		HttpResponseFactory factory = new DefaultHttpResponseFactory();
		return factory.newHttpResponse(new BasicStatusLine(this.httpVersion, httpStatus, message), null);
	}
}
