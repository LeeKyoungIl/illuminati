package me.phoboslabs.illuminati.client.prossor.infra.rabbitmq.impl;

import me.phoboslabs.illuminati.client.prossor.exception.PublishMessageException;
import me.phoboslabs.illuminati.client.prossor.exception.ValidationException;
import me.phoboslabs.illuminati.client.prossor.infra.IlluminatiInfraTemplate;
import me.phoboslabs.illuminati.client.prossor.infra.common.BasicTemplate;
import me.phoboslabs.illuminati.client.prossor.infra.kafka.enums.CommunicationType;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.nio.NioParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import static me.phoboslabs.illuminati.client.prossor.infra.rabbitmq.constants.RabbitmqConstant.*;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 11/07/2017.
 */
public class RabbitmqInfraTemplateImpl extends BasicTemplate implements IlluminatiInfraTemplate<String> {

    private static final Logger RABBITMQ_TEMPLATE_IMPL_LOGGER = LoggerFactory.getLogger(RabbitmqInfraTemplateImpl.class);

    private String clusterList;
    private String virtualHost;
    private String queueName;

    private String compressionCodec = "gzip; charset=UTF-8";
    private String contentType = "application/json";
    private boolean durable = true;

    private static final ConnectionFactory RABBITMQ_CONNECTION_FACTORY = new ConnectionFactory();
    private static BasicProperties PROPS;
    private static Connection AMQP_CONNECTION;

    public RabbitmqInfraTemplateImpl(final String propertiesName) {
        super(propertiesName);

        this.checkRequiredValuesForInit();

        this.clusterList = this.illuminatiProperties.getClusterList();
        this.virtualHost = this.illuminatiProperties.getVirtualHost();
        this.queueName = this.illuminatiProperties.getQueueName();

        this.initProperties();
        this.setBasicProperties();

        this.createConnection();
    }

    @Override protected void checkRequiredValuesForInit () {
        if (!StringObjectUtils.isValid(this.illuminatiProperties.getVirtualHost())) {
            RABBITMQ_TEMPLATE_IMPL_LOGGER.error("error : virtualHostName is empty.");
            throw new ValidationException("error : virtualHostName is empty.");
        }

        if (!StringObjectUtils.isValid(this.illuminatiProperties.getQueueName())) {
            RABBITMQ_TEMPLATE_IMPL_LOGGER.error("error : queueName is empty.");
            throw new ValidationException("error : queueName is empty.");
        }
    }

    @Override protected void initProperties () {
        this.setConnectUserInfo();

        this.setProps();
        this.isAsync();
        this.isCompression();
        this.setTopicAndQueue();
    }

