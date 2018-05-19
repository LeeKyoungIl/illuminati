package com.leekyoungil.illuminati.gatekeeper.api.service;

import com.leekyoungil.illuminati.gatekeeper.api.controller.param.JvmRequestParam;
import com.leekyoungil.illuminati.gatekeeper.api.decorator.ApiDecorator;
import com.leekyoungil.illuminati.gatekeeper.api.decorator.ApiJsonDecorator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class RestApiService {

    @Inject
    private JvmInfoApiService jvmInfoApiService;

    public String getJvmInfo () {
        ApiDecorator<List<Map<String, Object>>> apiDecorator = new ApiJsonDecorator(jvmInfoApiService.getJvmInfoFromElasticsearch());
        return apiDecorator.getStringData();
    }

    public String getJvmInfoWithCondition(JvmRequestParam jvmRequestParam) {
        ApiDecorator<List<Map<String, Object>>> apiDecorator = new ApiJsonDecorator(jvmInfoApiService.getJvmInfoByConditionFromElasticsearch(jvmRequestParam.getParam()));
        return apiDecorator.getStringData();
    }
}
