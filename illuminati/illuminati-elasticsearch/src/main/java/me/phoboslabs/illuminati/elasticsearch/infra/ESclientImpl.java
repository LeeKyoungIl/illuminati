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

package me.phoboslabs.illuminati.elasticsearch.infra;

import com.google.gson.JsonSyntaxException;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import me.phoboslabs.illuminati.elasticsearch.model.IlluminatiEsModel;
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
        this.esUrl = "http://".concat(esUrl).concat(":").concat(String.valueOf(esPort));
    }

    public void setOptionalIndex (String optionalIndex) {
        this.optionalIndex = optionalIndex;
    }

    @Override public HttpResponse save (final IlluminatiEsModel entity) throws Exception {
        try {
            this.esAuthString = entity.getEsAuthString();
        } catch (Exception ignore) {}

        this.checkIndexAndGenerate(entity);
        return this.saveToEs(entity.getEsUrl(this.esUrl + this.optionalIndex), entity.getJsonString());
    }

    private HttpResponse saveToEs (String esRequestUrl, String jsonString) {
        final HttpRequestBase httpPutRequest = new HttpPut(esRequestUrl);

        if (StringObjectUtils.isValid(this.esAuthString)) {
            try {
                httpPutRequest.setHeader("Authorization", "Basic " + this.esAuthString);
            } catch (Exception ex) {
                this.logger.error("Sorry. something is wrong in encoding es user auth info. ({})", ex.getMessage(), ex);
            }
        }

        ((HttpPut) httpPutRequest).setEntity(this.getHttpEntity(jsonString));

        HttpResponse httpResponse = null;

        try {
            httpResponse = this.httpClient.execute(httpPutRequest);
        } catch (IOException e) {
            this.logger.error("Sorry. something is wrong in Http Request. ({})", e.getMessage(), e);
        } finally {
            try {
                httpPutRequest.releaseConnection();
            } catch (Exception ignored) {}
        }

        return httpResponse != null ? httpResponse
                                    : this.getHttpResponseByData(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Sorry. something is wrong in Http Request.");
    }

    @Override public String getDataByJson(final String jsonRequestString) throws Exception {
        if (StringObjectUtils.isValid(jsonRequestString) == false) {
            throw new Exception("jsonRequestString must not be null.");
        }
        final HttpRequestBase httpPostRequest = new HttpPost(this.getRequestUrl(ES_SEARCH_KEYWORD));
        ((HttpPost) httpPostRequest).setEntity(this.getHttpEntity(jsonRequestString));

        return this.requestToEsByHttp(httpPostRequest);
    }

    @Override public String getMappingByIndex(final IlluminatiEsModel entity) throws Exception {
        return this.requestToEsByHttp(new HttpGet(this.getRequestUrl(entity, ES_MAPPING_KEYWORD)));
    }

    private String getRequestUrl (String command) {
        return this.generateRequestUrl(this.getBaseEsHttpUrl(), command);
    }

    private String getRequestUrl (final IlluminatiEsModel entity, String command) throws Exception {
        return this.generateRequestUrl(entity.getBaseEsUrl(this.getBaseEsHttpUrl()), command);
    }

    private String getBaseEsHttpUrl () {
        StringBuilder baseEsHttpUrl = new StringBuilder(this.esUrl);
        if (StringObjectUtils.isValid(this.optionalIndex)) {
            baseEsHttpUrl.append(this.optionalIndex);
        }

        return baseEsHttpUrl.toString();
    }

    private String generateRequestUrl (String baseEsUrl, String command) {
        return new StringBuilder(baseEsUrl).append("/_").append(command)
                    .append("?").append(IS_RESPONSE_JSON).toString();
    }

    private String requestToEsByHttp (HttpRequestBase httpRequestBase) {
        HttpResponse httpResponse = null;
        try {
            httpResponse = this.httpClient.execute(httpRequestBase);
        } catch (IOException e) {
            this.logger.error("Sorry. something is wrong in Http Request. ({})", e.getMessage(), e);
            try {
                httpRequestBase.releaseConnection();
            } catch (Exception ignored) {}
        }

        try {
            return EntityUtils.toString(httpResponse.getEntity(), Charset.forName(IlluminatiConstant.BASE_CHARSET));
        } catch (IOException e) {
            this.logger.error("Sorry. something is wrong in Parse on Http Response. ({})", e.getMessage(), e);
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

    private void checkIndexAndGenerate (final IlluminatiEsModel entity) throws Exception {
        try {
            Map<String, Object> indexMappingResult = IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson(this.getMappingByIndex(entity), IlluminatiConstant.TYPE_FOR_TYPE_TOKEN);
            if (indexMappingResult.containsKey(INDEX_IS_NOT_EXISTS_STATUS_OF_KEY)
                    && indexMappingResult.get(INDEX_IS_NOT_EXISTS_STATUS_OF_KEY).equals(INDEX_IS_NOT_EXISTS_KEY_IS_STATUS_VALUE)) {
                this.saveToEs(entity.getBaseEsUrl(this.esUrl), entity.getIndexMapping());
            }
        } catch (JsonSyntaxException ignore) {}
    }
}
