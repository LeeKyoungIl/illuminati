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

package me.phoboslabs.illuminati.elasticsearch.model;

import com.google.gson.annotations.Expose;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.GroupMapping;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import me.phoboslabs.illuminati.elasticsearch.infra.EsDocument;
import me.phoboslabs.illuminati.elasticsearch.infra.enums.EsIndexStoreType;
import me.phoboslabs.illuminati.elasticsearch.infra.enums.EsRefreshType;
import me.phoboslabs.illuminati.elasticsearch.infra.model.Settings;
import me.phoboslabs.illuminati.elasticsearch.infra.param.mapping.EsIndexMappingBuilder;
import net.sf.uadetector.OperatingSystem;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.VersionNumber;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
@EsDocument(indexName = "illuminati", type = "log", indexStoreType = EsIndexStoreType.FS, shards = 1, replicas = 0, refreshType = EsRefreshType.TRUE)
public abstract class IlluminatiEsTemplateInterfaceModelImpl extends IlluminatiTemplateInterfaceModelImpl implements IlluminatiEsModel {

    private final static Logger ES_CONSUMER_LOGGER = LoggerFactory.getLogger(IlluminatiEsTemplateInterfaceModelImpl.class);

    public final static UserAgentStringParser UA_PARSER = UADetectorServiceFactory.getResourceModuleParser();

    @Expose private Settings settings;
    @Expose private Map<String, String> postContentResultData;

    @Expose private Map<String, String> clientBrower;
    @Expose private Map<String, String> clientOs;
    @Expose private String clientDevice;

    private String esUserName;
    private String esUserPass;

    private final String objectPackageName = "java.lang.Object";
    private final String mapPackageName = "java.util.Map";
    private final String listPackageName = "java.util.List";
    private final String mappingTargetPackageName = "me.phoboslabs";

    public IlluminatiEsTemplateInterfaceModelImpl() {
        super();
    }

    @Override public String getJsonString () {
        this.settings = new Settings(this.getEsDocumentAnnotation().indexStoreType().getType());

        this.setUserAgent();
        this.setPostContentResultData();

        return IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(this);
    }

    @Override public String getBaseEsUrl(final String baseUrl) throws Exception {
        if (!StringObjectUtils.isValid(baseUrl)) {
            final String errorMessage = "Sorry. baseUrl of Elasticsearch is required value.";
            ES_CONSUMER_LOGGER.error(errorMessage);
            throw new Exception(errorMessage);
        }

        try {
            final EsDocument esDocument = this.getEsDocumentAnnotation();

            final String[] dateForIndex = IlluminatiConstant.DATE_FORMAT_EVENT.format(new Date()).split("T");

            return new StringBuilder(baseUrl).append("/").append(esDocument.indexName()+"-"+dateForIndex[0]).toString();
        } catch (Exception ex) {
            final String errorMessage = "Sorry. something is wrong in generated Elasticsearch url. ("+ex.getCause().getMessage()+")";
            ES_CONSUMER_LOGGER.error(errorMessage, ex);
            throw new Exception(errorMessage);
        }
    }

    @Override public String getEsUrl(final String baseUrl) throws Exception {
        final String baseEsUrl = this.getBaseEsUrl(baseUrl);
        if (!StringObjectUtils.isValid(baseEsUrl)) {
            throw new Exception("baseEsUrl must not be null.");
        }

        try {
            final EsDocument esDocument = this.getEsDocumentAnnotation();

            return new StringBuilder(baseEsUrl)
                    .append("/")
                    .append(esDocument.type())
                    .append("/")
                    .append(this.getId())
                    .append("?refresh=")
                    .append(esDocument.refreshType().getValue()).toString();
        } catch (Exception ex) {
            final String errorMessage = "Sorry. something is wrong in generated Elasticsearch url. ("+ex.getCause().getMessage()+")";
            ES_CONSUMER_LOGGER.error(errorMessage, ex);
            throw new Exception(errorMessage);
        }
    }

    @Override public void setEsUserAuth (String esUserName, String esUserPass) {
        if (StringObjectUtils.isValid(esUserName) && StringObjectUtils.isValid(esUserPass)) {
            this.esUserName = esUserName;
            this.esUserPass = esUserPass;
        }
    }

    private EsDocument getEsDocumentAnnotation () {
        return this.getClass().getAnnotation(EsDocument.class);
    }

    private void setPostContentResultData () {
        final String postContentBody = this.header.getPostContentBody();

        if (!StringObjectUtils.isValid(postContentBody)) {
            return;
        }

        try {
            final String[] postContentBodyArray = URLDecoder.decode(postContentBody, IlluminatiConstant.BASE_CHARSET).split("&");

            if (postContentBodyArray.length > 0) {
                this.postContentResultData = new HashMap<String, String>();
            }

            for (String element : postContentBodyArray) {
                final String[] elementArray = element.split("=");

                if (elementArray.length == 2) {
                    this.postContentResultData.put(elementArray[0], elementArray[1]);
                }
            }
        } catch (Exception ex) {
            ES_CONSUMER_LOGGER.error("Sorry. an error occurred during parsing of post content. ({})", ex.getCause().getMessage(), ex);
        }
    }

