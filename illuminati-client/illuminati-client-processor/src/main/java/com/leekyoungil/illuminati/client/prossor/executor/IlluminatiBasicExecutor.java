package com.leekyoungil.illuminati.client.prossor.executor;

import com.leekyoungil.illuminati.client.prossor.executor.impl.IlluminatiBlockingQueue;
import com.leekyoungil.illuminati.client.prossor.executor.impl.IlluminatiFileBackupExecutorImpl;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.dto.IlluminatiInterfaceModel;
import com.leekyoungil.illuminati.common.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public abstract class IlluminatiBasicExecutor<T extends IlluminatiInterfaceModel> implements IlluminatiExecutor<T> {

    protected final Logger illuminatiExecutorLogger = LoggerFactory.getLogger(this.getClass());

    // ################################################################################################################
    // ### init illuminati template executor                                                                        ###
    // ################################################################################################################
    protected final IlluminatiExecutor<IlluminatiInterfaceModel> illuminatiFileBackupExecutor = IlluminatiFileBackupExecutorImpl.getInstance();

    protected final IlluminatiBlockingQueue<T> illuminatiBlockingQueue;

    private long enQueuingTimeout = 0l;
    private long deQueuingTimeout = 0l;

    public abstract void sendToNextStep(final T t);
    protected abstract void sendToNextStepByDebug(final T t);
    protected abstract void preventErrorOfSystemThread(final T t);

    protected IlluminatiBasicExecutor (int capacity, long enQueuingTimeout, long deQueuingTimeout, IlluminatiBlockingQueue<T> blockingQueue) {
        this.enQueuingTimeout = enQueuingTimeout;
        this.deQueuingTimeout = deQueuingTimeout;

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
                return illuminatiBlockingQueue.poll(this.deQueuingTimeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                illuminatiExecutorLogger.warn("Failed to dequeing the ILLUMINATI_BLOCKING_QUEUE.. ("+e.getMessage()+")");
            }

            return null;
        } else {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // ignore
            }

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
        if (IlluminatiConstant.ILLUMINATI_DEBUG == true) {
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
            T illuminatiInterfaceModel = illuminatiBlockingQueue.poll(this.deQueuingTimeout, TimeUnit.MILLISECONDS);
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
        final Runnable runnableFirst = new Runnable() {
            public void run() {
                while (true) {
                    T illuminatiInterfaceModel = null;
                    try {
                        illuminatiInterfaceModel = deQueue();
                        if (illuminatiInterfaceModel != null) {
                            if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
                                sendToNextStep(illuminatiInterfaceModel);
                            } else {
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    // ignore
                                }
                                sendToNextStepByDebug(illuminatiInterfaceModel);
                            }
                        }
                    } catch (Exception e) {
                        if (illuminatiInterfaceModel != null) {
                            preventErrorOfSystemThread(illuminatiInterfaceModel);
                        }

                        illuminatiExecutorLogger.warn("Failed to send the ILLUMINATI_BLOCKING_QUEUE.. ("+e.getMessage()+")");
                    }
                }
            }
        };

        SystemUtil.createSystemThread(runnableFirst, this.getClass().getName() + " : ILLUMINATI_SENDER_THREAD");

        // if you set debug is true
        this.createDebugThread();
    }

    protected void createDebugThread () {
        // debug illuminati buffer queue
        if (IlluminatiConstant.ILLUMINATI_DEBUG == true) {
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
