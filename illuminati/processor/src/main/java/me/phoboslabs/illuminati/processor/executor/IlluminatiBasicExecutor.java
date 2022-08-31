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

package me.phoboslabs.illuminati.processor.executor;

import java.util.concurrent.TimeUnit;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.IlluminatiInterfaceModel;
import me.phoboslabs.illuminati.common.util.SystemUtil;
import me.phoboslabs.illuminati.processor.exception.IlluminatiQueueException;
import me.phoboslabs.illuminati.processor.shutdown.IlluminatiGracefulShutdownChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 04/05/2018.
 */
public abstract class IlluminatiBasicExecutor<T extends IlluminatiInterfaceModel> implements IlluminatiExecutor<T> {

    protected final static Logger ILLUMINATI_EXECUTOR_LOGGER = LoggerFactory.getLogger(IlluminatiBasicExecutor.class);

    public static final int ILLUMINATI_BAK_LOG = 10000;

    protected final IlluminatiBlockingQueue<T> illuminatiBlockingQueue;

    private final long enQueuingTimeout;

    public abstract void sendToNextStep(T t) throws Exception;

    protected abstract void sendToNextStepByDebug(T t) throws Exception;

    protected abstract void preventErrorOfSystemThread(T t);

    protected IlluminatiBasicExecutor(long enQueuingTimeout, IlluminatiBlockingQueue<T> blockingQueue) {
        this.enQueuingTimeout = enQueuingTimeout;
        this.illuminatiBlockingQueue = blockingQueue;
    }

    public int getQueueSize() {
        if (illuminatiBlockingQueue == null) {
            ILLUMINATI_EXECUTOR_LOGGER.warn("ILLUMINATI_BLOCKING_QUEUE is must not null.");
            return 0;
        }
        return illuminatiBlockingQueue.size();
    }

    public void addToQueue(T illuminatQueueInterfaceModel) {
        if (!IlluminatiConstant.ILLUMINATI_DEBUG) {
            try {
                if (!illuminatiBlockingQueue.offer(illuminatQueueInterfaceModel, this.enQueuingTimeout, TimeUnit.MILLISECONDS)) {
                    throw new IlluminatiQueueException("illuminati queue offer has failed");
                }
            } catch (InterruptedException e) {
                ILLUMINATI_EXECUTOR_LOGGER.warn("Failed to enqueuing the ILLUMINATI_BLOCKING_QUEUE...", e);
                Thread.currentThread().interrupt();
            }
        } else {
            this.addToQueueByDebug(illuminatQueueInterfaceModel);
        }
    }

    public T deQueue() {
        if (!IlluminatiConstant.ILLUMINATI_DEBUG) {
            try {
                return illuminatiBlockingQueue.take();
            } catch (InterruptedException e) {
                final String errorMessage = "Failed to dequeing the ILLUMINATI_BLOCKING_QUEUE... (" + e.getMessage() + ")";
                ILLUMINATI_EXECUTOR_LOGGER.warn(errorMessage);
                Thread.currentThread().interrupt();
                throw new IlluminatiQueueException(errorMessage);
            }
        } else {
            return this.deQueueByDebug();
        }
    }

    // ################################################################################################################
    // ### private methods                                                                                          ###
    // ################################################################################################################

