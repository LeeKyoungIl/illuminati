package executor.impl

import me.phoboslabs.illuminati.processor.executor.IlluminatiExecutor
import me.phoboslabs.illuminati.processor.executor.impl.IlluminatiDataSendExecutorImpl
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiDataSendModel
import org.aspectj.lang.reflect.MethodSignature
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class IlluminatiDataSendExecutorImplTest extends Specification {

    def "separate make illuminati model from main logic" () {
        setup:
        final HttpServletRequest request = Mock(HttpServletRequest.class);
        final MethodSignature signature = Mock(MethodSignature.class);
        final Object[] args = null;
        long elapsedTime = 3l;
        final Object output = "test";

        IlluminatiDataSendModel illuminatiDataInterfaceModel = new IlluminatiDataSendModel(request, signature, args, elapsedTime, output);

        when:
        IlluminatiExecutor<IlluminatiDataSendModel> illuminatiExecutor = new IlluminatiDataSendExecutorImpl()
        illuminatiExecutor.addToQueue(illuminatiDataInterfaceModel);

        then:
        illuminatiExecutor.getQueueSize() == 1;
    }

    def "process illuminati model from queue" () {
        setup:
        final HttpServletRequest request = Mock(HttpServletRequest.class);
        final MethodSignature signature = Mock(MethodSignature.class);
        final Object[] args = null;
        long elapsedTime = 3l;
        final Object output = "test";

        IlluminatiDataSendModel illuminatiDataInterfaceModel = new IlluminatiDataSendModel(request, signature, args, elapsedTime, output);

        when:
        IlluminatiExecutor<IlluminatiDataSendModel> illuminatiExecutor = new IlluminatiDataSendExecutorImpl()
        illuminatiExecutor.addToQueue(illuminatiDataInterfaceModel);

        then:
        illuminatiExecutor.getQueueSize() == 1;
        illuminatiExecutor.deQueue() != null;
        illuminatiExecutor.getQueueSize() == 0;
    }

    def "illuminati data thread test" () {
        setup:
        final HttpServletRequest request = Mock(HttpServletRequest.class);
        final MethodSignature signature = Mock(MethodSignature.class);
        final Object[] args = null;
        long elapsedTime = 3l;
        final Object output = "test";

        IlluminatiDataSendModel illuminatiDataInterfaceModel = new IlluminatiDataSendModel(request, signature, args, elapsedTime, output);
        IlluminatiExecutor<IlluminatiDataSendModel> illuminatiExecutor = new IlluminatiDataSendExecutorImpl();
        illuminatiExecutor.createSystemThread();

        when:
        illuminatiExecutor.addToQueue(illuminatiDataInterfaceModel);

        then:
        Thread.sleep(3000l);
        illuminatiExecutor.getQueueSize() == 0;
    }
}
