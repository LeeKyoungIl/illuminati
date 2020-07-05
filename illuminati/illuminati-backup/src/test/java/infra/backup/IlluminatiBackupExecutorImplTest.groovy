package infra.backup

import me.phoboslabs.illuminati.common.constant.IlluminatiConstant
import me.phoboslabs.illuminati.common.dto.IlluminatiInterfaceModel
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl
import spock.lang.Specification

class IlluminatiBackupExecutorImplTest extends Specification {

    def "data add to file infra.backup queue" () {
//        setup:
//        IlluminatiInterfaceModel illuminatiInterfaceModel = new IlluminatiTemplateInterfaceModelImpl();
//
//        IlluminatiExecutor<IlluminatiInterfaceModel> illuminatiExecutor = IlluminatiBackupExecutorImpl.getInstance();
//
//        when:
//        illuminatiExecutor.addToQueue(illuminatiInterfaceModel);
//
//        then:
//        illuminatiExecutor.getQueueSize() == 1;
    }

    def "get data from file infra.backup queue" () {
//        setup:
//        IlluminatiInterfaceModel illuminatiInterfaceModel = new IlluminatiTemplateInterfaceModelImpl();
//        IlluminatiExecutor<IlluminatiInterfaceModel> illuminatiExecutor = IlluminatiBackupExecutorImpl.getInstance();
//
//        when:
//        illuminatiExecutor.addToQueue(illuminatiInterfaceModel);
//        IlluminatiInterfaceModel dequeueTextData = illuminatiExecutor.deQueue();
//
//        then:
//        illuminatiExecutor.getQueueSize() == 0;
    }

    def "create system thread" () {
//        setup:
//        IlluminatiExecutor<String> illuminatiExecutor = IlluminatiBackupExecutorImpl.getInstance();
//        String threadName = "IlluminatiBackupExecutorImpl : ILLUMINATI_SAVE_DATA_TO_FILE_THREAD";
//
//        when:
//        illuminatiExecutor.init();
//
//        then:
//        IlluminatiConstant.SYSTEM_THREAD_MAP.containsKey(threadName) == true;
    }
}
