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

package me.phoboslabs.illuminati.processor.infra.kafka.impl;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import me.phoboslabs.illuminati.common.util.NetworkUtil;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import me.phoboslabs.illuminati.processor.exception.PublishMessageException;
import me.phoboslabs.illuminati.processor.exception.ValidationException;
import me.phoboslabs.illuminati.processor.infra.IlluminatiInfraTemplate;
import me.phoboslabs.illuminati.processor.infra.common.BasicTemplate;
import me.phoboslabs.illuminati.processor.infra.kafka.constants.KafkaConstant;
import me.phoboslabs.illuminati.processor.infra.kafka.enums.CommunicationType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 06/07/2017.
 */
public class KafkaInfraTemplateImpl extends BasicTemplate implements IlluminatiInfraTemplate<String> {

    private static final Logger KAFKA_TEMPLATE_IMPL_LOGGER = LoggerFactory.getLogger(KafkaInfraTemplateImpl.class);

    private String topic;

    private final Properties kafkaProperties = new Properties();

    private Producer<String, byte[]> kafkaProducer;

    public KafkaInfraTemplateImpl(String propertiesName) {
        super(propertiesName);

        this.validateBasicTemplateClass();

        this.checkRequiredValuesForInit();

        this.topic = this.illuminatiProperties.getTopic();

        this.initProperties();
        this.initPublisher();
    }

    @Override
    protected void checkRequiredValuesForInit() {
        if (!StringObjectUtils.isValid(this.illuminatiProperties.getTopic())) {
            KAFKA_TEMPLATE_IMPL_LOGGER.error("error : topic variable is empty.");
            throw new ValidationException("error : topic variable is empty.");
        }
        if (!StringObjectUtils.isValid(this.illuminatiProperties.getClusterList())) {
            KAFKA_TEMPLATE_IMPL_LOGGER.error("error : brokerList variable is empty.");
            throw new ValidationException("error : brokerList variable is empty.");
        }
    }

    /**
     * ClusterList is for bootstrapping and the producer will only use it for getting metadata. the socket connections for sending
     * the actual dto will be established based on the broker informaion returned is the metadata.
     * <p>
     * The format is host1:port1,host2:port2,host3:port3 and the list can be a subset of brokers or a VIP pointing to a subset of
     * brokers.
     */
    private void setBasicProperties() {
        this.setKafkaProperties(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.illuminatiProperties.getClusterList());
        this.setKafkaProperties(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaConstant.VALUE_SERIALIZER_TYPE_BYTE);
        this.setKafkaProperties(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaConstant.VALUE_SERIALIZER_TYPE_BYTE);
        this.setKafkaProperties(ProducerConfig.PARTITIONER_CLASS_CONFIG, KafkaConstant.VALUE_PARTITIONER);
        this.setKafkaProperties(ProducerConfig.RETRIES_CONFIG, KafkaConstant.VALUE_RETRIES_CONFIG);
        this.setKafkaProperties(ProducerConfig.METADATA_MAX_AGE_CONFIG, KafkaConstant.VALUE_METADATA_MAX_AGE_CONFIG);
        this.setKafkaProperties(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, KafkaConstant.VALUE_MAX_REQUEST_SIZE_CONFIG);
        this.setKafkaProperties(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, KafkaConstant.VALUE_RECONNECT_BACKOFF_MS_CONFIG);
        this.setKafkaProperties(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
            KafkaConstant.VALUE_MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION);
        this.setKafkaProperties(ProducerConfig.BUFFER_MEMORY_CONFIG, KafkaConstant.VALUE_BUFFER_MEMORY_CONFIG);
        this.setKafkaProperties(ProducerConfig.SEND_BUFFER_CONFIG, KafkaConstant.VALUE_SEND_BUFFER_CONFIG);
        this.setKafkaProperties(ProducerConfig.RECEIVE_BUFFER_CONFIG, KafkaConstant.VALUE_RECEIVE_BUFFER_CONFIG);
        this.setKafkaProperties(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, KafkaConstant.VALUE_TRANSACTION_TIMEOUT_MS_CONFIG);
        this.setKafkaProperties(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, KafkaConstant.VALUE_REQUEST_TIMEOUT_MS_CONFIG);
    }

    private synchronized void initPublisher() {
        if (this.kafkaProducer == null) {
            this.kafkaProducer = new KafkaProducer<>(this.kafkaProperties);
        }
    }

