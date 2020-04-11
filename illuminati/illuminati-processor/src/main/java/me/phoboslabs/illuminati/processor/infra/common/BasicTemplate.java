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

package me.phoboslabs.illuminati.processor.infra.common;

import me.phoboslabs.illuminati.processor.exception.ValidationException;
import me.phoboslabs.illuminati.processor.infra.kafka.enums.CommunicationType;
import me.phoboslabs.illuminati.processor.infra.kafka.enums.CompressionCodecType;
import me.phoboslabs.illuminati.processor.infra.kafka.enums.PerformanceType;
import me.phoboslabs.illuminati.processor.properties.IlluminatiPropertiesImpl;
import me.phoboslabs.illuminati.common.util.PropertiesUtil;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 13/07/2017.
 */
public abstract class BasicTemplate {

    private static final Logger BASIC_TEMPLATE_LOGGER = LoggerFactory.getLogger(BasicTemplate.class);

    protected CommunicationType communicationType;
    protected CompressionCodecType compressionCodecType;
    protected PerformanceType performanceType;
    protected String topic;

    protected IlluminatiPropertiesImpl illuminatiProperties;

    protected boolean sending = false;

    abstract protected void checkRequiredValuesForInit ();
    abstract protected void initProperties () throws Exception;

    protected final AtomicInteger sendCount = new AtomicInteger(0);

    protected BasicTemplate (final String propertiesName) {
        this.illuminatiProperties = PropertiesUtil.getIlluminatiProperties(IlluminatiPropertiesImpl.class, propertiesName);

        if (this.illuminatiProperties == null) {
            BASIC_TEMPLATE_LOGGER.error("error : Sorry, something is wrong in read Properties file.");
            throw new ValidationException("error : Sorry, something is wrong in read Properties file.");
        }

        if (!StringObjectUtils.isValid(this.illuminatiProperties.getClusterList())) {
            BASIC_TEMPLATE_LOGGER.error("error : cluster list variable is empty.");
            throw new ValidationException("error : cluster list variable is empty.");
        }
    }

    protected void isAsync () {
        boolean isAsync = false;

        if (StringObjectUtils.isValid(this.illuminatiProperties.getIsAsync()) && "true".equals(this.illuminatiProperties.getIsAsync().toLowerCase())) {
            isAsync = true;
        }

        if (isAsync) {
            this.communicationType = CommunicationType.ASYNC;
        } else {
            this.communicationType = CommunicationType.SYNC;
        }
    }

    protected void isCompression () {
        boolean isComperession = false;

        if (StringObjectUtils.isValid(this.illuminatiProperties.getIsCompression())
                && "true".equalsIgnoreCase(this.illuminatiProperties.getIsCompression())) {
            isComperession = true;
        }

        if (isComperession) {
            this.compressionCodecType = CompressionCodecType.getCompressionCodecType(this.illuminatiProperties.getCompressionType());
        } else {
            this.compressionCodecType = CompressionCodecType.NONE;
        }
    }

    /**
     * This value controls when a produce request is considered completed. Specifically, how many other brokers must
     * have committed the dto to their log and acknowledged this to the leader? Typical values are
     *
     *  0 : which means that the producer never waits for an acknowledgement from the broker.
     *      this option procides the lowest latency but the weakest durabilility guarantees.
     *      some dto will be lost when a spring fails.
     *  1 : which means that the producer gets an acknowledgement after the leader replica has received the dto.
     *      this option provides better durability as the processor waits until the spring acknowledges the request as
     *      successful.
     *  -1 : which means thar the producer gets an acknowledgement after all in-sync replicas have received the dto.
     *       this option provides the best durability, we guarantee that no messages will be lost as long as at least
     *       one in sync replica remains.
     *
     *  default value is 0
     *  it's only using when you choose kafka.
     */
    protected void performanceType () {
        int performance = 0;
        if (StringObjectUtils.isValid(this.illuminatiProperties.getPerformance())) {
            try {
                performance = Integer.parseInt(this.illuminatiProperties.getPerformance());
            } catch (Exception ex) {
                BASIC_TEMPLATE_LOGGER.error("error : casting exception. ({})", ex.toString(), ex);
            }
        }

        switch (performance) {
            case 0 :
                this.performanceType = PerformanceType.FASTEST_BUT_NO_GUARANTEE_DATA;
                break;
            case -1 :
                this.performanceType = PerformanceType.SLOW_BUT_GUARANTEE_DATA;
                break;

            default:
                this.performanceType = PerformanceType.FAST_BUT_SOMETIMES_DISAPPEAR;
                break;
        }
    }

    protected void waitBeforeClosing() {
        int timeoutTryCount = 0;
        while (this.sending && timeoutTryCount < 30) {
            try {
                System.out.println("Waiting for transaction with the Illuminati to end.... ("+timeoutTryCount+" up to 30)");
                timeoutTryCount++;
                Thread.sleep(2000);
            } catch (Exception ignore) {}
        }
    }
}
