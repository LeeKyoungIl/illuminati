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

package me.phoboslabs.illuminati.elasticsearch.infra.properties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Properties;
import me.phoboslabs.illuminati.common.properties.IlluminatiBaseProperties;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import me.phoboslabs.illuminati.elasticsearch.infra.model.EsInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EsClientProperties extends IlluminatiBaseProperties {

    private EsInfo elasticsearchInfo;

    public EsClientProperties() {
        super();
    }

    public EsClientProperties(Properties prop) {
        super(prop);
    }

    public EsInfo getElasticsearchInfo() {
        return this.elasticsearchInfo;
    }

    public String getHost() throws Exception {
        if (this.elasticsearchInfo != null) {
            return this.elasticsearchInfo.getHost();
        }

        throw new Exception("elasticsearch host info must not be null.");
    }

    public int getPort() throws Exception {
        if (this.elasticsearchInfo != null) {
            return this.elasticsearchInfo.getPort();
        }
        throw new Exception("elasticsearch port info must not be null.");
    }

    public boolean isValid() {
        return this.elasticsearchInfo != null && StringObjectUtils.isValid(this.elasticsearchInfo.getHost())
            && this.elasticsearchInfo.getPort() > 0;
    }
}
