package com.leekyoungil.illuminati.common.dto.impl;

import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.dto.ChangedJsElement;
import com.leekyoungil.illuminati.common.dto.IlluminatiInterfaceModel;
import com.leekyoungil.illuminati.common.dto.RequestHeaderModel;
import com.leekyoungil.illuminati.common.dto.enums.IlluminatiInterfaceType;
import com.leekyoungil.illuminati.common.dto.enums.IlluminatiTransactionIdType;
import com.leekyoungil.illuminati.common.util.ConvertUtil;
import com.leekyoungil.illuminati.common.util.SystemUtil;
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

    public IlluminatiDataInterfaceModelImpl(final HttpServletRequest request, final MethodSignature signature, final Object[] args, long elapsedTime, final Map<String, Object> resultMap) {
        this.signature = signature;
        this.args = args;
        this.elapsedTime = elapsedTime;
        this.output = this.getOutputData(resultMap);

        this.initDataFromHttpRequest(request);
    }

    private void initDataFromHttpRequest (final HttpServletRequest request) {
        this.requestHeaderModel = new RequestHeaderModel()
            .setRequestInfo(request)
            .setSessionTransactionId(SystemUtil.generateTransactionIdByRequest(request, IlluminatiTransactionIdType.ILLUMINATI_S_PROC_ID))
            .setGlobalTransactionId(SystemUtil.generateTransactionIdByRequest(request, IlluminatiTransactionIdType.ILLUMINATI_G_PROC_ID))
            .setTransactionId(SystemUtil.generateTransactionIdByRequest(request, IlluminatiTransactionIdType.ILLUMINATI_PROC_ID));

        this.illuminatiUniqueUserId = SystemUtil.getValueFromHeaderByKey(request, ILLUMINATI_UNIQUE_USER_ID_KEY);
        this.clientInfoMap = ConvertUtil.getClientInfoFromHttpRequest(request);
        this.staticInfo = ConvertUtil.getStaticInfoFromHttpRequest(request);
        this.isActiveChaosBomber = ConvertUtil.getChaosBomberFromHttpRequest(request);

        this.setChangedJsElement();
    }

    private Map<String, Object> getOutputData (Map<String, Object> resultMap) {
        Map<String, Object> resultObjectMap = new HashMap<String, Object>();
        try {
            resultObjectMap.put(OUTPUT_RESULT_OBJECT_KEY_NAME, IlluminatiConstant.ILLUMINATI_GSON_OBJ.fromJson((String) resultMap.get(OUTPUT_RESULT_KEY_NAME), IlluminatiConstant.TYPE_FOR_TYPE_TOKEN));
        } catch (Exception ignore) {
            resultObjectMap.put(OUTPUT_RESULT_STRING_KEY_NAME, resultMap);
        }
        return resultObjectMap;
    }

    private void setChangedJsElement () {
        for (Class paramType : this.signature.getParameterTypes()) {
            if (CHANGED_JS_ELEMENT_CLASS_SIMPLE_NAME.equalsIgnoreCase(paramType.getSimpleName())
                    && this.args != null && this.args.length > 0) {
                this.changedJsElement = (ChangedJsElement) this.args[0];
            }
        }

    }

    public boolean isValid () {
        if (this.requestHeaderModel == null) {
            this.logger.warn("request is must not null");
            return  false;
        }
        if (signature == null) {
            this.logger.warn("signature is must not null");
            return  false;
        }

        return true;
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
    public IlluminatiInterfaceType getInterfaceType() {
        return null;
    }

    @Override
    public void setIlluminatiInterfaceType(IlluminatiInterfaceType illuminatiInterfaceType) {

    }
}
