package com.leekyoungil.illuminati.common;

import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.properties.IlluminatiCommonProperties;
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import com.leekyoungil.illuminati.common.util.SystemUtil;

import static com.leekyoungil.illuminati.common.constant.IlluminatiConstant.ILLUMINATI_BACKUP_ACTIVATION;

public class IlluminatiCommon {

    private static final String ILLUMINATI_BACKUP_CONFIGURATION_CLASS_NAME = "org.h2.Driver";

    public synchronized static void init () {
        final String debug = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiCommonProperties.class, null, "illuminati", "debug", null);
        if (StringObjectUtils.isValid(debug)) {
            IlluminatiConstant.ILLUMINATI_DEBUG = Boolean.valueOf(debug);
        }

        if (IlluminatiConstant.ILLUMINATI_DEBUG == true) {
            SystemUtil.createThreadStatusDebugThread();
        }

        if (IlluminatiPropertiesHelper.isIlluminatiSwitcherActive() == true) {
            IlluminatiConstant.ILLUMINATI_SWITCH_ACTIVATION = true;
        }

        try {
            Class.forName(ILLUMINATI_BACKUP_CONFIGURATION_CLASS_NAME);
            IlluminatiConstant.ILLUMINATI_BACKUP_ACTIVATION = true;
        } catch (ClassNotFoundException e) {
            //my class isn't there!
            IlluminatiConstant.ILLUMINATI_BACKUP_ACTIVATION = false;
        }
    }
}
