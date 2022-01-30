package me.phoboslabs.illuminati.gatekeeper.api.controller;

import me.phoboslabs.illuminati.gatekeeper.api.controller.param.JvmRequestParam;
import me.phoboslabs.illuminati.gatekeeper.api.service.RestApiService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiJsonController {

    @Inject private RestApiService restApiService;

    @RequestMapping(value = "jvmInfo", method = RequestMethod.GET)
    public String getJvmInfo () {
        return restApiService.getJvmInfo();
    }

    @RequestMapping(value = "jvmInfo", method = RequestMethod.POST)
    public String getJvmInfoWithCondition (@RequestBody JvmRequestParam jvmRequestParam) {
        return restApiService.getJvmInfoWithCondition(jvmRequestParam);
    }

    @RequestMapping(value = "hostInfo", method = RequestMethod.GET)
    public String getHostInfo () {
        return restApiService.getHostInfo();
    }
}
