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

import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.ChangedJsElement;
import me.phoboslabs.illuminati.common.dto.IlluminatiInterfaceModel;
import me.phoboslabs.illuminati.common.dto.RequestHeaderModel;
import me.phoboslabs.illuminati.common.dto.enums.IlluminatiInterfaceType;
import me.phoboslabs.illuminati.common.dto.enums.IlluminatiTransactionIdType;
import me.phoboslabs.illuminati.common.util.ConvertUtil;
import me.phoboslabs.illuminati.common.util.SystemUtil;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class IlluminatiDataInterfaceModelImpl implements IlluminatiInterfaceModel {

    private final Logger logger = LoggerFactory.getLogger(IlluminatiDataInterfaceModelImpl.class);

    private final MethodSignature signature;
    private final Object[] args;
    private final long elapsedTime;
    private final Map<String, Object> output;

    private String packageType = IlluminatiConstant.BASIC_PACKAGE_TYPE;
    private String illuminatiUniqueUserId;
    private RequestHeaderModel requestHeaderModel;
    private Map<String, String> clientInfoMap;
    private Map<String, Object> staticInfo;
    private boolean isActiveChaosBomber;

    private final static String CHANGED_JS_ELEMENT_CLASS_SIMPLE_NAME = "ChangedJsElement";
    private ChangedJsElement changedJsElement;

    private final static String OUTPUT_RESULT_KEY_NAME = "result";
    private final static String OUTPUT_RESULT_STRING_KEY_NAME = "resultString";
    private final static String OUTPUT_RESULT_OBJECT_KEY_NAME = "resultObject";

    private IlluminatiDataInterfaceModelImpl (HttpServletRequest request, MethodSignature signature, Object[] args, long elapsedTime, Map<String, Object> resultMap) {
        this.signature = signature;
        this.args = args;
        this.elapsedTime = elapsedTime;
        this.output = this.getOutputData(resultMap);

        this.initDataFromHttpRequest(request);
    }

    public static IlluminatiDataInterfaceModelImpl Builder (final HttpServletRequest request, final MethodSignature signature, final Object[] args, final long elapsedTime, final Map<String, Object> resultMap) {
        return new IlluminatiDataInterfaceModelImpl(request, signature, args, elapsedTime, resultMap);
    }

    private void initDataFromHttpRequest (final HttpServletRequest request) {
        this.requestHeaderModel = new RequestHeaderModel().setRequestInfo(request);

        try {
            final String sProcId = SystemUtil.generateTransactionIdByRequest(request, IlluminatiTransactionIdType.ILLUMINATI_S_PROC_ID);
            this.requestHeaderModel.setSessionTransactionId(sProcId);
        } catch (Exception ignore) {}
        try {
            final String gProcId = SystemUtil.generateTransactionIdByRequest(request, IlluminatiTransactionIdType.ILLUMINATI_G_PROC_ID);
            this.requestHeaderModel.setGlobalTransactionId(gProcId);
        } catch (Exception ignore) {}
        try {
            final String procId = SystemUtil.generateTransactionIdByRequest(request, IlluminatiTransactionIdType.ILLUMINATI_PROC_ID);
            this.requestHeaderModel.setTransactionId(procId);
        } catch (Exception ignore) {}

        try {
            this.illuminatiUniqueUserId = SystemUtil.getValueFromHeaderByKey(request, ILLUMINATI_UNIQUE_USER_ID_KEY);
        } catch (Exception ignore) {}
        try {
            this.clientInfoMap = ConvertUtil.getClientInfoFromHttpRequest(request);
        } catch (Exception ignore) {}

        this.staticInfo = ConvertUtil.getStaticInfoFromHttpRequest(request);
        this.isActiveChaosBomber = ConvertUtil.getChaosBomberFromHttpRequest(request);

        this.setChangedJsElement();
    }

    private Map<String, Object> getOutputData (Map<String, Object> resultMap) {
        Map<String, Object> resultObjectMap = new HashMap<>();
        try {
            resultObjectMap.put(OUTPUT_RESULT_OBJECT_KEY_NAME, IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson((String) resultMap.get(OUTPUT_RESULT_KEY_NAME), IlluminatiConstant.TYPE_FOR_TYPE_TOKEN));
        } catch (Exception ignore) {
            resultObjectMap.put(OUTPUT_RESULT_STRING_KEY_NAME, resultMap);
        }
        return resultObjectMap;
    }

    private boolean argsValidated() {
        return this.args != null && this.args.length > 0;
    }

    private void setChangedJsElement () {
        for (Class paramType : this.signature.getParameterTypes()) {
            if (CHANGED_JS_ELEMENT_CLASS_SIMPLE_NAME.equalsIgnoreCase(paramType.getSimpleName()) && this.argsValidated()) {
                this.changedJsElement = (ChangedJsElement) this.args[0];
            }
        }
    }

    public boolean isValid () {
        return this.requestHeaderModel != null && this.signature != null;
    }

    public IlluminatiDataInterfaceModelImpl setPackageType (String packageType) {
        this.packageType = packageType;
        return this;
    }

    public long getElapsedTime () {
        return this.elapsedTime;
    }
    public Map<String, Object> getOutput () {
        return this.output;
    }

    Object[] getParamValues () {
        return this.args;
    }

    ChangedJsElement getChangedJsElement () {
        return this.changedJsElement;
    }

    String getPackageType () {
        return this.packageType;
    }
    MethodSignature getSignature () {
        return this.signature;
    }
    RequestHeaderModel getRequestHeaderModel () {
        return this.requestHeaderModel;
    }
    String getIlluminatiUniqueUserId() {
        return this.illuminatiUniqueUserId;
    }
    Map<String, String> getClientInfoMap() {
        return this.clientInfoMap;
    }
    Map<String, Object> getStaticInfo() {
        return this.staticInfo;
    }
    boolean isActiveChaosBomber() {
        return this.isActiveChaosBomber;
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
