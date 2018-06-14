package com.leekyoungil.illuminati.gatekeeper.api.controller.param;

import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import com.leekyoungil.illuminati.elasticsearch.infra.enums.EsOrderType;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JvmRequestParam {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Setter private int size = 10;
    @Setter private int from = 0;
    private EsOrderType esOrderType = EsOrderType.getOrderType("ASC");
    private String gte;
    private String lte;
    @Setter private String hostName;

    public void setGte (String gte) {
        if (StringObjectUtils.isValid(gte) == true) {
            this.gte = gte.replace(" ", "T");
        }
    }

    public void setLte (String lte) {
        if (StringObjectUtils.isValid(lte) == true) {
            this.lte = lte.replace(" ", "T");
        }
    }

    public void setEsOrderType(String esOrderType) {
        if (StringObjectUtils.isValid(esOrderType) == true) {
            this.esOrderType = EsOrderType.getOrderType(esOrderType);
        }
    }

    public Map<String, Object> getParam () {
        Map<String, Object> param = new HashMap<>();
        param.put("size", this.size);
        param.put("from", this.from);

        Map<String, Object> rangeTimestamp = new HashMap<>();
        if (StringObjectUtils.isValid(this.gte) == true) {
            rangeTimestamp.put("gte", this.gte);
        }
        if (StringObjectUtils.isValid(this.lte) == true) {
            rangeTimestamp.put("lte", this.lte);
        }
        if (StringObjectUtils.isValid(this.hostName) == true) {
            Map<String, Object> match = new HashMap<>();
            match.put("hostName", hostName);
            param.put("match", match);
        }

        if (rangeTimestamp.size() > 0) {
            Map<String, Object> range = new HashMap<>();
            range.put("logTime", rangeTimestamp);
            param.put("range", range);
        }

        Map<String, Object> sort = new HashMap<>();
        sort.put("logTime", this.esOrderType);
        param.put("sort", sort);

        return param;
    }
}
