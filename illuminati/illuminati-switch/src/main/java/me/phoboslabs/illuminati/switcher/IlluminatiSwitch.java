package me.phoboslabs.illuminati.switcher;

import me.phoboslabs.illuminati.switcher.http.IlluminatiSwitchHttp;
import me.phoboslabs.illuminati.switcher.http.impl.IlluminatiSwitchHttpImpl;
import me.phoboslabs.illuminati.switcher.properties.IlluminatiSwitchPropertiesImpl;
import me.phoboslabs.illuminati.common.http.IlluminatiHttpClient;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.properties.IlluminatiPropertiesHelper;
import me.phoboslabs.illuminati.common.util.SystemUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IlluminatiSwitch {

    private static final Logger ILLUMINATI_SWITCH_LOGGER = LoggerFactory.getLogger(IlluminatiSwitch.class);

    private final static String ILLUMINATI_SWITCH_VALUE_GIT_URL = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiSwitchPropertiesImpl.class, "illuminati", "illuminatiSwitchValueURL", null);
    private static String BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL;

    private static IlluminatiSwitchHttp ILLUMINATI_SWITCH_HTTP;

    static {
        BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiSwitchPropertiesImpl.class, "illuminati", "illuminatiSwitchValueURLCheckInterval", null);

        if (BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL == null) {
            BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL = IlluminatiConstant.BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL;
        }

        initIlluminatiSwitchChecker();
        initIlluminatiSwitchThread();
    }

    private static void initIlluminatiSwitchChecker () {
        if (StringUtils.isNotEmpty(ILLUMINATI_SWITCH_VALUE_GIT_URL)) {
            ILLUMINATI_SWITCH_HTTP = new IlluminatiSwitchHttpImpl(new IlluminatiHttpClient(), ILLUMINATI_SWITCH_VALUE_GIT_URL);
        } else {
            ILLUMINATI_SWITCH_LOGGER.debug("there is no 'git url value' in properties or yml.");
        }
    }

    private static void initIlluminatiSwitchThread() {
        if (ILLUMINATI_SWITCH_HTTP == null) {
            final String errorMessage = "ILLUMINATI_SWITCH_THREAD is not activated. (ILLUMINATI_SWITCH_HTTP is null)";
            ILLUMINATI_SWITCH_LOGGER.debug(errorMessage);
            return;
        }

        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        setIlluminatiSwitchValue(ILLUMINATI_SWITCH_HTTP.getByGetMethod());
                    } catch (Exception ex) {
                        ILLUMINATI_SWITCH_LOGGER.error(ex.getMessage(), ex);
                    }

                    try {
                        Thread.sleep(Long.parseLong(BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL));
                    } catch (InterruptedException ignore) {}
                }
            }
        };

        SystemUtil.createSystemThread(runnable, "ILLUMINATI_SWITCH_THREAD");
    }

    private static void setIlluminatiSwitchValue (Object result) throws Exception {
        if (result == null) {
            final String errorMessage = "check your 'git url value'";
            ILLUMINATI_SWITCH_LOGGER.debug(errorMessage);
            throw new Exception(errorMessage);
        }
        String[] illuminatiSwitchValueArray = ((String) result).split(":");

        if (illuminatiSwitchValueArray.length == 2 && StringUtils.isNotEmpty(illuminatiSwitchValueArray[1])) {
            boolean switchValue = Boolean.valueOf(illuminatiSwitchValueArray[1].toLowerCase().indexOf("true") > -1 ? "true" : "false");

            if (switchValue != IlluminatiConstant.ILLUMINATI_SWITCH_VALUE.get()) {
                ILLUMINATI_SWITCH_LOGGER.debug("ILLUMINATI_SWITCH_VALUE has changed to " + switchValue);
                IlluminatiConstant.ILLUMINATI_SWITCH_VALUE.set(switchValue);
            }
        }
    }
}