    private void setUserAgent () {
        try {
            final String userAgent = this.header.getUserAgent();

            if (!StringObjectUtils.isValid(userAgent)) {
                return;
            }

            final ReadableUserAgent agent = UA_PARSER.parse(userAgent);

            this.setUserBrower(agent);
            this.setUserOs(agent);
            this.setUserDevice(agent);
        } catch (Exception ex) {
            ES_CONSUMER_LOGGER.error("Sorry. parsing failed. ({})", ex.getCause().getMessage(), ex);
        }
    }

    private void setUserBrower (final ReadableUserAgent agent) {
        this.clientBrower = new HashMap<String, String>();

        this.clientBrower.put("browserType", agent.getType().getName());
        this.clientBrower.put("browserName", agent.getName());

        final VersionNumber browserVersion = agent.getVersionNumber();
        this.clientBrower.put("browserVersion", browserVersion.toVersionString());
        this.clientBrower.put("browserVersionMajor", browserVersion.getMajor());
        this.clientBrower.put("browserVersionMinor", browserVersion.getMinor());
        this.clientBrower.put("browserVersionBugFix", browserVersion.getBugfix());
        this.clientBrower.put("browserVersionExtension", browserVersion.getExtension());
        this.clientBrower.put("browserProducer", agent.getProducer());
    }

    private void setUserOs (final ReadableUserAgent agent) {
        this.clientOs = new HashMap<String, String>();

        final OperatingSystem os = agent.getOperatingSystem();
        this.clientOs.put("osName", os.getName());
        this.clientOs.put("osProducer", os.getProducer());

        final VersionNumber osVersion = os.getVersionNumber();
        this.clientOs.put("osVersion", osVersion.toVersionString());
        this.clientOs.put("osVersionMajor", osVersion.getMajor());
        this.clientOs.put("osVersionMinor", osVersion.getMinor());
        this.clientOs.put("osVersionBugFix", osVersion.getBugfix());
        this.clientOs.put("osVersionExtension", osVersion.getExtension());
    }

    private void setUserDevice (final ReadableUserAgent agent) {
        this.clientDevice = agent.getDeviceCategory().getName();
    }

    @Override public boolean isSetUserAuth () {
        return StringObjectUtils.isValid(this.esUserName) && StringObjectUtils.isValid(this.esUserPass);
    }

    @Override public String getEsAuthString () throws Exception {
        if (!this.isSetUserAuth()) {
            throw new Exception("Elasticsearch user auth not set.");
        }
        StringBuilder authInfo = new StringBuilder(this.esUserName).append(":").append(this.esUserPass);

        byte[] credentials = Base64.encodeBase64(((authInfo.toString()).getBytes(Charset.forName(IlluminatiConstant.BASE_CHARSET))));
        return new String(credentials, Charset.forName(IlluminatiConstant.BASE_CHARSET));
    }

    @Override public String getIndexMapping () {
        return this.getGroupMappingAnnotation();
    }

    private String getGroupMappingAnnotation () {
        final EsDocument esDocument = this.getEsDocumentAnnotation();

        EsIndexMappingBuilder esIndexMappingBuilder = EsIndexMappingBuilder.Builder().setEsDataType(esDocument.type());
        this.getMappingAnnotation(this.getClass(), esIndexMappingBuilder);

        return IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(esIndexMappingBuilder.build());
    }

    private void getMappingAnnotation (final Class<?> clazz, EsIndexMappingBuilder esIndexMappingBuilder) {
        if (this.objectPackageName.equalsIgnoreCase(clazz.getName())) {
            return;
        }

        for (Field field : clazz.getDeclaredFields()) {
            String className = field.getType().getName();
            if (className.contains(this.mappingTargetPackageName)) {
                try {
                    this.getMappingAnnotation(Class.forName(className), esIndexMappingBuilder);
                } catch (ClassNotFoundException ignored) {}
            }

            if (field.getAnnotation(Expose.class) != null && field.getAnnotation(GroupMapping.class) != null) {
                if (!this.mapPackageName.equalsIgnoreCase(className)
                        && !this.listPackageName.equalsIgnoreCase(className)) {
                    final GroupMapping annotatedOnField = field.getAnnotation(GroupMapping.class);
                    esIndexMappingBuilder.setMapping(clazz.getSimpleName(), field.getName(), annotatedOnField.mappingType());
                }
            }
        }

        this.getMappingAnnotation(clazz.getSuperclass(), esIndexMappingBuilder);
    }
}
