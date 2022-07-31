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

package me.phoboslabs.illuminati.common.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Properties;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;

/**
 * This class is used where 'IlluminatiPropertiesImpl' is not used.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IlluminatiCommonProperties extends IlluminatiBaseProperties {

    private String debug = "false";

    public IlluminatiCommonProperties() {
        super();
    }

    public IlluminatiCommonProperties(Properties prop) {
        super(prop);
    }

    public String getDebug() {
        return StringObjectUtils.isValid(this.debug) ? this.debug : "false";
    }
}
