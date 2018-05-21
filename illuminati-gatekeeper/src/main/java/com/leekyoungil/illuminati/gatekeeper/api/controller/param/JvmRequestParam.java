package com.leekyoungil.illuminati.gatekeeper.api.controller.param;

import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JvmRequestParam {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Setter
    private int size = 10;
    @Setter
    private int from = 0;
    private OrderType orderType = OrderType.getOrderType("ASC");
    private String gte;
    private String lte;

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

    public void setOrderType (String orderType) {
        if (StringObjectUtils.isValid(orderType) == true) {
            this.orderType = OrderType.getOrderType(orderType);
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

        if (rangeTimestamp.size() > 0) {
            Map<String, Object> range = new HashMap<>();
            range.put("logTime", rangeTimestamp);
            param.put("range", range);
        }

        Map<String, Object> sort = new HashMap<>();
        sort.put("logTime", this.orderType.getOrderType());
        param.put("sort", sort);

        return param;
    }
}
