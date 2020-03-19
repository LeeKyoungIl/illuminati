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

package me.phoboslabs.illuminati.switcher.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.properties.IlluminatiBaseProperties;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;

import java.util.Properties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IlluminatiSwitchPropertiesImpl extends IlluminatiBaseProperties {

    private String illuminatiSwitchValueURL;
    private String illuminatiSwitchValueURLCheckInterval;

    public IlluminatiSwitchPropertiesImpl () {
        super();
    }

    public IlluminatiSwitchPropertiesImpl (final Properties prop) {
        super(prop);
    }

    public String getIlluminatiSwitchValueURL() {
        return StringObjectUtils.isValid(this.illuminatiSwitchValueURL) ? this.illuminatiSwitchValueURL : "false";
    }

    public String getIlluminatiSwitchValueURLCheckInterval() {
        return StringObjectUtils.isValid(this.illuminatiSwitchValueURLCheckInterval)
                ? this.illuminatiSwitchValueURLCheckInterval
                : IlluminatiConstant.BASIC_ILLUMINATI_SWITCH_VALUE_CHECK_INTERVAL;
    }
}
