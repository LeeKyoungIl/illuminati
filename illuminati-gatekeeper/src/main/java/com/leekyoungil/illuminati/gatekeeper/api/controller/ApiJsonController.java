package com.leekyoungil.illuminati.gatekeeper.api.controller;

import com.leekyoungil.illuminati.gatekeeper.api.service.RestApiService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping(value = "/api/v1/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiJsonController {

    @Inject
    private RestApiService restApiService;

    @RequestMapping(value = "jvmInfo")
    public String getJvmInfo () {
        return restApiService.getJvmInfo();
    }
}
