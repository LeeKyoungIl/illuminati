package me.phoboslabs.illuminati.prossor.infra.kafka.enums;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 11/07/2017.
 */
public enum CommunicationType {
    SYNC("sync"),
    ASYNC("async");

    private final String type;

    CommunicationType (String type) {
        this.type = type;
    }

    public String getType () {
        return this.type;
    }
}
