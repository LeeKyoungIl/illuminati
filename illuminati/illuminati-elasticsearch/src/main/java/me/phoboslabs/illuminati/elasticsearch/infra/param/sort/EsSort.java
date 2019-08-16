package me.phoboslabs.illuminati.elasticsearch.infra.param.sort;

import com.google.gson.annotations.Expose;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;

import java.util.HashMap;
import java.util.Map;

public class EsSort {

    @Expose
    private Map<String, String> sort = new HashMap<String, String>();

    public EsSort() {}

    public void setOrderDataToMap(String key, String orderByString) {
        if (StringObjectUtils.isValid(key) && StringObjectUtils.isValid(orderByString)) {
            this.sort.put(key, orderByString);
        }
    }

    public Map<String, String> getSort () {
        return this.sort;
    }
}
