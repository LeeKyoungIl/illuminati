package com.leekyoungil.illuminati.client.switcher;

import com.leekyoungil.illuminati.client.switcher.http.IlluminatiSwitchHttp;
import com.leekyoungil.illuminati.client.switcher.http.impl.IlluminatiSwitchHttpImpl;
import com.leekyoungil.illuminati.client.switcher.properties.IlluminatiSwitchPropertiesImpl;
import com.leekyoungil.illuminati.common.http.IlluminatiHttpClient;
import com.leekyoungil.illuminati.common.properties.IlluminatiConstant;
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper;
import org.apache.commons.lang3.StringUtils;

public class IlluminatiSwitch {

    public static Thread ILLUMINATI_SWITCH_THREAD;

    private final static String ILLUMINATI_SWITCH_VALUE_GIT_URL = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiSwitchPropertiesImpl.class, null, "illuminati", "illuminatiSwitchValueURL");
    private final static IlluminatiSwitchHttp ILLUMINATI_SWITCH_HTTP = new IlluminatiSwitchHttpImpl(new IlluminatiHttpClient(), ILLUMINATI_SWITCH_VALUE_GIT_URL);

    static {
        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    Object result = ILLUMINATI_SWITCH_HTTP.getByGetMethod();
                    if (result != null) {
                        String[] illuminatiSwitchValueArray = ((String) result).split(":");
                        if (illuminatiSwitchValueArray.length == 2 && StringUtils.isNotEmpty(illuminatiSwitchValueArray[1])) {
                            IlluminatiConstant.ILLUMINATI_SWITCH_VALUE = Boolean.valueOf(illuminatiSwitchValueArray[1].toLowerCase().indexOf("true") > -1 ? "true" : "false");
                        }
                    }

                    try {
                        Thread.sleep(5000);
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
}