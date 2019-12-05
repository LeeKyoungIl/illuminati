package me.phoboslabs.illuminati.processor.infra.enums;

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

    public static BrokerType getEnumType(String type) throws Exception {
        switch (type.toLowerCase()) {
            case "rabbitmq" :
                return RABBITMQ;
            case "kafka" :
                return KAFKA;
            default :
                throw new Exception(type + "is not support yet.");
        }
    }
}
