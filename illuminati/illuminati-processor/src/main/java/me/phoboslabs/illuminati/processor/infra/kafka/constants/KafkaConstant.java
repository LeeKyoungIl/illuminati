/*
 * Copyright 2017 Phoboslabs.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
