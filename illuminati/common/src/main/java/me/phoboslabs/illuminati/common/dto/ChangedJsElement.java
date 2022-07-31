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

package me.phoboslabs.illuminati.common.dto;

import com.google.gson.annotations.Expose;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.apache.commons.collections.CollectionUtils;

public class ChangedJsElement {

    private String illuminatiSProcId;
    private String illuminatiGProcId;
    private List<ChangedValue> changedValues;
    @Expose
    private Map<Integer, ChangedValue> changedValueMap;

    public void setIlluminatiGProcId(String illuminatiGProcId) {
        this.illuminatiGProcId = illuminatiGProcId;
    }

    public void setIlluminatiSProcId(String illuminatiSProcId) {
        this.illuminatiSProcId = illuminatiSProcId;
    }

    public void setChangedValues(List<ChangedValue> changedValues) {
        this.changedValues = changedValues;
    }

    public String getIlluminatiGProcId() {
        return this.illuminatiGProcId;
    }

    public String getIlluminatiSProcId() {
        return this.illuminatiSProcId;
    }

    public void convertListToMap() {
        if (CollectionUtils.isEmpty(this.changedValues)) {
            return;
        }

        if (this.changedValueMap == null) {
            this.changedValueMap = new HashMap<>();
        }

        IntStream.range(0, this.changedValues.size())
            .forEach(i -> this.changedValueMap.put(i, this.changedValues.get(i)));

        this.changedValues = null;
    }
}
