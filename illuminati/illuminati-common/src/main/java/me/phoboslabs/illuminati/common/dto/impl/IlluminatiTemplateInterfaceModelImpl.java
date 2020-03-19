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

package me.phoboslabs.illuminati.common.dto.impl;

import com.google.gson.annotations.Expose;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.*;
import me.phoboslabs.illuminati.common.dto.enums.IlluminatiInterfaceType;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.*;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class IlluminatiTemplateInterfaceModelImpl implements IlluminatiInterfaceModel {

    @Expose private String parentModuleName;
    @Expose private ServerInfo serverInfo;
    @Expose private Map<String, Object> jvmInfo;

    @Expose private String id;
    @Expose private String illuminatiUniqueUserId;
    @Expose private String packageType;
    @Expose protected RequestGeneralModel general;
    @Expose protected RequestHeaderModel header;
    @Expose private ChangedJsElement changedJsElement;
    @Expose private long elapsedTime;
    @Expose private long timestamp;
    @Expose private String logTime;
    @Expose protected Map<String, Object> output;

    @Expose private boolean isActiveChaosBomber = false;

    private Date localTime;
    private Object[] paramValues;

    private static final List<String> TRANSACTION_IDS = new ArrayList<String>();

    static {
        TRANSACTION_IDS.add(ILLUMINATI_GPROC_ID_KEY);
        TRANSACTION_IDS.add(ILLUMINATI_SPROC_ID_KEY);
        TRANSACTION_IDS.add(ILLUMINATI_UNIQUE_USER_ID_KEY);
    }

    public IlluminatiTemplateInterfaceModelImpl() {}

    public IlluminatiTemplateInterfaceModelImpl(final IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModelImpl) {
        this.localTime = new Date();
        this.generateAggregateId();

        this.elapsedTime = illuminatiDataInterfaceModelImpl.getElapsedTime();
        this.output = illuminatiDataInterfaceModelImpl.getOutput();

        this.timestamp = localTime.getTime();
        this.logTime = IlluminatiConstant.DATE_FORMAT_EVENT.format(localTime);
        this.paramValues = illuminatiDataInterfaceModelImpl.getParamValues();
        this.changedJsElement = illuminatiDataInterfaceModelImpl.getChangedJsElement();

        this.setMethod(illuminatiDataInterfaceModelImpl.getSignature())
            .initReqHeaderInfo(illuminatiDataInterfaceModelImpl.getRequestHeaderModel())
            .checkAndSetTransactionIdFromPostBody(this.header.getPostContentBody())
            .initUniqueUserId(illuminatiDataInterfaceModelImpl.getIlluminatiUniqueUserId())
            .loadClientInfo(illuminatiDataInterfaceModelImpl.getClientInfoMap())
            .staticInfo(illuminatiDataInterfaceModelImpl.getStaticInfo())
            .isActiveChaosBomber(illuminatiDataInterfaceModelImpl.isActiveChaosBomber())
            .setPackageType(illuminatiDataInterfaceModelImpl.getPackageType());
    }

    // ################################################################################################################
    // ### public methods                                                                                           ###
    // ################################################################################################################

    public IlluminatiTemplateInterfaceModelImpl initBasicJvmInfo (final Map<String, Object> jvmInfo) {
        this.jvmInfo = jvmInfo;
        return this;
    }

    public IlluminatiTemplateInterfaceModelImpl initStaticInfo (final String parentModuleName, final ServerInfo serverInfo) {
        this.parentModuleName= parentModuleName;
        this.serverInfo = serverInfo;
        return this;
    }

    public IlluminatiTemplateInterfaceModelImpl addBasicJvmMemoryInfo (final Map<String, Object> jvmMemoryInfo) {
        if (this.jvmInfo == null) {
            this.jvmInfo = new HashMap<String, Object>();
        }

        for (Map.Entry<String, Object> entry : jvmMemoryInfo.entrySet()) {
            this.jvmInfo.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public String getJsonString () {
        return IlluminatiConstant.ILLUMINATI_GSON_OBJ.toJson(this);
    }

    private boolean isEqualsGProcId(final String illuminatiGProcId) {
        return StringObjectUtils.isValid(illuminatiGProcId) && (illuminatiGProcId.equals(this.changedJsElement.getIlluminatiGProcId()));
    }

    private boolean isEqualsSProcId(final String illuminatiSProcId) {
        return StringObjectUtils.isValid(illuminatiSProcId) && (illuminatiSProcId.equals(this.changedJsElement.getIlluminatiSProcId()));
    }

    public IlluminatiTemplateInterfaceModelImpl setJavascriptUserAction () {
        if (this.changedJsElement != null
                && this.isEqualsGProcId(this.header.getIlluminatiGProcId()) && this.isEqualsSProcId(this.header.getIlluminatiSProcId())) {
            this.changedJsElement.convertListToMap();
        }

        return this;
    }

    // ################################################################################################################
    // ### protected methods                                                                                        ###
    // ################################################################################################################

    protected String getId () {
        return this.id;
    }

    protected String getParentModuleName () {
        return this.parentModuleName;
    }

    // ################################################################################################################
    // ### private methods                                                                                          ###
    // ################################################################################################################

    private IlluminatiTemplateInterfaceModelImpl initReqHeaderInfo (final RequestHeaderModel requestHeaderModel) {
        this.header = requestHeaderModel;
        return this;
    }

    private IlluminatiTemplateInterfaceModelImpl setPackageType (final String packageType) {
        this.packageType = packageType;
        return this;
    }

    private IlluminatiTemplateInterfaceModelImpl loadClientInfo (final Map<String, String> clientInfoMap) {
        if (clientInfoMap == null) {
            return this;
        }

        if (this.general == null) {
            this.general = new RequestGeneralModel();
        }

        this.general.initClientInfo(clientInfoMap);
        return this;
    }

    private IlluminatiTemplateInterfaceModelImpl staticInfo (final Map<String, Object> staticInfo) {
        if (this.serverInfo != null && this.serverInfo.isAlreadySetServerDomainAndPort() == false) {
            this.serverInfo.setStaticInfoFromRequest(staticInfo);
        }
        return this;
    }

    private IlluminatiTemplateInterfaceModelImpl isActiveChaosBomber (final boolean isActiveChaosBomber) {
        this.isActiveChaosBomber = isActiveChaosBomber;
        return this;
    }

    private IlluminatiTemplateInterfaceModelImpl initUniqueUserId (final String illuminatiUniqueUserId) {
        this.illuminatiUniqueUserId = illuminatiUniqueUserId;
        return this;
    }

    private IlluminatiTemplateInterfaceModelImpl checkAndSetTransactionIdFromPostBody (final String postBody) {
        if (StringObjectUtils.isValid(postBody) == false) {
            return this;
        }

        final String[] postArrayData = postBody.split("&");

        if (postArrayData.length <= 0) {
            return this;
        }

        for (int i=0; i<postArrayData.length; i++) {
            final String[] postElementArrayData = postArrayData[i].split("=");
            if (postElementArrayData.length != 2) {
                continue;
            }

            for (final String keyValue : TRANSACTION_IDS) {
                final String postElementKey = postElementArrayData[0];
                final String postElementValue = postElementArrayData[1];
                if (keyValue.equals(postElementKey) == false) {
                    continue;
                }

                if (ILLUMINATI_GPROC_ID_KEY.equals(keyValue) && StringObjectUtils.isValid(this.header.getIlluminatiGProcId()) == false) {
                    this.header.setGlobalTransactionId(postElementValue);
                } else if (ILLUMINATI_SPROC_ID_KEY.equals(keyValue) && StringObjectUtils.isValid(this.header.getIlluminatiSProcId()) == false) {
                    this.header.setSessionTransactionId(postElementValue);
                } else if (ILLUMINATI_UNIQUE_USER_ID_KEY.equals(keyValue) && StringObjectUtils.isValid(this.illuminatiUniqueUserId) == false) {
                    this.illuminatiUniqueUserId = postElementValue;
                }
            }
        }

        return this;
    }

    private void generateAggregateId () {
        this.id = StringObjectUtils.generateId(this.localTime.getTime(), null);
    }

    private IlluminatiTemplateInterfaceModelImpl setMethod (final MethodSignature methodSignature) {
        if (this.general == null) {
            this.general = new RequestGeneralModel();
        }
        this.general.setMethod(methodSignature.getMethod(), methodSignature.getParameterNames(), this.paramValues);
        return this;
    }

    @Override public IlluminatiInterfaceType getInterfaceType() throws Exception {
        throw new Exception("This feature is not available here.");
    }

    @Override public void setIlluminatiInterfaceType(IlluminatiInterfaceType illuminatiInterfaceType) throws Exception {
        throw new Exception("This feature is not available here.");
    }
}