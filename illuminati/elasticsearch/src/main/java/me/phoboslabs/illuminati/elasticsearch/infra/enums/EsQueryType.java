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

public enum EsQueryType {
    MATCH("match"),
    MATCH_ALL("match_all"),
    TERM("term");

    private String matchType;

    EsQueryType(String matchType) {
        this.matchType = matchType;
    }

    public String getMatchType() {
        return this.matchType;
    }

    public static EsQueryType getMatchType(String matchType) throws Exception {
        switch (matchType) {
            case "match":
                return EsQueryType.MATCH;
            case "match_all":
                return EsQueryType.MATCH_ALL;
            case "term":
                return EsQueryType.TERM;
            default:
                throw new Exception("matchType must not be null.");
        }
    }

    public static String getMatchText() {
        return EsQueryType.MATCH.name().toLowerCase();
    }

    public static String getMatchAllText() {
        return EsQueryType.MATCH_ALL.name().toLowerCase();
    }

    public static String getTermText() {
        return EsQueryType.TERM.name().toLowerCase();
    }
}
