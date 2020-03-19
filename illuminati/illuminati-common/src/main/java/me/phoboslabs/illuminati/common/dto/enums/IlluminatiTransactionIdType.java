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

package me.phoboslabs.illuminati.common.dto.enums;

public enum IlluminatiTransactionIdType {

    ILLUMINATI_G_PROC_ID("illuminatiGProcId"), // GLOBAL_TRANSACTION_ID_INCLUDE_JAVASCRIPT
    ILLUMINATI_S_PROC_ID("illuminatiSProcId"), // SESSION_TRANSACTION_ID_INCLUDE_JAVASCRIPT
    ILLUMINATI_PROC_ID("illuminatiProcId"); //GLOBAL_TRANSACTION_ID_SERVER

    private String value;

    IlluminatiTransactionIdType(String value) {
        this.value = value;
    }

    public String getValue () {
        return this.value;
    }
}
