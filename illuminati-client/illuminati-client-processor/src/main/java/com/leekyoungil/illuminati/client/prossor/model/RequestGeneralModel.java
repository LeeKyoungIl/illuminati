package com.leekyoungil.illuminati.client.prossor.model;

import com.google.gson.annotations.Expose;
import com.leekyoungil.illuminati.client.prossor.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class RequestGeneralModel {

    private static final Logger REQUEST_GENERAL_MODEL_LOGGER = LoggerFactory.getLogger(RequestGeneralModel.class);

    @Expose private String clientIp;
    @Expose private String methodName;
    @Expose private String methodParams;
    @Expose private String path;
    @Expose private String anotherPath;
    @Expose private String queryString;

    private Method method;

    public RequestGeneralModel (HttpServletRequest request) {
        if (request != null) {
            this.initClientInfo(request);
        }
    }

    public RequestGeneralModel (final Map<String, Object> requestMap) {
        if (requestMap == null || requestMap.isEmpty()) {
            REQUEST_GENERAL_MODEL_LOGGER.error("Sorry. check your requestMap variable.");
            return;
        }

        for (Map.Entry<String, Object> entry : requestMap.entrySet() ) {
            if (entry.getValue() == null) {
                REQUEST_GENERAL_MODEL_LOGGER.debug("Sorry. check your requestMap "+entry.getKey()+" key variable.");
                continue;
            }

            try {
                final Field field = this.getClass().getDeclaredField(entry.getKey());
                field.setAccessible(true);

                if ("serverPort".equals(entry.getKey())) {
                    field.setInt(this, (Integer) entry.getValue());
                } else {
                    field.set(this, entry.getValue().toString());
                }
            } catch (NoSuchFieldException ex) {
                REQUEST_GENERAL_MODEL_LOGGER.error("Sorry. check your class field. ("+ex.toString()+")");
            } catch (IllegalAccessException ex) {
                REQUEST_GENERAL_MODEL_LOGGER.error("Sorry. check your class field permission. ("+ex.toString()+")");
            }
        }
    }

    private void initClientInfo (HttpServletRequest request) {
        this.path = request.getRequestURI();
        this.queryString = request.getQueryString();

        this.clientIp = request.getHeader("X-FORWARDED-FOR");
        if (this.clientIp == null) {
            this.clientIp = request.getRemoteAddr();
        }

        // some frameworks (ex. grails) we can't be find requestUri by getRequestURI.
        // so add this line for get requestUri
        try {
            this.anotherPath = request.getAttribute("javax.servlet.forward.request_uri").toString();
        } catch (Exception ex) {
            // ignore this exception
        }
    }

    public String getServerIp() {
        return this.getServerIp();
    }

    public void setMethod(final Method method, final String[] paramNames, final Object[] paramValues) {
        this.method = method;
        this.methodName = this.method.toString();

        // if java8 : StringJoin....
        try {
            if (paramNames != null && paramValues != null) {
                final String[] param = new String[paramNames.length];
                for (int i=0; i<paramNames.length; i++) {
                    final StringBuilder tmpParams = new StringBuilder();
                    tmpParams.append(paramNames[i]);
                    tmpParams.append(" : ");

                    String paramValue = StringUtils.objectToString(paramValues[i]);

                    if (StringUtils.isValid(paramValue)) {
                        tmpParams.append(paramValue);
                    }

                    param[i] = tmpParams.toString();
                }

                if (param.length > 0) {
                    this.methodParams = org.apache.commons.lang3.StringUtils.join(param, ", ");
                }
            }
        } catch (Exception ex) {
            REQUEST_GENERAL_MODEL_LOGGER.error("Sorry. check your class method params. ("+ex.toString()+")");
        }
    }

    public void setCustomForEnv () {
        this.setReplaceMethodName("public java.lang.Object ");
        this.setPathForGrails();
    }

    private void setReplaceMethodName(final String replaceText) {
        if (StringUtils.isValid(this.methodName)) {
            this.methodName = this.methodName.replace(replaceText, "");
        }
    }

    private void setPathForGrails () {
        // grails always edit path in header ex) /gift/list => /grails/gift/list.dispatch
        //final String[] deleteKeyword = new String[]{"/grails", ".dispatch"};
        //final int[] deleteKeywordIndex = new int[]{0, -1};

        //this.path = StringUtils.deleteKeywordInString(this.path, deleteKeyword, deleteKeywordIndex);

        // It should not affect anything other than grails. Fuxx grails
        if (this.path.indexOf("/grails") == 0 && this.path.indexOf(".dispatch") > -1
                && (this.path.indexOf(".dispatch") + 9) == this.path.length()) {
            this.path = this.path.replace("/grails", "");
            this.path = this.path.substring(0, this.path.indexOf(".dispatch"));
        }
    }

    public String getMethodName () {
        return this.methodName;
    }
}
