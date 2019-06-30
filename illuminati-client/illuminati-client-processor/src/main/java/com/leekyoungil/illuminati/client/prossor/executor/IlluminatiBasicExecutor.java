package com.leekyoungil.illuminati.client.prossor.executor;

import com.leekyoungil.illuminati.client.prossor.infra.backup.shutdown.IlluminatiGracefulShutdownChecker;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.dto.IlluminatiInterfaceModel;
import com.leekyoungil.illuminati.common.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 04/05/2018.
 */
public abstract class IlluminatiBasicExecutor<T extends IlluminatiInterfaceModel> implements IlluminatiExecutor<T> {

    protected final Logger illuminatiExecutorLogger = LoggerFactory.getLogger(this.getClass());

    public static final int ILLUMINATI_BAK_LOG = 10000;

    protected final IlluminatiBlockingQueue<T> illuminatiBlockingQueue;

    private final long enQueuingTimeout;

    public abstract void sendToNextStep(final T t);
    protected abstract void sendToNextStepByDebug(final T t);
    protected abstract void preventErrorOfSystemThread(final T t);

    protected IlluminatiBasicExecutor (long enQueuingTimeout, IlluminatiBlockingQueue<T> blockingQueue) {
        this.enQueuingTimeout = enQueuingTimeout;

        this.illuminatiBlockingQueue = blockingQueue;
    }

    public int getQueueSize () {
        if (illuminatiBlockingQueue == null) {
            illuminatiExecutorLogger.warn("ILLUMINATI_BLOCKING_QUEUE is must not null.");
            return 0;
        }
        return illuminatiBlockingQueue.size();
    }

    public void addToQueue(final T illuminatQueueInterfaceModel) {
        if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
            try {
                illuminatiBlockingQueue.offer(illuminatQueueInterfaceModel, this.enQueuingTimeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                illuminatiExecutorLogger.warn("Failed to enqueuing the ILLUMINATI_BLOCKING_QUEUE.. ("+e.getMessage()+")");
            }
        } else {
            this.addToQueueByDebug(illuminatQueueInterfaceModel);
        }
    }

    public T deQueue() {
        if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
            try {
                return illuminatiBlockingQueue.take();
            } catch (InterruptedException e) {
                illuminatiExecutorLogger.warn("Failed to dequeing the ILLUMINATI_BLOCKING_QUEUE.. ("+e.getMessage()+")");
            }

            return null;
        } else {
            return this.deQueueByDebug();
        }
    }

    // ################################################################################################################
    // ### private methods                                                                                          ###
    // ################################################################################################################

    /**
     * only execute at debug
     */
    protected void addToQueueByDebug (final T illuminatiInterfaceModel) {
        // debug illuminati buffer queue
        if (IlluminatiConstant.ILLUMINATI_DEBUG) {
            try {
                illuminatiExecutorLogger.info("ILLUMINATI_BLOCKING_QUEUE current size is "+String.valueOf(this.getQueueSize()));
                final long start = System.currentTimeMillis();
                illuminatiBlockingQueue.offer(illuminatiInterfaceModel, this.enQueuingTimeout, TimeUnit.MILLISECONDS);
                final long elapsedTime = System.currentTimeMillis() - start;
                illuminatiExecutorLogger.info("ILLUMINATI_BLOCKING_QUEUE after inserted size is "+String.valueOf(this.getQueueSize()));
                illuminatiExecutorLogger.info("elapsed time of enqueueing ILLUMINATI_BLOCKING_QUEUE is "+elapsedTime+" millisecond");
            } catch (InterruptedException e) {
                illuminatiExecutorLogger.error("Failed to enqueuing the ILLUMINATI_BLOCKING_QUEUE.. ("+e.getMessage()+")");
            }
        }
    }
    protected T deQueueByDebug () {
        illuminatiExecutorLogger.info("ILLUMINATI_BLOCKING_QUEUE current size is "+String.valueOf(this.getQueueSize()));

        if (illuminatiBlockingQueue == null || this.getQueueSize() == 0) {
            return null;
        }
        try {
            final long start = System.currentTimeMillis();
            T illuminatiInterfaceModel = illuminatiBlockingQueue.take();
            final long elapsedTime = System.currentTimeMillis() - start;
            illuminatiExecutorLogger.info("ILLUMINATI_BLOCKING_QUEUE after inserted size is "+String.valueOf(this.getQueueSize()));
            illuminatiExecutorLogger.info("elapsed time of dequeueing ILLUMINATI_BLOCKING_QUEUE is "+elapsedTime+" millisecond");
            return illuminatiInterfaceModel;
        } catch (InterruptedException e) {
            illuminatiExecutorLogger.warn("Failed to dequeing the ILLUMINATI_BLOCKING_QUEUE.. ("+e.getMessage()+")");
        }

        return null;
    }

    protected void createSystemThread () {
        final String thisClassName = this.getClass().getName();
        final Runnable runnableFirst = new Runnable() {
            public void run() {
                while (true) {
                    T illuminatiInterfaceModel = null;
                    try {
                        illuminatiInterfaceModel = deQueue();
                        if (illuminatiInterfaceModel != null) {
                            if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
                                if (IlluminatiGracefulShutdownChecker.getIlluminatiReadyToShutdown() == false) {
                                    sendToNextStep(illuminatiInterfaceModel);
                                } else {
                                    preventErrorOfSystemThread(illuminatiInterfaceModel);
                                }
                            } else {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    // ignore
                                }
                                sendToNextStepByDebug(illuminatiInterfaceModel);
                            }
                        }
                    } catch (Exception e) {
                        if (illuminatiInterfaceModel != null && IlluminatiGracefulShutdownChecker.getIlluminatiReadyToShutdown() == false) {
                            preventErrorOfSystemThread(illuminatiInterfaceModel);
                        }

                        StringBuilder errorMessage = new StringBuilder();
                        errorMessage.append("Failed to send the ILLUMINATI_BLOCKING_QUEUE.. ");

                        if (thisClassName.contains("IlluminatiTemplateExecutorImpl")) {
                            errorMessage.append("But Your data has already been safely stored. ");
                            errorMessage.append("It will be restored. When broker is restored. ");
                        }

                        illuminatiExecutorLogger.debug(errorMessage.toString() + " ("+e.getMessage()+")");
                    }
                }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, thisClassName + " : ILLUMINATI_SENDER_THREAD");

        // if you set debug is true
        this.createDebugThread();
    }

    protected void createDebugThread () {
        // debug illuminati buffer queue
        if (IlluminatiConstant.ILLUMINATI_DEBUG) {
            final Runnable queueCheckRunnable = new Runnable() {
                public void run() {
                    while (true) {
                        illuminatiExecutorLogger.info("");
                        illuminatiExecutorLogger.info("#########################################################################################################");
                        illuminatiExecutorLogger.info("## template queue buffer debug info");
                        illuminatiExecutorLogger.info("## -------------------------------------------------------------------------------------------------------");
                        illuminatiExecutorLogger.info("## current template queue count : "+String.valueOf(getQueueSize()));
                        illuminatiExecutorLogger.info("#########################################################################################################");

                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                }
            };

            SystemUtil.createSystemThread(queueCheckRunnable, this.getClass().getName() + " : ILLUMINATI_TEMPLATE_QUEUE_CHECK_THREAD");
        }
    }
}
