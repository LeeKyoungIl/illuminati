package com.leekyoungil.illuminati.client.prossor.model;

import com.google.gson.annotations.Expose;
import com.leekyoungil.illuminati.client.prossor.init.IlluminatiClientInit;
import com.leekyoungil.illuminati.client.prossor.util.StringUtils;
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

    private final Date localTime = new Date();

    private final int mb = 1024*1024;

    public IlluminatiModel () {}

    public IlluminatiModel (HttpServletRequest request, final long elapsedTime, final MethodSignature signature
            , final Object output, final Object[] paramValues) {
        this.generateAggregateId();

        this.general = new RequestGeneralModel(request);

        if (request != null) {
            this.setIlluminatiId(request);
            this.checkChaosBomber(request);
            this.header = new RequestHeaderModel(request);
            this.setStaticInfo(request);
        }

        this.elapsedTime = elapsedTime;
        this.output = output;

        this.timestamp = localTime.getTime();
        this.logTime = DATE_FORMAT_EVENT.format(localTime);

        this.getJvmMemoryStatus();
        this.setMethod(signature.getMethod(), signature.getParameterNames(), paramValues);
    }

    /**
     * for TestCase
     *
     * @param general
     * @param header
     * @param elapsedTime
     * @param output
     * @param id
     * @param timestamp
     */
    public IlluminatiModel (RequestGeneralModel general, RequestHeaderModel header, long elapsedTime, Object output
            , String id, long timestamp) {
        this.general = general;
        this.header = header;
        this.elapsedTime = elapsedTime;
        this.output = output;
        this.id = id;
        this.timestamp = timestamp;
    }

    private void setStaticInfo (HttpServletRequest request) {
        if (!IlluminatiClientInit.SERVER_INFO.isAreadySetServerDomainAndPort()) {
            IlluminatiClientInit.SERVER_INFO.setServerInfoFromRequest(request);
        }

        if (IlluminatiClientInit.JVM_INFO != null) {
            this.jvmInfo = new HashMap<String, Object>(IlluminatiClientInit.JVM_INFO);
        }
    }

    public String getJsonString () {
        return IlluminatiClientInit.ILLUMINATI_GSON_OBJ.toJson(this);
    }

    public ByteBuffer toAMQPBuffer() {
        final byte[] messageBytes = StringUtils.gzipMessage(getJsonString());
        ByteBuffer buffer = ByteBuffer.allocate(messageBytes.length);
        buffer.put(messageBytes);
        buffer.flip();
        return buffer;
    }

    private void generateAggregateId () {
        this.id = StringUtils.generateId(this.localTime.getTime(), null);
    }

    private void setIlluminatiId (HttpServletRequest request) {
        if (request.getAttribute("illuminatiProcId") == null) {
            request.setAttribute("illuminatiProcId", StringUtils.generateId(this.localTime.getTime(), "illuminatiProcId"));
        }
    }

    private void getJvmMemoryStatus () {
        this.jvmInfo.put("jvmUsedMemory", (IlluminatiClientInit.RUNTIME.totalMemory() - IlluminatiClientInit.RUNTIME.freeMemory()) / mb);
        this.jvmInfo.put("jvmFreeMemory", IlluminatiClientInit.RUNTIME.freeMemory() / mb);
        this.jvmInfo.put("jvmTotalMemory", IlluminatiClientInit.RUNTIME.totalMemory() / mb);
        this.jvmInfo.put("jvmMaxMemory", IlluminatiClientInit.RUNTIME.maxMemory() / mb);
    }

    protected String getId () {
        return this.id;
    }

    protected String getParentModuleName () {
        return this.parentModuleName;
    }

    private void setMethod (final Method method, final String[] paramNames, final Object[] paramValues) {
        if (this.general != null) {
            this.general.setMethod(method, paramNames, paramValues);
        }
    }

    private void checkChaosBomber (HttpServletRequest request) {
        if (request.getAttribute("ChaosBomber") != null && "true".equals(request.getAttribute("ChaosBomber").toString())) {
            this.isActiveChaosBomber = true;
            request.setAttribute("ChaosBomber", null);
        }
    }
}