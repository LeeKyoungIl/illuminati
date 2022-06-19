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
     * TCP_NODELAY - 1 : (TRUE) DONT USE NAGEL - 2 : (FALSE) USE NAGEL
     */
    public final static boolean VALUE_DONT_USE_NAGLE = true;
    public final static boolean VALUE_TCP_KEELALIVE = false;
}
