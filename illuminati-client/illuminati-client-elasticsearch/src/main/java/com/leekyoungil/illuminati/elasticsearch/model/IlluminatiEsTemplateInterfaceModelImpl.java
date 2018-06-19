package com.leekyoungil.illuminati.elasticsearch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.dto.GroupMapping;
import com.leekyoungil.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import com.leekyoungil.illuminati.elasticsearch.infra.EsDocument;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsIndexStoreType;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsRefreshType;
import com.leekyoungil.illuminati.elasticsearch.infra.model.Settings;
import com.leekyoungil.illuminati.elasticsearch.infra.param.mapping.EsIndexMappingBuilder;
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
    @Expose private Object resultData;
    @Expose private Map<String, String> postContentResultData;

    @Expose private Map<String, String> clientBrower;
    @Expose private Map<String, String> clientOs;
    @Expose private String clientDevice;

    private String esUserName;
    private String esUserPass;

    private final String encodingCharset = "UTF-8";

    private final String objectPackageName = "java.lang.Object";
    private final String mapPackageName = "java.util.Map";
    private final String listPackageName = "java.util.List";
    private final String mappingTargetPackageName = "com.leekyoungil";

    public IlluminatiEsTemplateInterfaceModelImpl() {
        super();
    }

//    public IlluminatiEsTemplateInterfaceModelImpl(long elapsedTime, Object output, String id, long timestamp) {
//        super(elapsedTime, output, id, timestamp);
//    }

    @Override public String getJsonString () {
        this.settings = new Settings(this.getEsDocumentAnnotation().indexStoreType().getType());

        this.setResultData();
        this.setUserAgent();
        this.setPostContentResultData();

        return IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(this);
    }

    @Override public String getBaseEsUrl(final String baseUrl) {
        if (StringObjectUtils.isValid(baseUrl) == false) {
            ES_CONSUMER_LOGGER.error("Sorry. baseUrl of Elasticsearch is required value.");
            return null;
        }

        try {
            final EsDocument esDocument = this.getEsDocumentAnnotation();

            final String[] dateForIndex = IlluminatiConstant.DATE_FORMAT_EVENT.format(new Date()).split("T");

            return new StringBuilder(baseUrl).append("/").append(esDocument.indexName()+"-"+dateForIndex[0]).toString();
        } catch (Exception ex) {
            ES_CONSUMER_LOGGER.error("Sorry. something is wrong in generated Elasticsearch url. ("+ex.toString()+")");
            return null;
        }
    }

    @Override public String getEsUrl(final String baseUrl) {
        final String baseEsUrl = this.getBaseEsUrl(baseUrl);
        if (StringObjectUtils.isValid(baseEsUrl) == false) {
            return null;
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
            ES_CONSUMER_LOGGER.error("Sorry. something is wrong in generated Elasticsearch url. ("+ex.toString()+")");
            return null;
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

        if (StringObjectUtils.isValid(postContentBody)) {
            try {
                final String[] postContentBodyArray = URLDecoder.decode(postContentBody, this.encodingCharset).split("&");

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
                ES_CONSUMER_LOGGER.error("Sorry. an error occurred during parsing of post content. ("+ex.toString()+")");
            }
        }
    }

    private void setResultData () {
        if (this.output != null) {
            if (!(this.output instanceof String)) {
                this.output = IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(this.output);
            }

            if (!StringObjectUtils.isValid((String) this.output)) {
                return;
            }

            Map<String, Object> tmpResultData = null;
            try {
                tmpResultData = IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson((String) this.output, new TypeToken<Map<String, Object>>(){}.getType());
            } catch (Exception ex) {
                ES_CONSUMER_LOGGER.error("Sorry. an error occurred during casting. ("+ex.toString()+")");
            }

            final String resultKey = "result";

            if (tmpResultData != null && tmpResultData.containsKey(resultKey) && tmpResultData.get(resultKey) != null) {
                this.resultData = tmpResultData.get(resultKey);
                // ignore output json
                this.output = null;
            } else {
                ES_CONSUMER_LOGGER.debug("Sorry. 'output' key of map is not exists.");
            }
        }
    }

    private void setUserAgent () {
        try {
            final String userAgent = this.header.getUserAgent();

            if (StringObjectUtils.isValid(userAgent)) {
                final ReadableUserAgent agent = UA_PARSER.parse(userAgent);

                this.setUserBrower(agent);
                this.setUserOs(agent);
                this.setUserDevice(agent);
            }
        } catch (Exception ex) {
            ES_CONSUMER_LOGGER.error("Sorry. parsing failed. ("+ex.toString()+")");
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
        if (StringObjectUtils.isValid(this.esUserName) && StringObjectUtils.isValid(this.esUserPass)) {
            return true;
        }

        return false;
    }

    @Override public String getEsAuthString () {
        if (this.isSetUserAuth()) {
            StringBuilder authInfo = new StringBuilder();
            authInfo.append(this.esUserName);
            authInfo.append(":");
            authInfo.append(this.esUserPass);

            byte[] credentials = Base64.encodeBase64(((authInfo.toString()).getBytes(Charset.forName(this.encodingCharset))));
            return new String(credentials, Charset.forName(this.encodingCharset));
        }

        return null;
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
                    Class<?> memberClass = Class.forName(className);
                    this.getMappingAnnotation(memberClass, esIndexMappingBuilder);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            if (field.getAnnotation(Expose.class) != null && field.getAnnotation(GroupMapping.class) != null) {
                if (this.mapPackageName.equalsIgnoreCase(className) == false
                        && this.listPackageName.equalsIgnoreCase(className) == false) {
                    final GroupMapping annotatedOnField = field.getAnnotation(GroupMapping.class);
                    esIndexMappingBuilder.setMapping(clazz.getSimpleName(), field.getName(), annotatedOnField.mappingType());
                }
            }
        }

        this.getMappingAnnotation(clazz.getSuperclass(), esIndexMappingBuilder);
    }
}
