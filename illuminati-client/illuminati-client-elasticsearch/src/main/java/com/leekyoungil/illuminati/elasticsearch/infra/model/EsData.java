package com.leekyoungil.illuminati.elasticsearch.infra.model;

import java.util.List;
import java.util.Map;

public interface EsData {

    List<Map<String, Object>> getEsDataList ();
}
