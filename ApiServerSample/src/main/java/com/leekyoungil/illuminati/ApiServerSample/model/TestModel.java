package com.leekyoungil.illuminati.ApiServerSample.model;

import java.io.Serializable;

public class TestModel implements Serializable {

    private String name;
    private String userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
