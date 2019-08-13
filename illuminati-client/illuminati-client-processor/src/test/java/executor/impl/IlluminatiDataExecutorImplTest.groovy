package executor.impl

import me.phoboslabs.illuminati.client.prossor.executor.IlluminatiExecutor
import me.phoboslabs.illuminati.client.prossor.executor.impl.IlluminatiDataExecutorImpl
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiDataInterfaceModelImpl
import org.aspectj.lang.reflect.MethodSignature
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class IlluminatiDataExecutorImplTest extends Specification {

    def "separate make illuminati model from main logic" () {
        setup:
        final HttpServletRequest request = Mock(HttpServletRequest.class);
        final MethodSignature signature = Mock(MethodSignature.class);
        final Object[] args = null;
        long elapsedTime = 3l;
        final Object output = "test";

        IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModel = new IlluminatiDataInterfaceModelImpl(request, signature, args, elapsedTime, output);

        when:
        IlluminatiExecutor<IlluminatiDataInterfaceModelImpl> illuminatiExecutor = new IlluminatiDataExecutorImpl()
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

        IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModel = new IlluminatiDataInterfaceModelImpl(request, signature, args, elapsedTime, output);

        when:
        IlluminatiExecutor<IlluminatiDataInterfaceModelImpl> illuminatiExecutor = new IlluminatiDataExecutorImpl()
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

        IlluminatiDataInterfaceModelImpl illuminatiDataInterfaceModel = new IlluminatiDataInterfaceModelImpl(request, signature, args, elapsedTime, output);
        IlluminatiExecutor<IlluminatiDataInterfaceModelImpl> illuminatiExecutor = new IlluminatiDataExecutorImpl();
        illuminatiExecutor.createSystemThread();

        when:
        illuminatiExecutor.addToQueue(illuminatiDataInterfaceModel);

        then:
        Thread.sleep(3000l);
        illuminatiExecutor.getQueueSize() == 0;
    }
}
