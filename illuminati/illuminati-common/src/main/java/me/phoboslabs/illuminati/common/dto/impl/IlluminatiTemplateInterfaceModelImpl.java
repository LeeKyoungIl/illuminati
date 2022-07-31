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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.ChangedJsElement;
import me.phoboslabs.illuminati.common.dto.IlluminatiInterfaceModel;
import me.phoboslabs.illuminati.common.dto.RequestGeneralModel;
import me.phoboslabs.illuminati.common.dto.RequestHeaderModel;
import me.phoboslabs.illuminati.common.dto.ServerInfo;
import me.phoboslabs.illuminati.common.dto.enums.IlluminatiInterfaceType;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class IlluminatiTemplateInterfaceModelImpl implements IlluminatiInterfaceModel {

    @Expose
    private String parentModuleName;
    @Expose
    private ServerInfo serverInfo;
    @Expose
    private Map<String, Object> jvmInfo;

    @Expose
    private String id;
    @Expose
    private String illuminatiUniqueUserId;
    @Expose
    private String packageType;
    @Expose
    protected RequestGeneralModel general;
    @Expose
    protected RequestHeaderModel header;
    @Expose
    private ChangedJsElement changedJsElement;
    @Expose
    private long elapsedTime;
    @Expose
    private long timestamp;
    @Expose
    private String logTime;
    @Expose
    protected Map<String, Object> output;

    @Expose
    private boolean isActiveChaosBomber = false;

    private Date localTime;
    private Object[] paramValues;

    private static final List<String> TRANSACTION_IDS = new ArrayList<>();

    static {
        TRANSACTION_IDS.add(ILLUMINATI_GPROC_ID_KEY);
        TRANSACTION_IDS.add(ILLUMINATI_SPROC_ID_KEY);
        TRANSACTION_IDS.add(ILLUMINATI_UNIQUE_USER_ID_KEY);
    }

    public IlluminatiTemplateInterfaceModelImpl() {
    }

    public IlluminatiTemplateInterfaceModelImpl(IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModelImpl) {
        LocalDateTime parsedLocalDateTime = LocalDateTime.now();

        this.localTime = Date.from(parsedLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        this.generateAggregateId();

        this.elapsedTime = illuminatiDataInterfaceModelImpl.getElapsedTime();
        this.output = illuminatiDataInterfaceModelImpl.getOutput();

        this.timestamp = localTime.getTime();
        this.logTime = parsedLocalDateTime.format(IlluminatiConstant.DATE_FORMAT_EVENT);
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

    public IlluminatiTemplateInterfaceModelImpl initBasicJvmInfo(Map<String, Object> jvmInfo) {
        this.jvmInfo = jvmInfo;
        return this;
    }

    public IlluminatiTemplateInterfaceModelImpl initStaticInfo(String parentModuleName, ServerInfo serverInfo) {
        this.parentModuleName = parentModuleName;
        this.serverInfo = serverInfo;
        return this;
    }

    public IlluminatiTemplateInterfaceModelImpl addBasicJvmMemoryInfo(Map<String, Object> jvmMemoryInfo) {
        if (this.jvmInfo == null) {
            this.jvmInfo = new HashMap<>();
        }

        jvmMemoryInfo.forEach((key, value) -> this.jvmInfo.put(key, value));

        return this;
    }

    public String getJsonString() {
        return IlluminatiConstant.ILLUMINATI_GSON_EXCLUDE_NULL_OBJ.toJson(this, IlluminatiTemplateInterfaceModelImpl.class);
    }

    private boolean isEqualsGProcId(String illuminatiGProcId) {
        return StringObjectUtils.isValid(illuminatiGProcId) && (illuminatiGProcId.equals(
            this.changedJsElement.getIlluminatiGProcId()));
    }

    private boolean isEqualsSProcId(String illuminatiSProcId) {
        return StringObjectUtils.isValid(illuminatiSProcId) && (illuminatiSProcId.equals(
            this.changedJsElement.getIlluminatiSProcId()));
    }

    public IlluminatiTemplateInterfaceModelImpl setJavascriptUserAction() {
        if (this.changedJsElement != null
            && this.isEqualsGProcId(this.header.getIlluminatiGProcId()) && this.isEqualsSProcId(
            this.header.getIlluminatiSProcId())) {
            this.changedJsElement.convertListToMap();
        }

        return this;
    }

    public RequestGeneralModel getGeneralRequestMethodInfo() {
        return this.general;
    }

    public long getElapsedTime() {
        return this.elapsedTime;
    }

    public String getLogTime() {
        return this.logTime;
    }

    public Map<String, Object> getOutput() {
        return this.output;
    }

    // ################################################################################################################
    // ### protected methods                                                                                        ###
    // ################################################################################################################

    protected String getId() {
        return this.id;
    }

    protected String getParentModuleName() {
        return this.parentModuleName;
    }

    // ################################################################################################################
    // ### private methods                                                                                          ###
    // ################################################################################################################

    private IlluminatiTemplateInterfaceModelImpl initReqHeaderInfo(RequestHeaderModel requestHeaderModel) {
        this.header = requestHeaderModel;
        return this;
    }

    private IlluminatiTemplateInterfaceModelImpl setPackageType(String packageType) {
        this.packageType = packageType;
        return this;
    }

    private IlluminatiTemplateInterfaceModelImpl loadClientInfo(Map<String, String> clientInfoMap) {
        if (clientInfoMap == null) {
            return this;
        }

        if (this.general == null) {
            this.general = new RequestGeneralModel();
        }

        this.general.initClientInfo(clientInfoMap);
        return this;
    }

    private IlluminatiTemplateInterfaceModelImpl staticInfo(Map<String, Object> staticInfo) {
        if (this.serverInfo != null && !this.serverInfo.isAlreadySetServerDomainAndPort()) {
            this.serverInfo.setStaticInfoFromRequest(staticInfo);
        }
        return this;
    }

    private IlluminatiTemplateInterfaceModelImpl isActiveChaosBomber(boolean isActiveChaosBomber) {
        this.isActiveChaosBomber = isActiveChaosBomber;
        return this;
    }

    private IlluminatiTemplateInterfaceModelImpl initUniqueUserId(String illuminatiUniqueUserId) {
        this.illuminatiUniqueUserId = illuminatiUniqueUserId;
        return this;
    }

    private IlluminatiTemplateInterfaceModelImpl checkAndSetTransactionIdFromPostBody(String postBody) {
        if (!StringObjectUtils.isValid(postBody)) {
            return this;
        }

        final String[] postArrayData = postBody.split("&");

        if (postArrayData.length <= 0) {
            return this;
        }

        Arrays.stream(postArrayData).map(postArrayDatum -> postArrayDatum.split("="))
            .filter(postElementArrayData -> postElementArrayData.length == 2)
            .<Consumer<? super String>>map(postElementArrayData -> keyValue -> {
                final String postElementKey = postElementArrayData[0];
                final String postElementValue = postElementArrayData[1];
                if (!keyValue.equals(postElementKey)) {
                    return;
                }
                if (ILLUMINATI_GPROC_ID_KEY.equals(keyValue) && !StringObjectUtils.isValid(this.header.getIlluminatiGProcId())) {
                    this.header.setGlobalTransactionId(postElementValue);
                } else if (ILLUMINATI_SPROC_ID_KEY.equals(keyValue) && !StringObjectUtils.isValid(
                    this.header.getIlluminatiSProcId())) {
                    this.header.setSessionTransactionId(postElementValue);
                } else if (ILLUMINATI_UNIQUE_USER_ID_KEY.equals(keyValue) && !StringObjectUtils.isValid(
                    this.illuminatiUniqueUserId)) {
                    this.illuminatiUniqueUserId = postElementValue;
                }
            }).forEach(TRANSACTION_IDS::forEach);

        return this;
    }

    private void generateAggregateId() {
        this.id = StringObjectUtils.generateId(this.localTime.getTime(), null);
    }

    private IlluminatiTemplateInterfaceModelImpl setMethod(MethodSignature methodSignature) {
        if (this.general == null) {
            this.general = new RequestGeneralModel();
        }
        this.general.setMethod(methodSignature.getMethod(), methodSignature.getParameterNames(), this.paramValues);
        return this;
    }

    @Override
    public IlluminatiInterfaceType getInterfaceType() throws Exception {
        throw new Exception("This feature is not available here.");
    }

    @Override
    public void setIlluminatiInterfaceType(IlluminatiInterfaceType illuminatiInterfaceType) throws Exception {
        throw new Exception("This feature is not available here.");
    }
}