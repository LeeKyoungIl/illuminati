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

import java.util.ArrayList;
import java.util.List;

public class EsSource {

    @Expose
    private final List<String> source = new ArrayList<String>();

    EsSource () {

    }

    public void setSource (String columnName) {
        if (!this.source.contains(columnName)) {
            this.source.add(columnName);
        }
    }

    public List<String> getSource () {
        return this.source;
    }
}
