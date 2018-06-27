package com.leekyoungil.illuminati.esconsumer.config.model;

import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import com.leekyoungil.illuminati.elasticsearch.infra.EsDocument;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsIndexStoreType;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsRefreshType;
import com.leekyoungil.illuminati.elasticsearch.model.IlluminatiEsTemplateInterfaceModelImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EsDocument(indexName = "sample-illuminati", type = "log", indexStoreType = EsIndexStoreType.FS, shards = 1, replicas = 0, refreshType = EsRefreshType.TRUE)
public class SampleEsTemplateInterfaceModelImpl extends IlluminatiEsTemplateInterfaceModelImpl {

    private final static Logger SAMPLE_ES_CONSUMER_LOGGER = LoggerFactory.getLogger(SampleEsTemplateInterfaceModelImpl.class);
    
    public SampleEsTemplateInterfaceModelImpl() {
        super();
    }

    public void customData () {
        this.general.setCustomForEnv();
        this.header.parsingCookie();
    }

    public String getMethodName () {
        if (this.general != null && StringObjectUtils.isValid(this.general.getMethodName())) {
            return this.general.getMethodName();
        }

        return null;
    }

    private void setCustomParsingCookie (String key, String value) {
        if (this.header != null) {
            this.header.setParsedCookieElement(key, value);
        } else {
            SAMPLE_ES_CONSUMER_LOGGER.info("Sorry. header object is null. have to init of header object.");
        }
    }
}
