package me.phoboslabs.illuminati.hdfs.vo;

import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import me.phoboslabs.illuminati.hdfs.enums.HDFSSecurityAuthentication;
import me.phoboslabs.illuminati.hdfs.enums.HDFSSecurityAuthorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HDFSConnectionInfo {

    private final static Logger HDFS_LOGGER = LoggerFactory.getLogger(HDFSConnectionInfo.class);

    private final String uriAddress;
    private final int port;
    private final String hdfsUser;
    private String homeDir = "/";
    private HDFSSecurityAuthentication hdfsSecurityAuthentication = HDFSSecurityAuthentication.SIMPLE;
    private HDFSSecurityAuthorization hdfsSecurityAuthorization = HDFSSecurityAuthorization.FALSE;
    private int rpcTimeout = 10000;

    private final static String HADOOP_PREFIX = "hdfs://";
    private final static String HADOOP_CENTERFIX_WITH_PORT = ":";
    private final static String HADOOP_POSTFIX = "/";

    public HDFSConnectionInfo(final String uriAddress, final int port, final String hdfsUser) {
        this.uriAddress = uriAddress;
        this.port = port;
        this.hdfsUser = hdfsUser;
    }

    public boolean isValid () throws Exception {
        if (StringObjectUtils.isNotValid(this.uriAddress)) {
            final String errorMessage = "uriAddress is required value.";
            HDFS_LOGGER.error(errorMessage);
            throw new Exception(errorMessage);
        }
        if (this.port == 0) {
            final String errorMessage = "port is required value.";
            HDFS_LOGGER.error(errorMessage);
            throw new Exception(errorMessage);
        }
        if (StringObjectUtils.isNotValid(this.hdfsUser)) {
            final String errorMessage = "HDFS User is required value.";
            HDFS_LOGGER.error(errorMessage);
            throw new Exception(errorMessage);
        }
        if (StringObjectUtils.isNotValid(this.homeDir)) {
            final String errorMessage = "homeDir is required value.";
            HDFS_LOGGER.error(errorMessage);
            throw new Exception(errorMessage);
        }
        if (this.hdfsSecurityAuthentication == null) {
            final String errorMessage = "hadoopSecurityAuthentication is required value.";
            HDFS_LOGGER.error(errorMessage);
            throw new Exception(errorMessage);
        }
        if (this.hdfsSecurityAuthorization == null) {
            final String errorMessage = "hadoopSecurityAuthorization is required value.";
            HDFS_LOGGER.error(errorMessage);
            throw new Exception(errorMessage);
        }
        return true;
    }

    public void setHomeDir(final String homeDir) {
        this.homeDir = homeDir;
    }

    public void setRpcTimeout(final int rpcTimeout) {
        this.rpcTimeout = rpcTimeout;
    }

    public String getHDFSUser() {
        return this.hdfsUser;
    }

    public String getHomeDir() {
        return this.homeDir;
    }

    public String getHdfsUriAddress () {
        return new StringBuilder(HADOOP_PREFIX)
                .append(this.uriAddress)
                .append(this.HADOOP_CENTERFIX_WITH_PORT)
                .append(this.port)
                .append(this.HADOOP_POSTFIX).toString();
    }

    public void setHDFSSecurityAuthentication(String hdfsSecurityAuthentication) {
        this.hdfsSecurityAuthentication = HDFSSecurityAuthentication.valueOf(hdfsSecurityAuthentication);
    }
    public String getHDFSSecurityAuthenticationType() {
        return this.hdfsSecurityAuthentication.getAuthType();
    }

    public void setHDFSSecurityAuthorization(String hdfsSecurityAuthorization) {
        this.hdfsSecurityAuthorization = HDFSSecurityAuthorization.valueOf(hdfsSecurityAuthorization);
    }
    public String getHDFSSecurityAuthorizationValue() {
        return this.hdfsSecurityAuthorization.getIsAuthorization();
    }

    public String getRpcTimeout () {
        return String.valueOf(this.rpcTimeout);
    }
}