    @Override public void connectionClose() {
        try {
            if (AMQP_CONNECTION != null && AMQP_CONNECTION.isOpen()) {
                AMQP_CONNECTION.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }

    private void setProps () {
        this.PROPS = new BasicProperties.Builder()
                .contentEncoding(this.compressionCodec)
                .contentType(this.contentType)
                .deliveryMode(2)
                .priority(0)
                .build();
    }

    public void sendToIlluminati (final String entity) {
        final Channel amqpChannel = this.createAmqpChannel();

        try {
            if (amqpChannel != null && AMQP_CONNECTION.isOpen()) {
                amqpChannel.basicPublish(this.topic,"", this.PROPS, entity.getBytes());

                if (this.communicationType == CommunicationType.SYNC) {
                    amqpChannel.waitForConfirms(VALUE_CONNECTION_WAIT_CONFIRM_TIMEOUT_MS);
                }

                if (IlluminatiConstant.ILLUMINATI_DEBUG) {
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info("");
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info("#########################################################################################################");
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info("## rabbitMq send log");
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info("## -------------------------------------------------------------------------------------------------------");
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info("## successfully transferred dto to Illuminati broker.");
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info("#########################################################################################################");
                }
            }
        } catch (Exception ex) {
            RABBITMQ_TEMPLATE_IMPL_LOGGER.info("");
            RABBITMQ_TEMPLATE_IMPL_LOGGER.info("#########################################################################################################");
            RABBITMQ_TEMPLATE_IMPL_LOGGER.info("## rabbitMq send exception log");
            RABBITMQ_TEMPLATE_IMPL_LOGGER.info("## -------------------------------------------------------------------------------------------------------");
            RABBITMQ_TEMPLATE_IMPL_LOGGER.info("## failed to publish message (don't worry about failed. illuminati will retry send again your dto.) : ");
            RABBITMQ_TEMPLATE_IMPL_LOGGER.info("## messages : "+ex.toString());
            RABBITMQ_TEMPLATE_IMPL_LOGGER.info("#########################################################################################################");

            throw new PublishMessageException("failed to publish message : " + ex.toString());
        } finally {
            if (amqpChannel != null) {
                String amqpChannelExceptionMessage = null;
                String amqpChannelExceptionLog = null;
                try {
                    amqpChannel.close();
                } catch (IOException e) {
                    amqpChannelExceptionMessage = "there was a problem close a nio channel (IO).";
                    amqpChannelExceptionLog = e.toString();
                } catch (TimeoutException e) {
                    amqpChannelExceptionMessage = "there was a problem close a nio channel (timeout).";
                    amqpChannelExceptionLog = e.toString();
                }

                if (amqpChannelExceptionMessage != null && amqpChannelExceptionLog != null) {
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info("");
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info("#########################################################################################################");
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info("## amqp channel close exception log");
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info("## -------------------------------------------------------------------------------------------------------");
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.warn("## exception message : " + amqpChannelExceptionMessage);
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.warn("## exception log : " + amqpChannelExceptionLog);
                    RABBITMQ_TEMPLATE_IMPL_LOGGER.info("#########################################################################################################");
                }
            }
        }
    }

    @Override public boolean canIConnect() {
        if (AMQP_CONNECTION != null && AMQP_CONNECTION.isOpen()) {
            return true;
        }

        return false;
    }

    private synchronized void createConnection () {
        try {
            final ExecutorService executor = Executors.newFixedThreadPool(3);
            AMQP_CONNECTION = RABBITMQ_CONNECTION_FACTORY.newConnection(executor, this.getClusterList());
        } catch (IOException ex) {
            RABBITMQ_TEMPLATE_IMPL_LOGGER.error("error : cluster host had a problem. " + ex.toString());
            //throw new CommunicationException("error : cluster host had a problem. " + ex.toString());
        } catch (TimeoutException ex) {
            RABBITMQ_TEMPLATE_IMPL_LOGGER.error("error : there was a problem communicating with the spring. " + ex.toString());
            //throw new CommunicationException("error : there was a problem communicating with the spring. " + ex.toString());
        }
    }

    private Channel createAmqpChannel () {
        if (AMQP_CONNECTION != null) {
            try {
                final Channel amqpChannel = AMQP_CONNECTION.createChannel();
                amqpChannel.queueDeclare(this.queueName, true, false, false, null);

                if (this.communicationType == CommunicationType.SYNC) {
                    amqpChannel.confirmSelect();
                }

                return amqpChannel;
            } catch (IOException ex) {
                RABBITMQ_TEMPLATE_IMPL_LOGGER.error("error : create connection channel has failed.. ("+ex.toString()+")");
            }
        }

        return null;
    }

    private List<Address> getClusterList () {
        final List<Address> clusterAddressList = new ArrayList<Address>();
        for (String serverData : Arrays.asList(this.clusterList.split(","))) {
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

    private void setBasicProperties () {
        RABBITMQ_CONNECTION_FACTORY.setShutdownExecutor(Executors.newSingleThreadExecutor());
        RABBITMQ_CONNECTION_FACTORY.setShutdownTimeout(VALUE_SHUTDOWN_TIMEOUT_MS);
        RABBITMQ_CONNECTION_FACTORY.setRequestedHeartbeat(VALUE_REQUESTED_HEART_BEAT);
        RABBITMQ_CONNECTION_FACTORY.setAutomaticRecoveryEnabled(VALUE_AUTOMATIC_RECOVERY);
        RABBITMQ_CONNECTION_FACTORY.setTopologyRecoveryEnabled(VALUE_AUTOMATIC_EXCHANGE_RECOVERY);
        RABBITMQ_CONNECTION_FACTORY.setNetworkRecoveryInterval(VALUE_AUTOMATIC_RECOVERY_NETWORK_DELAY_MS);
        RABBITMQ_CONNECTION_FACTORY.setVirtualHost(this.virtualHost);
        RABBITMQ_CONNECTION_FACTORY.setSocketConfigurator(new DefaultSocketConfigurator() {
            @Override
            public void configure(Socket socket) throws IOException {
                socket.setTcpNoDelay(VALUE_DONT_USE_NAGLE);
                socket.setReceiveBufferSize(VALUE_SET_WRITE_BUFFER_SIZE);
                socket.setSendBufferSize(VALUE_SET_SEND_BUFFER_SIZE);
                socket.setPerformancePreferences(0, 2, 1);
                socket.setReuseAddress(true);
                socket.setKeepAlive(VALUE_TCP_KEELALIVE);
                socket.setSoLinger(true, VALUE_SO_LINGER_TIME);
                socket.setSoTimeout(VALUE_SOCKET_READ_BLOCK_TIMEOUT);
            }
        });

        if (this.communicationType == CommunicationType.ASYNC) {
            final NioParams nioParams = new NioParams()
                    .setNbIoThreads(4)
                    .setWriteEnqueuingTimeoutInMs(0)
                    .setWriteByteBufferSize(VALUE_SET_WRITE_BUFFER_SIZE);

            RABBITMQ_CONNECTION_FACTORY.useNio();
            RABBITMQ_CONNECTION_FACTORY.setNioParams(nioParams);
        } else {
            RABBITMQ_CONNECTION_FACTORY.useBlockingIo();
            RABBITMQ_CONNECTION_FACTORY.setConnectionTimeout(VALUE_CONNECTION_TIMEOUT_MS);
            RABBITMQ_CONNECTION_FACTORY.setChannelRpcTimeout(VALUE_RPC_CALL_TIMEOUT_MS);
            RABBITMQ_CONNECTION_FACTORY.setHandshakeTimeout(VALUE_HANDSHAKE_CONNECTION_TIMEOUT_MS);
        }
    }

    private void setTopicAndQueue () {
        if (StringObjectUtils.isValid(this.illuminatiProperties.getTopic()) && StringObjectUtils
                .isValid(this.illuminatiProperties.getQueueName())) {
            this.topic = this.illuminatiProperties.getTopic();
            this.queueName = this.illuminatiProperties.getQueueName();
        } else {
            RABBITMQ_TEMPLATE_IMPL_LOGGER.error("error : topic or queueName is empty.");
        }
    }

    private void setConnectUserInfo () {
        if (StringObjectUtils.isValid(this.illuminatiProperties.getUserName()) && StringObjectUtils
                .isValid(this.illuminatiProperties.getPassword())) {
            RABBITMQ_CONNECTION_FACTORY.setUsername(this.illuminatiProperties.getUserName());
            RABBITMQ_CONNECTION_FACTORY.setPassword(this.illuminatiProperties.getPassword());
        }
    }
}
