package com.leekyoungil.illuminati.client.switcher;

import com.leekyoungil.illuminati.client.switcher.http.IlluminatiSwitchHttp;
import com.leekyoungil.illuminati.client.switcher.http.impl.IlluminatiSwitchHttpImpl;
import com.leekyoungil.illuminati.client.switcher.properties.IlluminatiSwitchPropertiesImpl;
import com.leekyoungil.illuminati.common.http.IlluminatiHttpClient;
import com.leekyoungil.illuminati.common.properties.IlluminatiConstant;
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IlluminatiSwitch {

    private static final Logger ILLUMINATI_SWITCH_LOGGER = LoggerFactory.getLogger(IlluminatiSwitch.class);

    public static Thread ILLUMINATI_SWITCH_THREAD;

    private final static String ILLUMINATI_SWITCH_VALUE_GIT_URL = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiSwitchPropertiesImpl.class, null, "illuminati", "illuminatiSwitchValueURL");
    private static String BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL;

    private static IlluminatiSwitchHttp ILLUMINATI_SWITCH_HTTP;

    static {
        BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiSwitchPropertiesImpl.class, null, "illuminati", "illuminatiSwitchValueURLCheckInterval");

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

    private static void initIlluminatiSwitchThread () {
        if (ILLUMINATI_SWITCH_HTTP == null) {
            ILLUMINATI_SWITCH_LOGGER.debug("ILLUMINATI_SWITCH_THREAD is not activated. (ILLUMINATI_SWITCH_HTTP is null)");
            return;
        }

        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    setIlluminatiSwitchValue(ILLUMINATI_SWITCH_HTTP.getByGetMethod());

                    try {
                        Thread.sleep(Long.parseLong(BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL));
                    } catch (InterruptedException e) {

                    }
                }
            }
        };

        ILLUMINATI_SWITCH_THREAD = new Thread(runnable);
        ILLUMINATI_SWITCH_THREAD.setName("ILLUMINATI_SWITCH_THREAD");
        ILLUMINATI_SWITCH_THREAD.setDaemon(true);
        ILLUMINATI_SWITCH_THREAD.start();
    }

    private static void setIlluminatiSwitchValue (Object result) {
        if (result != null) {
            String[] illuminatiSwitchValueArray = ((String) result).split(":");
            if (illuminatiSwitchValueArray.length == 2 && StringUtils.isNotEmpty(illuminatiSwitchValueArray[1])) {
                boolean switchValue = Boolean.valueOf(illuminatiSwitchValueArray[1].toLowerCase().indexOf("true") > -1 ? "true" : "false");
                if (switchValue != IlluminatiConstant.ILLUMINATI_SWITCH_VALUE.get()) {
                    ILLUMINATI_SWITCH_LOGGER.debug("ILLUMINATI_SWITCH_VALUE has changed to " + switchValue);
                    IlluminatiConstant.ILLUMINATI_SWITCH_VALUE.set(switchValue);
                }
            }
        } else {
            ILLUMINATI_SWITCH_LOGGER.debug("check your 'git url value'");
        }
    }
}