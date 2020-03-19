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

package me.phoboslabs.illuminati.elasticsearch.infra.enums;

public enum EsRangeType {

    GT("gt"), // >
    LT("lt"), // <
    GTE("gte"), // >=
    LTE("lte"); // <=

    private String rangeType;

    EsRangeType (String rangeType) {
        this.rangeType = rangeType;
    }

    public String getRangeType () {
        return this.rangeType;
    }

    public static EsRangeType getRangeType (final String rangeType) throws Exception {
        switch (rangeType) {
            case "gt" :
                return EsRangeType.GT;
            case "lt" :
                return EsRangeType.LT;
            case "gte" :
                return EsRangeType.GTE;
            case "lte" :
                return EsRangeType.LTE;
            default:
                throw new Exception("rangeType must not be null.");
        }
    }
}