    /**
     * only execute at debug
     *
     * @param illuminatiInterfaceModel - input method parameter model
     */
    protected void addToQueueByDebug(T illuminatiInterfaceModel) {
        // debug illuminati buffer queue
        if (!IlluminatiConstant.ILLUMINATI_DEBUG) {
            return;
        }
        try {
            ILLUMINATI_EXECUTOR_LOGGER.info("ILLUMINATI_BLOCKING_QUEUE current size is {}", this.getQueueSize());
            final long start = System.currentTimeMillis();
            if (illuminatiBlockingQueue.offer(illuminatiInterfaceModel, this.enQueuingTimeout, TimeUnit.MILLISECONDS)) {
                final long elapsedTime = System.currentTimeMillis() - start;
                ILLUMINATI_EXECUTOR_LOGGER.info("ILLUMINATI_BLOCKING_QUEUE after inserted size is {}", this.getQueueSize());
                ILLUMINATI_EXECUTOR_LOGGER.info("elapsed time of enqueueing ILLUMINATI_BLOCKING_QUEUE is {} millisecond",
                        elapsedTime);
            } else {
                throw new IlluminatiQueueException("illuminati queue offer has failed");
            }
        } catch (InterruptedException e) {
            ILLUMINATI_EXECUTOR_LOGGER.error("Failed to enqueuing the ILLUMINATI_BLOCKING_QUEUE... ({})", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    protected T deQueueByDebug() {
        ILLUMINATI_EXECUTOR_LOGGER.info("ILLUMINATI_BLOCKING_QUEUE current size is {}", this.getQueueSize());

        if (illuminatiBlockingQueue == null || this.getQueueSize() == 0) {
            throw new IlluminatiQueueException("ILLUMINATI_BLOCKING_QUEUE is empty");
        }
        try {
            final long start = System.currentTimeMillis();
            T illuminatiInterfaceModel = illuminatiBlockingQueue.take();
            final long elapsedTime = System.currentTimeMillis() - start;
            ILLUMINATI_EXECUTOR_LOGGER.info("ILLUMINATI_BLOCKING_QUEUE after inserted size is {}", this.getQueueSize());
            ILLUMINATI_EXECUTOR_LOGGER.info("elapsed time of dequeueing ILLUMINATI_BLOCKING_QUEUE is {} millisecond",
                elapsedTime);
            return illuminatiInterfaceModel;
        } catch (InterruptedException e) {
            final String errorMessage = "Failed to dequeing the ILLUMINATI_BLOCKING_QUEUE... (" + e.getMessage() + ")";
            ILLUMINATI_EXECUTOR_LOGGER.warn(errorMessage);
            Thread.currentThread().interrupt();
            throw new IlluminatiQueueException(errorMessage);
        }
    }

    protected void createSystemThread() {
        final String thisClassName = this.getClass().getName();
        final Runnable runnableFirst = () -> {
            while (true) {
                T illuminatiInterfaceModel = null;
                try {
                    illuminatiInterfaceModel = deQueue();
                    if (illuminatiInterfaceModel == null) {
                        continue;
                    }
                    if (!IlluminatiConstant.ILLUMINATI_DEBUG) {
                        if (!IlluminatiGracefulShutdownChecker.getIlluminatiReadyToShutdown()) {
                            sendToNextStep(illuminatiInterfaceModel);
                        } else {
                            preventErrorOfSystemThread(illuminatiInterfaceModel);
                        }
                    } else {
                        Thread.sleep(2000);
                        sendToNextStepByDebug(illuminatiInterfaceModel);
                    }
                } catch (Exception e) {
                    if (illuminatiInterfaceModel != null && !IlluminatiGracefulShutdownChecker.getIlluminatiReadyToShutdown()) {
                        preventErrorOfSystemThread(illuminatiInterfaceModel);
                    }

                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append("Failed to send the ILLUMINATI_BLOCKING_QUEUE...");

                    if (thisClassName.contains("IlluminatiTemplateExecutorImpl")) {
                        errorMessage.append("But Your data has already been safely stored.");
                        errorMessage.append("It will be restored. When broker is restored.");
                    }

                    ILLUMINATI_EXECUTOR_LOGGER.debug(errorMessage.toString(), e.getMessage());
                }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, thisClassName + " : ILLUMINATI_SENDER_THREAD");

        // if you set debug is true
        this.createDebugThread();
    }

    protected void createDebugThread() {
        // debug illuminati buffer queue
        if (IlluminatiConstant.ILLUMINATI_DEBUG) {
            final Runnable queueCheckRunnable = () -> {
                while (true) {
                    ILLUMINATI_EXECUTOR_LOGGER.info("");
                    ILLUMINATI_EXECUTOR_LOGGER.info(
                        "#########################################################################################################");
                    ILLUMINATI_EXECUTOR_LOGGER.info("## template queue buffer debug info");
                    ILLUMINATI_EXECUTOR_LOGGER.info(
                        "## -------------------------------------------------------------------------------------------------------");
                    ILLUMINATI_EXECUTOR_LOGGER.info("## current template queue count : {}", getQueueSize());
                    ILLUMINATI_EXECUTOR_LOGGER.info(
                        "#########################################################################################################");

                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException ignore) {
                        ILLUMINATI_EXECUTOR_LOGGER.warn("Interrupted!!", ignore);
                        Thread.currentThread().interrupt();
                    }
                }
            };

            SystemUtil.createSystemThread(queueCheckRunnable,
                this.getClass().getName() + " : ILLUMINATI_TEMPLATE_QUEUE_CHECK_THREAD");
        }
    }
}
