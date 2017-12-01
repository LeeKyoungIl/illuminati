package com.leekyoungil.illuminati.client.prossor.init;

import com.leekyoungil.illuminati.client.annotation.Illuminati;
import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiDataExecutorImpl;
import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiExecutor;
import com.leekyoungil.illuminati.client.prossor.executor.IlluminatiTemplateExecutorImpl;
import com.leekyoungil.illuminati.common.dto.IlluminatiDataInterfaceModel;
import com.leekyoungil.illuminati.common.properties.IlluminatiPropertiesHelper;
import com.leekyoungil.illuminati.client.prossor.properties.IlluminatiPropertiesImpl;
import com.leekyoungil.illuminati.common.constant.IlluminatiConstant;
import com.leekyoungil.illuminati.common.util.StringObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class IlluminatiClientInit {

    private static final Logger ILLUMINATI_INIT_LOGGER = LoggerFactory.getLogger(IlluminatiClientInit.class);

    private static final AtomicInteger SAMPLING_RATE_CHECKER = new AtomicInteger(1);
    private static int SAMPLING_RATE = 20;
    private static int CHAOSBOMBER_NUMBER = (int) (Math.random() * 100) + 1;

    private static final IlluminatiExecutor<IlluminatiDataInterfaceModel> ILLUMINATI_DATA_EXECUTOR = new IlluminatiDataExecutorImpl();

    public synchronized static void init () {
        ILLUMINATI_DATA_EXECUTOR.init();

        final String samplingRate = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class, null, "illuminati", "samplingRate");
        SAMPLING_RATE = StringObjectUtils.isValid(samplingRate) ? Integer.valueOf(samplingRate) : SAMPLING_RATE;
    }

    private static boolean checkSamplingRate () {
        //SAMPLING_RATE_CHECKER.compareAndSet(100, 1);

        // sometimes compareAndSet does not work.
        // So add this code. This code forces a reset to 1 if greater than 100.
        if (SAMPLING_RATE_CHECKER.get() > 100) {
            SAMPLING_RATE_CHECKER.set(1);
            return true;
        }

        if (SAMPLING_RATE_CHECKER.getAndIncrement() <= SAMPLING_RATE) {
            return true;
        }

        return false;
    }

    public static boolean checkIlluminatiIsIgnore (final ProceedingJoinPoint pjp) throws Throwable {
        try {
            final MethodSignature signature = (MethodSignature) pjp.getSignature();
            final Method method = signature.getMethod();

            Illuminati illuminati = method.getAnnotation(Illuminati.class);

            if (illuminati == null) {
                illuminati = pjp.getTarget().getClass().getAnnotation(Illuminati.class);
            }

            if (illuminati == null) {
                return true;
            }

            return illuminati.ignore();
        } catch (Exception ex) {
            // ignore
            return true;
        }
    }

    public static Object executeIlluminati (final ProceedingJoinPoint pjp, final HttpServletRequest request) throws Throwable {
        if (IlluminatiConstant.ILLUMINATI_SWITCH_ACTIVATION == true
                && IlluminatiConstant.ILLUMINATI_SWITCH_VALUE.get() == false) {
            ILLUMINATI_INIT_LOGGER.debug("iilluminati processor is now off.");
            return pjp.proceed();
        }

        if (IlluminatiTemplateExecutorImpl.illuminatiTemplateIsNull() == true || !IlluminatiClientInit.checkSamplingRate()) {
            ILLUMINATI_INIT_LOGGER.debug("ignore illuminati processor.");
            return pjp.proceed();
        }

        final long start = System.currentTimeMillis();
        final Map<String, Object> originMethodExecute = getMethodExecuteResult(pjp);
        final long elapsedTime = System.currentTimeMillis() - start;

        final Object output = originMethodExecute.get("result");
        Throwable throwable = null;
        if (originMethodExecute.containsKey("throwable")) {
            throwable = (Throwable) originMethodExecute.get("throwable");
        }

        IlluminatiDataInterfaceModel illuminatiDataInterfaceModel = new IlluminatiDataInterfaceModel(request, (MethodSignature) pjp.getSignature(), pjp.getArgs(), elapsedTime, output);

        ILLUMINATI_DATA_EXECUTOR.addToQueue(illuminatiDataInterfaceModel);

        if (throwable != null) {
            throw throwable;
        }

        return output;
    }

    /**
     * it is only execute on debug mode and activated chaosBomber.
     * can't be use sampling rate.
     *
     * @param pjp
     * @param request
     * @return
     * @throws Throwable
     */
    public static Object executeIlluminatiByChaosBomber (final ProceedingJoinPoint pjp, final HttpServletRequest request) throws Throwable {
        if (IlluminatiConstant.ILLUMINATI_SWITCH_VALUE.get() == false) {
            ILLUMINATI_INIT_LOGGER.debug("illuminati processor is now off.");
            return pjp.proceed();
        }

        if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
            return IlluminatiClientInit.executeIlluminati(pjp, request);
        }

        if (IlluminatiTemplateExecutorImpl.illuminatiTemplateIsNull() == true) {
            ILLUMINATI_INIT_LOGGER.debug("ignore illuminati processor.");
            return pjp.proceed();
        }

        final long start = System.currentTimeMillis();
        final Map<String, Object> originMethodExecute = getMethodExecuteResult(pjp);
        final long elapsedTime = System.currentTimeMillis() - start;

        final Object output = originMethodExecute.get("result");
        Throwable throwable = null;
        if (originMethodExecute.containsKey("throwable")) {
            throwable = (Throwable) originMethodExecute.get("throwable");
        } else if (CHAOSBOMBER_NUMBER == ((int) (Math.random() * 100) + 1)) {
            throwable = new Throwable("Illuminati ChaosBomber Exception Activate");
            request.setAttribute("ChaosBomber", "true");
        }

        IlluminatiDataInterfaceModel illuminatiDataInterfaceModel = new IlluminatiDataInterfaceModel(request, (MethodSignature) pjp.getSignature(), pjp.getArgs(), elapsedTime, output);

        ILLUMINATI_DATA_EXECUTOR.addToQueue(illuminatiDataInterfaceModel);

        if (throwable != null) {
            throw throwable;
        }

        return output;
    }

    private static Map<String, Object> getMethodExecuteResult (final ProceedingJoinPoint pjp) {
        final Map<String, Object> originMethodExecute = new HashMap<String, Object>();

        try {
            originMethodExecute.put("result", pjp.proceed());
        } catch (Throwable ex) {
            originMethodExecute.put("throwable", ex);
            ILLUMINATI_INIT_LOGGER.error("error : check your process. ("+ex.toString()+")");
            originMethodExecute.put("result", StringObjectUtils.getExceptionMessageChain(ex));
        }

        return originMethodExecute;
    }
}
