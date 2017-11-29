package com.leekyoungil.illuminati.common.dto;

import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class IlluminatiDataInterfaceModel {

    private static final Logger ILLUMINATI_DATA_INTERFACE_MODEL_LOGGER = LoggerFactory.getLogger(IlluminatiDataInterfaceModel.class);

    private final HttpServletRequest request;
    private final MethodSignature signature;
    private final Object[] args;
    private long elapsedTime = 0L;
    private final Object output;

    public IlluminatiDataInterfaceModel (final HttpServletRequest request, final MethodSignature signature, final Object[] args, long elapsedTime, final Object output) {
        this.request = request;
        this.signature = signature;
        this.args = args;
        this.elapsedTime = elapsedTime;
        this.output = output;
    }

    public boolean isValid () {
        if (request == null) {
            ILLUMINATI_DATA_INTERFACE_MODEL_LOGGER.warn("request is must not null");
            return  false;
        }
        if (signature == null) {
            ILLUMINATI_DATA_INTERFACE_MODEL_LOGGER.warn("signature is must not null");
            return  false;
        }

        return true;
    }

    public long getElapsedTime () {
        return this.elapsedTime;
    }
    public Object getOutput () {
        return this.output;
    }
    public Object[] getParamValues () {
        return this.args;
    }
    public MethodSignature getSignature () {
        return this.signature;
    }
    public HttpServletRequest getRequest () {
        return this.request;
    }
}
