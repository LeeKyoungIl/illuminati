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

package me.phoboslabs.illuminati.elasticsearch.infra.param.source;

import com.google.gson.annotations.Expose;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class EsSourceBuilder {

    @Expose
    private final EsSource esSource = new EsSource();

    public static EsSourceBuilder Builder () {
        return new EsSourceBuilder();
    }

    private EsSourceBuilder () {

    }

    public EsSourceBuilder setSource (String column) {
        if (StringObjectUtils.isValid(column) == false) {
            return this;
        }
        this.esSource.setSource(column);
        return this;
    }

    public EsSourceBuilder setSource (List<String> sources) {
        if (CollectionUtils.isNotEmpty(sources)) {
            for (String source : sources) {
                this.setSource(source);
            }
        }
        return this;
    }

    public List<String> build () {
        return this.esSource.getSource();
    }
}
