package executor.impl

import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutor
import com.leekyoungil.illuminati.client.prossor.executor.impl.IlluminatiBackupExecutorImpl
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant
import com.leekyoungil.illuminati.common.dto.IlluminatiInterfaceModel
import com.leekyoungil.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl
import com.leekyoungil.illuminati.common.util.FileUtil
import spock.lang.Specification

class IlluminatiBackupExecutorImplTest extends Specification {

    def "data add to file backup queue" () {
        setup:
        IlluminatiInterfaceModel illuminatiInterfaceModel = new IlluminatiTemplateInterfaceModelImpl();

        IlluminatiExecutor<IlluminatiInterfaceModel> illuminatiExecutor = IlluminatiBackupExecutorImpl.getInstance();

        when:
        illuminatiExecutor.addToQueue(illuminatiInterfaceModel);

        then:
        illuminatiExecutor.getQueueSize() == 1;
    }

    def "get data from file backup queue" () {
        setup:
        IlluminatiInterfaceModel illuminatiInterfaceModel = new IlluminatiTemplateInterfaceModelImpl();
        IlluminatiExecutor<IlluminatiInterfaceModel> illuminatiExecutor = IlluminatiBackupExecutorImpl.getInstance();

        when:
        illuminatiExecutor.addToQueue(illuminatiInterfaceModel);
        IlluminatiInterfaceModel dequeueTextData = illuminatiExecutor.deQueue();

        then:
        illuminatiExecutor.getQueueSize() == 0;
    }

    def "create system thread" () {
        setup:
        IlluminatiExecutor<String> illuminatiExecutor = IlluminatiBackupExecutorImpl.getInstance();
        String threadName = "com.leekyoungil.illuminati.client.prossor.executor.impl.IlluminatiBackupExecutorImpl : ILLUMINATI_SAVE_DATA_TO_FILE_THREAD";

        when:
        illuminatiExecutor.init();

        then:
        IlluminatiConstant.SYSTEM_THREAD_MAP.containsKey(threadName) == true;
    }
}
