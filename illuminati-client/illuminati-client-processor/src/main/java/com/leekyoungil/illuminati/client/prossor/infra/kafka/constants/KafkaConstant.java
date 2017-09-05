package com.leekyoungil.illuminati.client.prossor.infra.kafka.constants;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class KafkaConstant {

    public final static String VALUE_SERIALIZER_TYPE_BYTE = "org.apache.kafka.common.serialization.ByteArraySerializer";
    public final static String VALUE_PARTITIONER = "RoundRobinPartitioner";
    public final static String VALUE_ASYNC_MAX_MESSAGE_AT_ONCE = "256";
    public final static String VALUE_LINGER_MS_SIZE = "0";
    public final static String VALUE_RETRIES_CONFIG = "3";
    public final static String VALUE_METADATA_MAX_AGE_CONFIG = "100000";
    public final static String VALUE_MAX_REQUEST_SIZE_CONFIG = "5000000";
    public final static String VALUE_RECONNECT_BACKOFF_MS_CONFIG = "100";
    public final static String VALUE_MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION = "1024";
    public final static String VALUE_BUFFER_MEMORY_CONFIG = "1024000";
    public final static String VALUE_SEND_BUFFER_CONFIG = "102400";
    public final static String VALUE_RECEIVE_BUFFER_CONFIG = "102400";
}
