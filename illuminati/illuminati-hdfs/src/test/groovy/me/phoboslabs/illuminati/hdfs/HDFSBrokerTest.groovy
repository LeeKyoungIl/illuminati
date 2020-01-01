package me.phoboslabs.illuminati.hdfs

import me.phoboslabs.illuminati.hdfs.vo.HDFSConnectionInfo
import spock.lang.Specification

class HDFSBrokerTest extends Specification {

    private DataBroker dataBorker;

    def setup() {
        HDFSConnectionInfo hdfsConnectionInfo = new HDFSConnectionInfo("your hdfs ip address", 8020, "hdfs");
        hdfsConnectionInfo.hdfsSecurityAuthentication = "SIMPLE";
        hdfsConnectionInfo.hdfsSecurityAuthorization = "FALSE";
        hdfsConnectionInfo.homeDir = "/";
        hdfsConnectionInfo.rpcTimeout = 6000;

        this.dataBorker = new HDFSDataBroker(hdfsConnectionInfo);
    }

    def "TEST : data write to hadoop"() {
        when:
        String source = "/Users/leekyoungil/Desktop/test.txt";
        String dest = "/tmp/test1.txt";
        boolean result = this.dataBorker.addFile(source, dest, true);

        then:
        result == true;
    }

    def "TEST : data read from hadoop"() {
        setup:
        String source = "/tmp/test1.txt";

        when:
        String result = this.dataBorker.readFile(source);

        then:
        result != null && result.trim().length() > 0
        "test1\ntest2\ntest3\ntest4\ntest5\n".equals(result);
    }

    def "TEST : delete the file in hadoop"() {
        setup:
        String source = "/tmp/test2.txt";

        when:
        boolean deleteResult = this.dataBorker.deleteFile(source, true);
        String getFileData = this.dataBorker.readFile(source);

        then:
        thrown Exception
    }

    def "TEST : make dir in hadoop"() {
        setup:
        String source = "/tmp/testdir";

        when:
        boolean makeDirResult = this.dataBorker.mkdir(source);

        then:
        makeDirResult
    }
}