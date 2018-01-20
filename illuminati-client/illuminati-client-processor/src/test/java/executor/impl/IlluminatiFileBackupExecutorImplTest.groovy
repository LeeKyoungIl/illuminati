package executor.impl

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutor
import com.leekyoungil.illuminati.client.prossor.executor.impl.IlluminatiFileBackupExecutorImpl
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant
import com.leekyoungil.illuminati.common.dto.IlluminatiFileBackupInterfaceModel
import com.leekyoungil.illuminati.common.util.FileUtil
import spock.lang.Specification

class IlluminatiFileBackupExecutorImplTest extends Specification {

    def "data add to file backup queue" () {
        setup:
        IlluminatiExecutor<IlluminatiFileBackupInterfaceModel> illuminatiExecutor = IlluminatiFileBackupExecutorImpl.getInstance();
        IlluminatiFileBackupInterfaceModel illuminatiFileBackupInterfaceModel = new IlluminatiFileBackupInterfaceModel();

        when:
        illuminatiExecutor.addToQueue(illuminatiFileBackupInterfaceModel);

        then:
        illuminatiExecutor.getQueueSize() == 1;
    }

    def "get data from file backup queue" () {
        setup:
        IlluminatiExecutor<IlluminatiFileBackupInterfaceModel> illuminatiExecutor = IlluminatiFileBackupExecutorImpl.getInstance();
        IlluminatiFileBackupInterfaceModel illuminatiFileBackupInterfaceModel = new IlluminatiFileBackupInterfaceModel();

        illuminatiExecutor.addToQueue(illuminatiFileBackupInterfaceModel);

        when:
        IlluminatiFileBackupInterfaceModel illuminatiFileBackupInterfaceModel1 = illuminatiExecutor.deQueue();

        then:
        illuminatiExecutor.getQueueSize() == 0;
        illuminatiFileBackupInterfaceModel1 != null;
    }

    def "create system thread" () {
        setup:
        IlluminatiExecutor<IlluminatiFileBackupInterfaceModel> illuminatiExecutor = IlluminatiFileBackupExecutorImpl.getInstance();
        String threadName = "com.leekyoungil.illuminati.client.prossor.executor.impl.IlluminatiFileBackupExecutorImpl : ILLUMINATI_SENDER_THREAD";

        when:
        illuminatiExecutor.init();

        then:
        IlluminatiConstant.SYSTEM_THREAD_MAP.containsKey(threadName) == true;
    }

    def "add data to file by thread" () {
        setup:
        IlluminatiExecutor<String> illuminatiExecutor = IlluminatiFileBackupExecutorImpl.getInstance();
        String textData = "text file test\r\ntext file test1";

        String basePath = "./log";

        when:
        illuminatiExecutor.addToQueue(textData);
        String data = illuminatiExecutor.deQueue();
        illuminatiExecutor.sendToNextStep(data);

        then:
        FileUtil.isFileExists(basePath, FileUtil.generateFileName()) == true;
    }


}
