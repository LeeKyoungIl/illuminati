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

package me.phoboslabs.illuminati.processor.infra.rabbitmq.impl;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultSocketConfigurator;
import com.rabbitmq.client.impl.nio.NioParams;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import me.phoboslabs.illuminati.processor.exception.CommunicationException;
import me.phoboslabs.illuminati.processor.exception.PublishMessageException;
import me.phoboslabs.illuminati.processor.exception.ValidationException;
import me.phoboslabs.illuminati.processor.infra.IlluminatiInfraTemplate;
import me.phoboslabs.illuminati.processor.infra.common.BasicTemplate;
import me.phoboslabs.illuminati.processor.infra.kafka.enums.CommunicationType;
import me.phoboslabs.illuminati.processor.infra.rabbitmq.constants.RabbitmqConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 11/07/2017.
 */
public class RabbitmqInfraTemplateImpl extends BasicTemplate implements IlluminatiInfraTemplate<String> {

    private static final Logger RABBITMQ_TEMPLATE_IMPL_LOGGER = LoggerFactory.getLogger(RabbitmqInfraTemplateImpl.class);

    private String compressionCodec = "gzip; charset=UTF-8";
    private String contentType = "application/json";

    private BasicProperties props;
    private Connection amqpConnection;
    private Channel amqpChannel;

    public RabbitmqInfraTemplateImpl(String propertiesName) throws Exception {
        super(propertiesName);

        this.checkRequiredValuesForInit();

        this.initProperties();
        this.createConnection(this.setBasicProperties());
    }

    @Override
    protected void checkRequiredValuesForInit() {
        this.validateBasicTemplateClass();

        if (!StringObjectUtils.isValid(this.illuminatiProperties.getVirtualHost())) {
            RABBITMQ_TEMPLATE_IMPL_LOGGER.error("error : virtualHostName is empty.");
            throw new ValidationException("error : virtualHostName is empty.");
        }

        if (!StringObjectUtils.isValid(this.illuminatiProperties.getQueueName())) {
            RABBITMQ_TEMPLATE_IMPL_LOGGER.error("error : queueName is empty.");
            throw new ValidationException("error : queueName is empty.");
        }
    }

    @Override
    protected void initProperties() throws Exception {
        this.setProps();
        this.isAsync();
        this.isCompression();
        this.setTopic();
    }

    @Override
    public void connectionClose() {
        this.waitBeforeClosing();

        try {
            if (this.amqpChannel != null && this.amqpChannel.isOpen()) {
                this.amqpChannel.close();
            }
        } catch (Exception ignore) {
        }

        try {
            if (this.amqpConnection != null && this.amqpConnection.isOpen()) {
                this.amqpConnection.close();
            }
        } catch (IOException ignore) {
        }
    }

    private static final String RABBIT_BROKER_CLASS_NAME = "com.rabbitmq.client.ConnectionFactory";

    @Override
    public void validateBasicTemplateClass() throws ValidationException {
        try {
            Class.forName(RABBIT_BROKER_CLASS_NAME);
        } catch (ClassNotFoundException cex) {
            throw new ValidationException(cex.toString());
        }
    }

    private void setProps() {
        this.props = new BasicProperties.Builder()
            .contentEncoding(this.compressionCodec)
            .contentType(this.contentType)
            .deliveryMode(2)
            .priority(0)
            .build();
    }

