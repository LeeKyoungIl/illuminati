package com.leekyoungil.illuminati.client.prossor.infra.kafka.enums;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 11/07/2017.
 */
public enum PerformanceType {
    SLOW_BUT_GUARANTEE_DATA(-1),
    FAST_BUT_SOMETIMES_DISAPPEAR(1),
    FASTEST_BUT_NO_GUARANTEE_DATA(0);

    private int type;

    PerformanceType (int type) {
        this.type = type;
    }

    public int getType () {
        return this.type;
    }
}
