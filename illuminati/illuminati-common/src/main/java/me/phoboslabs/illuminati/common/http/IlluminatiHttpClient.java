/*
 * Copyright 2017 Phoboslabs.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.phoboslabs.illuminati.common.http;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class IlluminatiHttpClient extends CloseableHttpClient {

    private CloseableHttpClient httpClient;

    private static final int MAX_CONNECTION = 1000;
    private static final int MAX_CONNECTION_PER_ROUTE = 1000;
    private static final int CONNECTION_TIMEOUT = 1000;
    private static final int SOCKET_TIMEOUT = 3000;

    private final Properties properties = new Properties();

    public IlluminatiHttpClient () {
        this.initPoolingHttpClientManager();
    }

    @Override protected CloseableHttpResponse doExecute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws IOException, ClientProtocolException {
        return this.httpClient.execute(httpHost, httpRequest, httpContext);
    }

    public void setProperties (final String key, final String value) {
        this.properties.put(key, value);
    }

    private void initPoolingHttpClientManager () {
        final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .register("http", PlainConnectionSocketFactory.getSocketFactory()).build();

        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(MAX_CONNECTION);
        connectionManager.setDefaultMaxPerRoute(MAX_CONNECTION_PER_ROUTE);

        final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT);

        final RequestConfig requestConfig = requestConfigBuilder.build();

        HashSet<Header> defaultHeaders = new HashSet<>();
        defaultHeaders.add(new BasicHeader(HttpHeaders.PRAGMA, "no-cache"));
        defaultHeaders.add(new BasicHeader(HttpHeaders.CACHE_CONTROL, "no-cache"));

        final HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultHeaders(defaultHeaders).disableAuthCaching().disableContentCompression();
        this.httpClient = httpClientBuilder.setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).build();
    }

    @Override public void close() throws IOException {
        if (this.httpClient != null) {
            this.httpClient.close();
        }
    }

    @Override @Deprecated public HttpParams getParams() {
        return null;
    }

    @Override @Deprecated public ClientConnectionManager getConnectionManager() {
        return null;
    }
}
