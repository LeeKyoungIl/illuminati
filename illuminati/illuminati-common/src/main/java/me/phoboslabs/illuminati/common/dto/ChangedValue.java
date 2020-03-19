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

public class ChangedValue {

    @Expose private String elementUniqueId;
    @Expose private String elementType;
    @Expose private String attributeName;
    @Expose private String newData;
    @Expose private String oldData;
    @Expose private String index;

    public void setElementUniqueId(String elementUniqueId) {
        this.elementUniqueId = elementUniqueId;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }

    public void setOldData(String oldData) {
        this.oldData = oldData;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getElementUniqueId() {
        return this.elementUniqueId;
    }

    public String getElementType() {
        return this.elementType;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public String getNewData() {
        return this.newData;
    }

    public String getOldData() {
        return this.oldData;
    }

    public String getIndex() {
        return this.index;
    }
}

