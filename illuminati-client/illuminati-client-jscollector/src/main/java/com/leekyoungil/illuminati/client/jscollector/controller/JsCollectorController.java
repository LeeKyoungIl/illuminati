package com.leekyoungil.illuminati.client.jscollector.controller;

import com.leekyoungil.illuminati.client.annotation.Illuminati;
import com.leekyoungil.illuminati.common.dto.ChangedJsElement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/illuminati/js/collector", produces = MediaType.APPLICATION_JSON_VALUE)
public class JsCollectorController {

    @Illuminati
    @PostMapping
    public void getByPot (@RequestBody ChangedJsElement changedJsElement) {

    }
}