    private void setKafkaProperties(String key, Object value) {
        this.kafkaProperties.put(key, value);
    }

    @Override
    protected void initProperties() {
        this.setBasicProperties();
        this.setPerformance();
        this.setIsAsync();
        this.setIsCompression();
    }

    @Override
    public void sendToIlluminati(String entity) throws Exception, PublishMessageException {
        if (this.kafkaProducer == null) {
            KAFKA_TEMPLATE_IMPL_LOGGER.error("kafka publisher not initialized.");
            throw new PublishMessageException("kafka publisher not initialized.");
        }

        try {
            this.sending = true;
            final Future<RecordMetadata> sendResult = this.kafkaProducer.send(
                new ProducerRecord<>(this.topic, entity.getBytes()));

            KAFKA_TEMPLATE_IMPL_LOGGER.debug("Message produced, offset: " + sendResult.get().offset());
            KAFKA_TEMPLATE_IMPL_LOGGER.debug("Message produced, partition : " + sendResult.get().partition());
            KAFKA_TEMPLATE_IMPL_LOGGER.debug("Message produced, topic: " + sendResult.get().topic());

            KAFKA_TEMPLATE_IMPL_LOGGER.info("successfully transferred dto to Illuminati broker(kafka).");
        } catch (Exception ex) {
            throw new PublishMessageException("failed to publish message : (" + ex.toString() + ")");
        } finally {
            this.sending = false;
        }
    }

    @Override
    public boolean canIConnect() {
        if (this.kafkaProducer == null) {
            return false;
        }

        int canIConnectCount = 0;
        try {
            List<String> clusterList = this.illuminatiProperties.getClusterArrayList();
            if (CollectionUtils.isNotEmpty(clusterList)) {
                for (String clusterAddress : clusterList) {
                    final String[] clusterAddressInfo = clusterAddress.split(":");
                    if (clusterAddressInfo.length != 2) {
                        KAFKA_TEMPLATE_IMPL_LOGGER.error("check kafka cluster({}). maybe typo in cluster address string.",
                            clusterAddress);
                    } else {
                        boolean connectResult = NetworkUtil.canIConnect(clusterAddressInfo[0],
                            Integer.parseInt(clusterAddressInfo[1]));
                        if (connectResult) {
                            canIConnectCount++;
                        } else {
                            KAFKA_TEMPLATE_IMPL_LOGGER.error("check kafka cluster. connection error ({})", clusterAddress);
                        }
                    }
                }
            } else {
                KAFKA_TEMPLATE_IMPL_LOGGER.error("cluster address string is required value.");
            }
        } catch (Exception ex) {
            KAFKA_TEMPLATE_IMPL_LOGGER.error("check kafka cluster.", ex);
        }

        if (canIConnectCount == 0) {
            this.kafkaProducer.close();
        }

        return canIConnectCount > 0;
    }

    @Override
    public void connectionClose() {
        this.waitBeforeClosing();
        kafkaProducer.close();
    }

    private static final String KAFKA_BROKER_CLASS_NAME = "org.apache.kafka.clients.producer.KafkaProducer";

    @Override
    public void validateBasicTemplateClass() throws ValidationException {
        try {
            Class.forName(KAFKA_BROKER_CLASS_NAME);
        } catch (ClassNotFoundException cex) {
            throw new ValidationException(cex.toString());
        }
    }

    private void setPerformance() {
        super.performanceType();
        this.setKafkaProperties(ProducerConfig.ACKS_CONFIG, this.performanceType.getType());
    }

    /**
     * Kafka Always send to stream by Async.
     */
    private void setIsAsync() {
        super.isAsync();

        if (CommunicationType.ASYNC == this.communicationType) {
            this.setKafkaProperties(ProducerConfig.BATCH_SIZE_CONFIG, KafkaConstant.VALUE_ASYNC_MAX_MESSAGE_AT_ONCE);
            this.setKafkaProperties(ProducerConfig.LINGER_MS_CONFIG, KafkaConstant.VALUE_LINGER_MS_SIZE);
        }
    }

    private void setIsCompression() {
        super.isCompression();
        this.setKafkaProperties(ProducerConfig.COMPRESSION_TYPE_CONFIG, this.compressionCodecType.getType());
    }
}
