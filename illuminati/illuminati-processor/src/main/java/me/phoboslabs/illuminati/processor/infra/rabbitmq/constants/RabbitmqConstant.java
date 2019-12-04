package me.phoboslabs.illuminati.processor.infra.rabbitmq.constants;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 12/07/2017.
 */
public class RabbitmqConstant {

    public final static int VALUE_NIO_THREAD_COUNT = 20;
    public final static int VALUE_CONNECTION_WAIT_CONFIRM_TIMEOUT_MS = 5000;
    // Timeout for connection establishment: 5s
    public final static int VALUE_CONNECTION_TIMEOUT_MS = 5000;
    public final static int VALUE_RPC_CALL_TIMEOUT_MS = 5000;
    public final static int VALUE_SHUTDOWN_TIMEOUT_MS = 5000;
    public final static int VALUE_HANDSHAKE_CONNECTION_TIMEOUT_MS = 20000;
    public final static int VALUE_REQUESTED_HEART_BEAT = 5;
    // Configure automatic reconnections
    public final static boolean VALUE_AUTOMATIC_RECOVERY = true;
    // Exchanges and so on should be redeclared if necessary
    public final static boolean VALUE_AUTOMATIC_EXCHANGE_RECOVERY = true;
    // Recovery interval: 10s
    public final static int VALUE_AUTOMATIC_RECOVERY_NETWORK_DELAY_MS = 10000;
    public final static int VALUE_SET_WRITE_BUFFER_SIZE = 65536;
    public final static int VALUE_SET_SEND_BUFFER_SIZE = 65536;
    /**
     *  TCP_NODELAY
     * - 1 : (TRUE) DONT USE NAGEL
     * - 2 : (FALSE) USE NAGEL
     */
    public final static boolean VALUE_DONT_USE_NAGLE = true;
    public final static boolean VALUE_TCP_KEELALIVE = false;
}