    @Override
    public void sendToIlluminati(String entity) throws Exception, PublishMessageException {
        try {
            if (this.amqpConnection.isOpen() && this.amqpChannel.isOpen()) {
                this.sending = true;
                this.amqpChannel.basicPublish(this.topic, "", this.props, entity.getBytes());

                if (this.communicationType == CommunicationType.SYNC) {
                    this.amqpChannel.waitForConfirms(RabbitmqConstant.VALUE_CONNECTION_WAIT_CONFIRM_TIMEOUT_MS);
                }

                if (IlluminatiConstant.ILLUMINATI_DEBUG) {
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info("");
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info(
                        "#########################################################################################################");
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info("## rabbitMq send log");
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info(
                        "## -------------------------------------------------------------------------------------------------------");
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info("## successfully transferred dto to Illuminati broker.");
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info(
                        "#########################################################################################################");
                }

                RABBITMQ_TEMPLATE_IMPL_LOGGER.info("successfully transferred dto to Illuminati broker(rabbitMq).");
            }
        } catch (Exception ex) {
            RABBITMQ_TEMPLATE_IMPL_LOGGER.info("");
            RABBITMQ_TEMPLATE_IMPL_LOGGER.info(
                "#########################################################################################################");
            RABBITMQ_TEMPLATE_IMPL_LOGGER.info("## rabbitMq send exception log");
            RABBITMQ_TEMPLATE_IMPL_LOGGER.info(
                "## -------------------------------------------------------------------------------------------------------");
            RABBITMQ_TEMPLATE_IMPL_LOGGER.info(
                "## failed to publish message (don't worry about failed. illuminati will retry send again your dto.) : ");
            RABBITMQ_TEMPLATE_IMPL_LOGGER.info("## messages : " + ex.toString());
            RABBITMQ_TEMPLATE_IMPL_LOGGER.info(
                "#########################################################################################################");

            throw new PublishMessageException("failed to publish message : " + ex.toString());
        } finally {
            this.sending = false;
        }
    }

    @Override
    public boolean canIConnect() {
        boolean canIConnect = this.amqpChannel != null && this.amqpChannel.isOpen()
            && this.amqpConnection != null && this.amqpConnection.isOpen();
        if (!canIConnect) {
            this.connectionClose();
        }
        return canIConnect;
    }

    private synchronized void createConnection(ConnectionFactory rabbitMQConnectionFactory) {
        try {
            this.setConnectUserInfo(rabbitMQConnectionFactory);
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            this.amqpConnection = rabbitMQConnectionFactory.newConnection(executor, this.getClusterList());
        } catch (IOException ex) {
            final String errorMessage = "error : cluster host had a problem. " + ex.toString();
            RABBITMQ_TEMPLATE_IMPL_LOGGER.error(errorMessage, ex);
            throw new CommunicationException(errorMessage);
        } catch (TimeoutException ex) {
            final String errorMessage = "error : there was a problem communicating with the spring. " + ex.toString();
            RABBITMQ_TEMPLATE_IMPL_LOGGER.error(errorMessage, ex);
            throw new CommunicationException(errorMessage);
        }

        try {
            this.amqpChannel = this.createAmqpChannel();
        } catch (Exception ex) {
            final String errorMessage = "error : amqp channel create had a problem. " + ex.getMessage();
            RABBITMQ_TEMPLATE_IMPL_LOGGER.error(errorMessage, ex);
            this.connectionClose();
            throw new CommunicationException(errorMessage);
        }
    }

    private Channel createAmqpChannel() throws Exception {
        if (this.amqpConnection == null) {
            throw new Exception("AMQP_CONNECTION must not be null.");
        }
        try {
            final Channel amqpChannel = this.amqpConnection.createChannel();
            amqpChannel.queueDeclare(this.illuminatiProperties.getQueueName(), true, false, false, null);

            if (this.communicationType == CommunicationType.SYNC) {
                amqpChannel.confirmSelect();
            }
            return amqpChannel;
        } catch (IOException ex) {
            final String errorMessage = "error : create connection channel has failed.. (" + ex.toString() + ")";
            RABBITMQ_TEMPLATE_IMPL_LOGGER.error(errorMessage, ex);
            throw new Exception(errorMessage);
        }
    }

    private List<Address> getClusterList() {
        final String clusterList = this.illuminatiProperties.getClusterList();
        if (!StringObjectUtils.isValid(clusterList)) {
            RABBITMQ_TEMPLATE_IMPL_LOGGER.error("error : cluster list is empty.");
            throw new ValidationException("error : cluster list is empty.");
        }

        final List<Address> clusterAddressList = new ArrayList<>();
        for (String serverData : Arrays.asList(clusterList.split(","))) {
            Address address;
            if (serverData.indexOf(":") > -1) {
                final String[] tmpServerData = serverData.split(":");
                address = new Address(tmpServerData[0], Integer.parseInt(tmpServerData[1]));
            } else {
                address = new Address(serverData);
            }

            clusterAddressList.add(address);
        }

        // it is not be able to over here, but ...
        if (clusterAddressList.size() == 0) {
            RABBITMQ_TEMPLATE_IMPL_LOGGER.error("error : cluster list is empty.");
            throw new ValidationException("error : cluster list is empty.");
        }

        return clusterAddressList;
    }

