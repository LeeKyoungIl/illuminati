package infra.backup

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutorType
import com.leekyoungil.illuminati.client.prossor.infra.backup.Backup
import com.leekyoungil.illuminati.client.prossor.infra.backup.impl.H2Backup
import spock.lang.Specification

class BackupTest extends Specification {

    def "save" () {
        setup:
        Backup backup = new H2Backup<String>();
        String testData = "test1";
        List<String> dataList = null;

        when:
        backup.append(IlluminatiExecutorType.DATA_EXECUTOR, testData);
        dataList = backup.getDataList();

        then:
        dataList.size() > 0;
    }
}
