package me.phoboslabs.illuminati.gatekeeper.api.service;

import me.phoboslabs.illuminati.gatekeeper.api.controller.param.JvmRequestParam;
import me.phoboslabs.illuminati.gatekeeper.api.decorator.ApiDecorator;
import me.phoboslabs.illuminati.gatekeeper.api.decorator.ApiJsonDecorator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class RestApiService {

    @Inject private JvmInfoApiService jvmInfoApiService;
    @Inject private HostInfoService hostInfoService;

    public String getJvmInfo () {
        ApiDecorator<List<Map<String, Object>>> apiDecorator = new ApiJsonDecorator(jvmInfoApiService.getJvmInfoFromElasticsearch());
        return apiDecorator.getStringData();
    }

    public String getJvmInfoWithCondition(JvmRequestParam jvmRequestParam) {
        ApiDecorator<List<Map<String, Object>>> apiDecorator = new ApiJsonDecorator(jvmInfoApiService.getJvmInfoByConditionFromElasticsearch(jvmRequestParam.getParam()));
        return apiDecorator.getStringData();
    }

    public String getHostInfo () {
        ApiDecorator<List<Map<String, Object>>> apiDecorator = new ApiJsonDecorator(hostInfoService.getHostInfoFromElasticsearch());
        return apiDecorator.getStringData();
    }
}
