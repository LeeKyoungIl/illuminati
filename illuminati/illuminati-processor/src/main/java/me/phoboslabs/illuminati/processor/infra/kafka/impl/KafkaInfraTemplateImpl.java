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

import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import me.phoboslabs.illuminati.processor.exception.PublishMessageException;
import me.phoboslabs.illuminati.processor.exception.ValidationException;
import me.phoboslabs.illuminati.processor.infra.IlluminatiInfraTemplate;
import me.phoboslabs.illuminati.processor.infra.common.BasicTemplate;
import me.phoboslabs.illuminati.processor.infra.kafka.constants.KafkaConstant;
import me.phoboslabs.illuminati.processor.infra.kafka.enums.CommunicationType;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 06/07/2017.
 */
public class KafkaInfraTemplateImpl extends BasicTemplate implements IlluminatiInfraTemplate<String> {

    private static final Logger KAFKA_TEMPLATE_IMPL_LOGGER = LoggerFactory.getLogger(KafkaInfraTemplateImpl.class);

    /**
     * This is for bootstrapping and the producer will only use it for getting metadata.
     * the socket connections for sending the actual dto will be established based on the broker informaion returned
     * is the metadata.
     *
     * The format is host1:port1,host2:port2,host3:port3 and the list can be a subset of brokers or a VIP pointing to
     * a subset of brokers.
     */
    private String brokerList;

    private String topic;

    private static final Properties PROPERTIES = new Properties();

    private static Producer<String,  byte[]> KAFKA_PUBLISHER;

    public KafkaInfraTemplateImpl(final String propertiesName) {
        super(propertiesName);

        this.validateBasicTemplateClass();

        this.checkRequiredValuesForInit();

        this.topic = this.illuminatiProperties.getTopic();
        this.brokerList = this.illuminatiProperties.getClusterList();

        this.setBasicProperties();
        this.initProperties();
        this.initPublisher();
    }

    @Override protected void checkRequiredValuesForInit () {
         if (!StringObjectUtils.isValid(this.illuminatiProperties.getTopic())) {
             KAFKA_TEMPLATE_IMPL_LOGGER.error("error : topic variable is empty.");
             throw new ValidationException("error : topic variable is empty.");
        }
    }

    private void setBasicProperties () {
        this.setKafkaProperties(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.brokerList);
        this.setKafkaProperties(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaConstant.VALUE_SERIALIZER_TYPE_BYTE);
        this.setKafkaProperties(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaConstant.VALUE_SERIALIZER_TYPE_BYTE);
        this.setKafkaProperties(ProducerConfig.PARTITIONER_CLASS_CONFIG, KafkaConstant.VALUE_PARTITIONER);
        this.setKafkaProperties(ProducerConfig.RETRIES_CONFIG, KafkaConstant.VALUE_RETRIES_CONFIG);
        this.setKafkaProperties(ProducerConfig.METADATA_MAX_AGE_CONFIG, KafkaConstant.VALUE_METADATA_MAX_AGE_CONFIG);
        this.setKafkaProperties(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, KafkaConstant.VALUE_MAX_REQUEST_SIZE_CONFIG);
        this.setKafkaProperties(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, KafkaConstant.VALUE_RECONNECT_BACKOFF_MS_CONFIG);
        this.setKafkaProperties(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, KafkaConstant.VALUE_MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION);
        this.setKafkaProperties(ProducerConfig.BUFFER_MEMORY_CONFIG, KafkaConstant.VALUE_BUFFER_MEMORY_CONFIG);
        this.setKafkaProperties(ProducerConfig.SEND_BUFFER_CONFIG, KafkaConstant.VALUE_SEND_BUFFER_CONFIG);
        this.setKafkaProperties(ProducerConfig.RECEIVE_BUFFER_CONFIG, KafkaConstant.VALUE_RECEIVE_BUFFER_CONFIG);
        this.setKafkaProperties(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, KafkaConstant.VALUE_TRANSACTION_TIMEOUT_MS_CONFIG);
        this.setKafkaProperties(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, KafkaConstant.VALUE_REQUEST_TIMEOUT_MS_CONFIG);
    }

    private synchronized void initPublisher () {
        if (this.KAFKA_PUBLISHER == null) {
            this.KAFKA_PUBLISHER = new KafkaProducer<>(this.PROPERTIES);
        }
    }

    private void setKafkaProperties (String key, Object value) {
        this.PROPERTIES.put(key, value);
    }

    @Override protected void initProperties () {
        this.setPerformance();
        this.setIsAsync();
        this.setIsCompression();
    }

    @Override
    public void sendToIlluminati (String entity) throws Exception, PublishMessageException {
        if (this.KAFKA_PUBLISHER == null) {
            KAFKA_TEMPLATE_IMPL_LOGGER.error("kafka publisher not initialized.");
            throw new PublishMessageException("kafka publisher not initialized.");
        }

        try {
            this.sending = true;
            final Future<RecordMetadata> sendResult = this.KAFKA_PUBLISHER.send(new ProducerRecord<>(this.topic, entity.getBytes()));

            KAFKA_TEMPLATE_IMPL_LOGGER.debug("Message produced, offset: " + sendResult.get().offset());
            KAFKA_TEMPLATE_IMPL_LOGGER.debug("Message produced, partition : " + sendResult.get().partition());
            KAFKA_TEMPLATE_IMPL_LOGGER.debug("Message produced, topic: " + sendResult.get().topic());

            KAFKA_TEMPLATE_IMPL_LOGGER.info("successfully transferred dto to Illuminati broker.");
        } catch (Exception ex) {
            throw new PublishMessageException("failed to publish message : ("+ex.getMessage()+")");
        } finally {
            this.sending = false;
        }
    }

    @Override public boolean canIConnect() {
        return this.KAFKA_PUBLISHER != null;
    }

    @Override
    public void connectionClose() {
        this.waitBeforeClosing();
        KAFKA_PUBLISHER.close();
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

    private void setPerformance () {
        super.performanceType();
        this.setKafkaProperties(ProducerConfig.ACKS_CONFIG, this.performanceType.getType());
    }

    /**
     * Kafka Always send to stream by Async.
     */
    private void setIsAsync () {
        super.isAsync();

        if (CommunicationType.ASYNC == this.communicationType) {
            this.setKafkaProperties(ProducerConfig.BATCH_SIZE_CONFIG, KafkaConstant.VALUE_ASYNC_MAX_MESSAGE_AT_ONCE);
            this.setKafkaProperties(ProducerConfig.LINGER_MS_CONFIG, KafkaConstant.VALUE_LINGER_MS_SIZE);
        }
    }

    private void setIsCompression () {
        super.isCompression();
        this.setKafkaProperties(ProducerConfig.COMPRESSION_TYPE_CONFIG, this.compressionCodecType.getType());
    }
}
