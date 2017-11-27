package com.leekyoungil.illuminati.client.jscollector.controller;

import com.leekyoungil.illuminati.client.annotation.Illuminati;
import com.leekyoungil.illuminati.common.dto.ChangedJsElement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/illuminati/js/collector", produces = MediaType.APPLICATION_JSON_VALUE)
public class JsCollectorController {

    @Illuminati
    @PostMapping
    public void getByPost (@RequestBody ChangedJsElement changedJsElement) {

    }

    @Illuminati
    @GetMapping
    public void getByGet (@RequestBody ChangedJsElement changedJsElement) {

    }
}
