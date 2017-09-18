package com.leekyoungil.illuminati.client.prossor.infra.common;

import com.leekyoungil.illuminati.client.prossor.exception.ValidationException;
import com.leekyoungil.illuminati.client.prossor.infra.kafka.enums.CommunicationType;
import com.leekyoungil.illuminati.client.prossor.infra.kafka.enums.CompressionCodecType;
import com.leekyoungil.illuminati.client.prossor.infra.kafka.enums.PerformanceType;
import com.leekyoungil.illuminati.client.prossor.properties.IlluminatiPropertiesHelper;
import com.leekyoungil.illuminati.common.properties.IlluminatiProperties;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 13/07/2017.
 */
public abstract class BasicTemplate {

    private static final Logger BASIC_TEMPLATE_LOGGER = LoggerFactory.getLogger(BasicTemplate.class);

    protected CommunicationType communicationType;
    protected CompressionCodecType compressionCodecType;
    protected PerformanceType performanceType;
    protected String topic;

    protected IlluminatiProperties illuminatiProperties;

    abstract protected void checkRequiredValuesForInit ();
    abstract protected void initProperties ();
    abstract protected void publisherClose ();

    protected BasicTemplate (final String propertiesName) {
        this.illuminatiProperties = IlluminatiPropertiesHelper.getIlluminatiProperties(null, propertiesName);

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

        if (StringObjectUtils.isValid(this.illuminatiProperties.getIsCompression()) && "true".equals(this.illuminatiProperties.getIsCompression().toLowerCase())) {
            isComperession = true;
        }

        if (isComperession) {
            this.compressionCodecType = CompressionCodecType.SNAPPY;
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
     *      some dto will be lost when a server fails.
     *  1 : which means that the producer gets an acknowledgement after the leader replica has received the dto.
     *      this option provides better durability as the processor waits until the server acknowledges the request as
     *      successful.
     *  -1 : which means thar the producer gets an acknowledgement after all in-sync replicas have received the dto.
     *       this option provides the best durability, we guarantee that no messages will be lost as long as at least
     *       one in sync replica remains.
     *
     *  default value is 0
     */
    protected void performanceType () {
        int performance = 0;
        if (StringObjectUtils.isValid(this.illuminatiProperties.getPerformance())) {
            try {
                performance = Integer.parseInt(this.illuminatiProperties.getPerformance());
            } catch (Exception ex) {
                BASIC_TEMPLATE_LOGGER.error("error : casting exception. ("+ex.toString()+")");
            }
        }

        switch (performance) {
            case 0 :
                this.performanceType = PerformanceType.FASTEST_BUT_NO_GUARANTEE_DATA;
                break;

            case 1 :
                this.performanceType = PerformanceType.FAST_BUT_SOMETIMES_DISAPPEAR;
                break;

            case -1 :
                this.performanceType = PerformanceType.SLOW_BUT_GUARANTEE_DATA;
                break;

            default:
                this.performanceType = PerformanceType.FAST_BUT_SOMETIMES_DISAPPEAR;
                break;
        }
    }
}
