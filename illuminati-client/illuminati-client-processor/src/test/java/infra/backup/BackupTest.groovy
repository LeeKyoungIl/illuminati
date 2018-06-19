package infra.backup

import com.leekyoungil.illuminati.client.prossor.infra.backup.Backup
import com.leekyoungil.illuminati.client.prossor.infra.backup.impl.H2Backup
import com.leekyoungil.illuminati.common.dto.enums.IlluminatiInterfaceType
import com.leekyoungil.illuminati.client.prossor.infra.backup.configuration.H2ConnectionFactory
import spock.lang.Specification

class BackupTest extends Specification {

    def "H2 configuration" () {
        setup:
        H2ConnectionFactory h2ConnectionFactory = H2ConnectionFactory.getInstance();

        when:
        boolean isConnectedValid = h2ConnectionFactory.isConnected();

        then:
        isConnectedValid == Boolean.TRUE;
    }

    def "add data" () {
        setup:
        H2Backup<String> h2Backup = new H2Backup<>();
        String stringData = "test data";

        when:
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData);
        List<String> h2DataList = h2Backup.getDataByList(Boolean.FALSE, Boolean.FALSE, 0, 10);

        then:
        h2DataList.size() > 0;
    }

    def "get data by length" () {
        setup:
        H2Backup<String> h2Backup = new H2Backup<>();
        String stringData = "test data";

        when:
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 1");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 2");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 3");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 4");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 5");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 6");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 7");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 8");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 9");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 10");

        List<String> h2DataList = h2Backup.getDataByList(Boolean.TRUE, Boolean.FALSE, 0, 5);

        then:
        h2DataList.size() == 5;
    }

    def "delete check with map" () {
        setup:
        H2Backup<String> h2Backup = new H2Backup<>();
        String stringData = "test data";

        when:
        int beforeCount = h2Backup.getCount();
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 1");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 2");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 3");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 4");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 5");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 6");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 7");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 8");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 9");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 10");

        Map<Integer, String> h2DataMap = h2Backup.getDataByMap(Boolean.TRUE,Boolean.TRUE, 0, 10);
        int afterCount = h2Backup.getCount();

        then:
        beforeCount == afterCount;
    }

    def "delete with list" () {
        setup:
        H2Backup<String> h2Backup = new H2Backup<>();
        String stringData = "test data";

        when:
        int beforeCount = h2Backup.getCount();
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 1");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 2");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 3");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 4");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 5");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 6");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 7");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 8");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 9");
        h2Backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, stringData + " 10");

        List<String> h2DataList = h2Backup.getDataByList(Boolean.TRUE, Boolean.TRUE, 0, 10);
        int afterCount = h2Backup.getCount();

        then:
        beforeCount == afterCount;
    }

    def "save" () {
        setup:
        Backup backup = new H2Backup<String>();
        String testData = "test1";
        List<String> dataList = null;

        when:
        backup.append(IlluminatiInterfaceType.DATA_EXECUTOR, testData);
        dataList = backup.getDataByList(Boolean.FALSE, Boolean.FALSE, 0, 0);

        then:
        dataList.size() > 0;
    }
}
