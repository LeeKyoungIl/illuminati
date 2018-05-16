package com.leekyoungil.illuminati.common.dto;

import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;

public class IlluminatiRestApiResult<T> {

    private int code;
    private String message;
    private T result;

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void setCodeAndMessageOfResult (int code) {
        this.code = code;
        this.message = IlluminatiConstant.JSON_STATUS_CODE.getMessage(code);
    }
}
