package executor.impl

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutor
import com.leekyoungil.illuminati.client.prossor.executor.impl.IlluminatiBackupExecutorImpl
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant

import com.leekyoungil.illuminati.common.util.FileUtil
import spock.lang.Specification

class IlluminatiBackupExecutorImplTest extends Specification {

    def "data add to file backup queue" () {
        setup:
        IlluminatiExecutor<String> illuminatiExecutor = IlluminatiBackupExecutorImpl.getInstance();
        String textData = "test1";

        when:
        illuminatiExecutor.addToQueue(textData);

        then:
        illuminatiExecutor.getQueueSize() == 1;
    }

    def "get data from file backup queue" () {
        setup:
        IlluminatiExecutor<String> illuminatiExecutor = IlluminatiBackupExecutorImpl.getInstance();
        String textData = "test1";

        illuminatiExecutor.addToQueue(textData);

        when:
        String dequeueTextData = illuminatiExecutor.deQueue();

        then:
        illuminatiExecutor.getQueueSize() == 0;
        dequeueTextData != null;
    }

    def "create system thread" () {
        setup:
        IlluminatiExecutor<String> illuminatiExecutor = IlluminatiBackupExecutorImpl.getInstance();
        String threadName = "com.leekyoungil.illuminati.client.prossor.executor.impl.IlluminatiBackupExecutorImpl : ILLUMINATI_SENDER_THREAD";

        when:
        illuminatiExecutor.init();

        then:
        IlluminatiConstant.SYSTEM_THREAD_MAP.containsKey(threadName) == true;
    }

    def "add data to file by thread" () {
        setup:
        IlluminatiExecutor<String> illuminatiExecutor = IlluminatiBackupExecutorImpl.getInstance();
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