    private ConnectionFactory setBasicProperties() {
        ConnectionFactory rabbitMQConnectionFactory = new ConnectionFactory();

        final NioParams nioParams = new NioParams()
            .setNbIoThreads(1)
            .setWriteEnqueuingTimeoutInMs(0)
            .setWriteByteBufferSize(RabbitmqConstant.VALUE_SET_WRITE_BUFFER_SIZE);

        rabbitMQConnectionFactory.useNio();
        rabbitMQConnectionFactory.setNioParams(nioParams);
        rabbitMQConnectionFactory.setConnectionTimeout(RabbitmqConstant.VALUE_CONNECTION_TIMEOUT_MS);
        rabbitMQConnectionFactory.setChannelRpcTimeout(RabbitmqConstant.VALUE_RPC_CALL_TIMEOUT_MS);
        rabbitMQConnectionFactory.setHandshakeTimeout(RabbitmqConstant.VALUE_HANDSHAKE_CONNECTION_TIMEOUT_MS);

        ExecutorService shutdownExecutor = Executors.newSingleThreadExecutor();
        rabbitMQConnectionFactory.setShutdownExecutor(shutdownExecutor);
        rabbitMQConnectionFactory.setShutdownTimeout(RabbitmqConstant.VALUE_SHUTDOWN_TIMEOUT_MS);
        rabbitMQConnectionFactory.setRequestedHeartbeat(RabbitmqConstant.VALUE_REQUESTED_HEART_BEAT);
        rabbitMQConnectionFactory.setAutomaticRecoveryEnabled(RabbitmqConstant.VALUE_AUTOMATIC_RECOVERY);
        rabbitMQConnectionFactory.setTopologyRecoveryEnabled(RabbitmqConstant.VALUE_AUTOMATIC_EXCHANGE_RECOVERY);
        rabbitMQConnectionFactory.setNetworkRecoveryInterval(RabbitmqConstant.VALUE_AUTOMATIC_RECOVERY_NETWORK_DELAY_MS);
        rabbitMQConnectionFactory.setVirtualHost(this.illuminatiProperties.getVirtualHost());
        rabbitMQConnectionFactory.setSocketConfigurator(new DefaultSocketConfigurator() {
            @Override
            public void configure(Socket socket) throws IOException {
                socket.setTcpNoDelay(RabbitmqConstant.VALUE_DONT_USE_NAGLE);
                socket.setReceiveBufferSize(RabbitmqConstant.VALUE_SET_WRITE_BUFFER_SIZE);
                socket.setSendBufferSize(RabbitmqConstant.VALUE_SET_SEND_BUFFER_SIZE);
                socket.setPerformancePreferences(0, 2, 1);
                socket.setReuseAddress(true);
                socket.setKeepAlive(RabbitmqConstant.VALUE_TCP_KEELALIVE);
                socket.setSoLinger(true, 1000);
            }
        });

        return rabbitMQConnectionFactory;
    }

    private void setTopic() throws Exception {
        if (StringObjectUtils.isValid(this.illuminatiProperties.getTopic())) {
            this.topic = this.illuminatiProperties.getTopic();
        } else {
            final String errorMessage = "\"error : topic is empty.\"";
            RABBITMQ_TEMPLATE_IMPL_LOGGER.error(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    private void setConnectUserInfo(ConnectionFactory rabbitMQConnectionFactory) {
        if (StringObjectUtils.isValid(this.illuminatiProperties.getUserName()) && StringObjectUtils
            .isValid(this.illuminatiProperties.getPassword())) {
            rabbitMQConnectionFactory.setUsername(this.illuminatiProperties.getUserName());
            rabbitMQConnectionFactory.setPassword(this.illuminatiProperties.getPassword());
        }
    }
}
