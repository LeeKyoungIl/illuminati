package com.leekyoungil.illuminati.common.dto;

import com.google.gson.annotations.Expose;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import com.leekyoungil.illuminati.common.util.SystemUtil;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class IlluminatiTemplateInterfaceModel implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    protected static final DateFormat DATE_FORMAT_EVENT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS", Locale.getDefault());

    @Expose private String parentModuleName;
    @Expose private ServerInfo serverInfo;
    @Expose private Map<String, Object> jvmInfo;

    @Expose private String id;
    @Expose private String illuminatiUniqueUserId;
    @Expose protected RequestGeneralModel general;
    @Expose protected RequestHeaderModel header;
    @Expose protected ChangedJsElement changedJsElement;
    @Expose private long elapsedTime;
    @Expose private long timestamp;
    @Expose private String logTime;
    @Expose protected Object output;

    @Expose private boolean isActiveChaosBomber = false;

    private Date localTime;
    private Object[] paramValues;

    public IlluminatiTemplateInterfaceModel() {}

    public IlluminatiTemplateInterfaceModel(final IlluminatiDataInterfaceModel illuminatiDataInterfaceModel) {
        this.localTime = new Date();
        this.generateAggregateId();

        this.elapsedTime = illuminatiDataInterfaceModel.getElapsedTime();
        this.output = illuminatiDataInterfaceModel.getOutput();

        this.timestamp = localTime.getTime();
        this.logTime = DATE_FORMAT_EVENT.format(localTime);
        this.paramValues = illuminatiDataInterfaceModel.getParamValues();

        this.setMethod(illuminatiDataInterfaceModel.getSignature());
    }

    public void initReqHeaderInfo (final RequestHeaderModel requestHeaderModel) {
        this.header = requestHeaderModel;
    }

    public void initBasicJvmInfo (final Map<String, Object> jvmInfo) {
        this.jvmInfo = jvmInfo;
    }

    public void initStaticInfo (final String parentModuleName, final ServerInfo serverInfo) {
        this.parentModuleName= parentModuleName;
        this.serverInfo = serverInfo;
    }

    public void addBasicJvmMemoryInfo (final Map<String, Object> jvmMemoryInfo) {
        if (this.jvmInfo == null) {
            this.jvmInfo = new HashMap<String, Object>();
        }

        for (Map.Entry<String, Object> entry : jvmMemoryInfo.entrySet()) {
            this.jvmInfo.put(entry.getKey(), entry.getValue());
        }
    }

    public void loadClientInfo (final Map<String, String> clientInfoMap) {
        if (clientInfoMap == null) {
            return;
        }

        if (this.general == null) {
            this.general = new RequestGeneralModel();
        }

        this.general.initClientInfo(clientInfoMap);
    }

    public void staticInfo (final Map<String, Object> staticInfo) {
        if (this.serverInfo != null && !this.serverInfo.isAreadySetServerDomainAndPort()) {
            this.serverInfo.setStaticInfoFromRequest(staticInfo);
        }
    }

    public void isActiveChaosBomber (boolean isActiveChaosBomber) {
        this.isActiveChaosBomber = isActiveChaosBomber;
    }

    public String getJsonString () {
        return IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(this);
    }

    public void setJavascriptUserAction () {
        if (this.paramValues != null && this.paramValues.length == 1
                && this.paramValues[0] instanceof ChangedJsElement) {
            ChangedJsElement changedJsElement = (ChangedJsElement) this.paramValues[0];

            if (changedJsElement == null) {
                return;
            }

            String illuminatiGProcId = this.header.getIlluminatiGProcId();
            String illuminatiSProcId = this.header.getIlluminatiSProcId();
            if (illuminatiGProcId != null && (illuminatiGProcId.equals(changedJsElement.getIlluminatiGProcId()) == true)
                    && illuminatiSProcId != null && (illuminatiSProcId.equals(changedJsElement.getIlluminatiSProcId()) == true)) {
                this.changedJsElement = changedJsElement;
                this.changedJsElement.convertListToMap();
            }
        }
    }

    public void initUniqueUserId (String illuminatiUniqueUserId) {
        this.illuminatiUniqueUserId = illuminatiUniqueUserId;
    }

    public void checkAndSetTransactionIdFromPostBody (String postBody) {
        if (StringObjectUtils.isValid(postBody) == false) {
            return;
        }

        final String[] postArrayData = postBody.split("&");

        if (postArrayData.length > 0) {
            final List<String> transactionIds = Arrays.asList(new String[]{"illuminatiGProcId", "illuminatiSProcId", "illuminatiUniqueUserId"});

            for (int i=0; i<postArrayData.length; i++) {
                final String[] postElementArrayData = postArrayData[i].split("=");

                for (final String keyValue : transactionIds) {
                    if (postElementArrayData.length != 2) {
                        continue;
                    }

                    if (keyValue.equals(postElementArrayData[0]) == true) {
                        if ("illuminatiGProcId".equals(keyValue) == true && StringObjectUtils.isValid(this.header.getIlluminatiGProcId()) == false) {
                            this.header.setGlobalTransactionId(postElementArrayData[1]);
                        } else if ("illuminatiSProcId".equals(keyValue) == true && StringObjectUtils.isValid(this.header.getIlluminatiSProcId()) == false) {
                            this.header.setSessionTransactionId(postElementArrayData[1]);
                        } else if ("illuminatiUniqueUserId".equals(keyValue) == true && StringObjectUtils.isValid(this.illuminatiUniqueUserId) == false) {
                            this.illuminatiUniqueUserId = postElementArrayData[1];
                        }
                    }
                }
            }
        }
    }

    private void generateAggregateId () {
        this.id = StringObjectUtils.generateId(this.localTime.getTime(), null);
    }

    protected String getId () {
        return this.id;
    }

    protected String getParentModuleName () {
        return this.parentModuleName;
    }

    private void setMethod (final MethodSignature methodSignature) {
        if (this.general == null) {
            this.general = new RequestGeneralModel();
        }
        this.general.setMethod(methodSignature.getMethod(), methodSignature.getParameterNames(), this.paramValues);
    }
}