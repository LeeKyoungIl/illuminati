package com.leekyoungil.illuminati.gatekeeper.api.controller;

import com.leekyoungil.illuminati.gatekeeper.api.controller.param.JvmRequestParam;
import com.leekyoungil.illuminati.gatekeeper.api.service.RestApiService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiJsonController {

    @Inject
    private RestApiService restApiService;

    @RequestMapping(value = "jvmInfo", method = RequestMethod.GET)
    public String getJvmInfo () {
        return restApiService.getJvmInfo();
    }

    @RequestMapping(value = "jvmInfo", method = RequestMethod.POST)
    public String getJvmInfoWithCondition (JvmRequestParam jvmRequestParam) {
        return restApiService.getJvmInfoWithCondition(jvmRequestParam);
    }
}
