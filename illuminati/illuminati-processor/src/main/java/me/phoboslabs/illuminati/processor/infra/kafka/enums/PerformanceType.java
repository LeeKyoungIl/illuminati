package me.phoboslabs.illuminati.processor.infra.kafka.enums;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 11/07/2017.
 */
public enum PerformanceType {
    SLOW_BUT_GUARANTEE_DATA("all"),
    FAST_BUT_SOMETIMES_DISAPPEAR("1"),
    FASTEST_BUT_NO_GUARANTEE_DATA("0");

    private String type;

    PerformanceType (String type) {
        this.type = type;
    }

    public String getType () {
        return this.type;
    }
}
