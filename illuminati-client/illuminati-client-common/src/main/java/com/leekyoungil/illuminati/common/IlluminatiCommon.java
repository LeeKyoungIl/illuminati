package com.leekyoungil.illuminati.common;

import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.dto.enums.IlluminatiStorageType;
import com.leekyoungil.illuminati.common.properties.IlluminatiCommonProperties;
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import com.leekyoungil.illuminati.common.util.SystemUtil;

import java.util.Arrays;
import java.util.List;

import static com.leekyoungil.illuminati.common.constant.IlluminatiConstant.ILLUMINATI_BACKUP_ACTIVATION;

public class IlluminatiCommon {

    private static final String H2_CLASS_NAME = "org.h2.Driver";

    private static final List<IlluminatiStorageType> ILLUMINATI_BACKUP_CONFIGURATION_CLASS_NAME_ARRAY = Arrays.asList(IlluminatiStorageType.getEnumType(H2_CLASS_NAME));

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

        for (IlluminatiStorageType illuminatiStorageType : ILLUMINATI_BACKUP_CONFIGURATION_CLASS_NAME_ARRAY) {
            try {
                Class.forName(illuminatiStorageType.getType());
                IlluminatiConstant.ILLUMINATI_BACKUP_STORAGE_TYPE = illuminatiStorageType;
                IlluminatiConstant.ILLUMINATI_BACKUP_ACTIVATION = true;
                break;
            } catch (ClassNotFoundException e) {
                //my class isn't there!
                IlluminatiConstant.ILLUMINATI_BACKUP_ACTIVATION = false;
            }
        }
    }
}
