package me.phoboslabs.illuminati.prossor.infra.enums;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 13/07/2017.
 */
public enum BrokerType {

    RABBITMQ("rabbitmq"),
    KAFKA("kafka");

    private final String type;

    BrokerType (String type) {
        this.type = type;
    }

    public String getType () {
        return this.type;
    }

    public static BrokerType getEnumType(String type) {
        if ("rabbitmq".equals(type)) {
            return RABBITMQ;
        } else if ("kafka".equals(type)) {
            return KAFKA;
        }

        return null;
    }
}
