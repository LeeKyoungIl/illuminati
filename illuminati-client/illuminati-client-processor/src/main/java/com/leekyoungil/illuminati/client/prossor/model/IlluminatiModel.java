package com.leekyoungil.illuminati.client.prossor.model;

import com.google.gson.annotations.Expose;
import com.leekyoungil.illuminati.client.prossor.init.IlluminatiClientInit;
import com.leekyoungil.illuminati.client.prossor.util.ConvertUtil;
import com.leekyoungil.illuminati.client.prossor.util.StringUtils;
import com.leekyoungil.illuminati.client.prossor.util.SystemUtil;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class IlluminatiModel implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    protected static final DateFormat DATE_FORMAT_EVENT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS");

    @Expose private final String parentModuleName = IlluminatiClientInit.PARENT_MODULE_NAME;
    @Expose private final ServerInfo serverInfo = IlluminatiClientInit.SERVER_INFO;
    @Expose private Map<String, Object> jvmInfo;

    @Expose private String id;
    @Expose protected RequestGeneralModel general;
    @Expose protected RequestHeaderModel header;
    @Expose private long elapsedTime;
    @Expose private long timestamp;
    @Expose private String logTime;
    @Expose protected Object output;

    @Expose private boolean isActiveChaosBomber = false;

    private Date localTime;

    public IlluminatiModel () {}

    public IlluminatiModel (final Date localTime
            , final long elapsedTime, final MethodSignature signature, final Object output, final Object[] paramValues) {
        this.localTime = localTime;
        this.generateAggregateId();

        this.elapsedTime = elapsedTime;
        this.output = output;

        this.timestamp = localTime.getTime();
        this.logTime = DATE_FORMAT_EVENT.format(localTime);

        this.setMethod(signature.getMethod(), signature.getParameterNames(), paramValues);
    }

    public void initReqHeaderInfo (final RequestHeaderModel requestHeaderModel) {
        this.header = requestHeaderModel;
    }

    public void initBasicJvmInfo (final Map<String, Object> jvmInfo) {
        this.jvmInfo = jvmInfo;
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
        if (!IlluminatiClientInit.SERVER_INFO.isAreadySetServerDomainAndPort()) {
            IlluminatiClientInit.SERVER_INFO.setStaticInfoFromRequest(staticInfo);
        }
    }

    public void isActiveChaosBomber (boolean isActiveChaosBomber) {
        this.isActiveChaosBomber = isActiveChaosBomber;
    }

    public String getJsonString () {
        return IlluminatiClientInit.ILLUMINATI_GSON_OBJ.toJson(this);
    }

    private void generateAggregateId () {
        this.id = StringUtils.generateId(this.localTime.getTime(), null);
    }

    protected String getId () {
        return this.id;
    }

    protected String getParentModuleName () {
        return this.parentModuleName;
    }

    private void setMethod (final Method method, final String[] paramNames, final Object[] paramValues) {
        if (this.general == null) {
            this.general = new RequestGeneralModel();
        }
        this.general.setMethod(method, paramNames, paramValues);
    }
}