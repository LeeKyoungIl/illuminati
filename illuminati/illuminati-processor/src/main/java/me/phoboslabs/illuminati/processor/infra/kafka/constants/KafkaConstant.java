package me.phoboslabs.illuminati.processor.infra.kafka.constants;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class KafkaConstant {

    public final static String VALUE_SERIALIZER_TYPE_BYTE = "org.apache.kafka.common.serialization.ByteArraySerializer";
    public final static String VALUE_PARTITIONER = "me.phoboslabs.illuminati.processor.infra.kafka.impl.RoundRobinPartitioner";
    public final static int VALUE_ASYNC_MAX_MESSAGE_AT_ONCE = 256;
    public final static int VALUE_LINGER_MS_SIZE = 10000;
    public final static int VALUE_RETRIES_CONFIG = 1;
    public final static int VALUE_METADATA_MAX_AGE_CONFIG = 100000;
    public final static int VALUE_MAX_REQUEST_SIZE_CONFIG = 5000000;
    public final static int VALUE_RECONNECT_BACKOFF_MS_CONFIG = 100;
    public final static int VALUE_MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION = 1024;
    public final static int VALUE_BUFFER_MEMORY_CONFIG = 10485760;
    public final static int VALUE_SEND_BUFFER_CONFIG = 10485760;
    public final static int VALUE_RECEIVE_BUFFER_CONFIG = 10485760;
    public final static int VALUE_TRANSACTION_TIMEOUT_MS_CONFIG = 10000;
    public final static int VALUE_REQUEST_TIMEOUT_MS_CONFIG = 10000;
}
