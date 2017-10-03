package com.leekyoungil.illuminati.common.dto;

import com.google.gson.annotations.Expose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsEventInfo {

    private static final Logger JS_EVENT_INFO_LOGGER = LoggerFactory.getLogger(JsEventInfo.class);

    @Expose String targetId;
    @Expose String targetName;
    @Expose String targetValue;
}
