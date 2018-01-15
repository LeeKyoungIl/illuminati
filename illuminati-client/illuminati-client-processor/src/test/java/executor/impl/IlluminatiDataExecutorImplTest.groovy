package executor.impl

import com.leekyoungil.illuminati.client.prossor.executor.impl.IlluminatiDataExecutorImpl
import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutor
import com.leekyoungil.illuminati.common.dto.IlluminatiDataInterfaceModel
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

        IlluminatiDataInterfaceModel illuminatiDataInterfaceModel = new IlluminatiDataInterfaceModel(request, signature, args, elapsedTime, output);

        when:
        IlluminatiExecutor<IlluminatiDataInterfaceModel> illuminatiExecutor = new IlluminatiDataExecutorImpl()
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

        IlluminatiDataInterfaceModel illuminatiDataInterfaceModel = new IlluminatiDataInterfaceModel(request, signature, args, elapsedTime, output);

        when:
        IlluminatiExecutor<IlluminatiDataInterfaceModel> illuminatiExecutor = new IlluminatiDataExecutorImpl()
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

        IlluminatiDataInterfaceModel illuminatiDataInterfaceModel = new IlluminatiDataInterfaceModel(request, signature, args, elapsedTime, output);
        IlluminatiExecutor<IlluminatiDataInterfaceModel> illuminatiExecutor = new IlluminatiDataExecutorImpl();
        illuminatiExecutor.createSystemThread();

        when:
        illuminatiExecutor.addToQueue(illuminatiDataInterfaceModel);

        then:
        illuminatiExecutor.getQueueSize() == 0;
    }
}
