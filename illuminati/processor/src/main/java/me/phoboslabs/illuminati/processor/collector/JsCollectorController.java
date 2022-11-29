/*
 * Copyright 2017 Phoboslabs.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.phoboslabs.illuminati.processor.collector;

import me.phoboslabs.illuminati.annotation.Illuminati;
import me.phoboslabs.illuminati.annotation.enums.PackageType;
import me.phoboslabs.illuminati.common.dto.ChangedJsElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/collect-log/v1/illuminati/js", produces = MediaType.APPLICATION_JSON_VALUE)
public class JsCollectorController {

    private final static Logger JS_COLLECTOR_LOGGER = LoggerFactory.getLogger(JsCollectorController.class);

    @Illuminati(packageType = PackageType.JAVASCRIPT)
    @PostMapping
    public void getByPost(@RequestBody ChangedJsElement changedJsElement) {
        JS_COLLECTOR_LOGGER.info("Send to Illuminati Store Success. (By Post)");
    }

    @Illuminati(packageType = PackageType.JAVASCRIPT)
    @GetMapping
    public void getByGet(@RequestBody ChangedJsElement changedJsElement) {
        JS_COLLECTOR_LOGGER.info("Send to Illuminati Store Success. (By Get)");
    }
}