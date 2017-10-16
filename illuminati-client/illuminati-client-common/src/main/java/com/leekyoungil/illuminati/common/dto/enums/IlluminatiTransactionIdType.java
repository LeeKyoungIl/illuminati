package com.leekyoungil.illuminati.common.dto.enums;

public enum IlluminatiTransactionIdType {

    ILLUMINATI_G_PROC_ID("illuminatiGProcId"), // GLOBAL_TRANSACTION_ID_INCLUDE_JAVASCRIPT
    ILLUMINATI_PROC_ID("illuminatiProcId"); //GLOBALTRANSACTION_ID_SERVER

    private String value;

    IlluminatiTransactionIdType(String value) {
        this.value = value;
    }

    public String getValue () {
        return this.value;
    }
}
