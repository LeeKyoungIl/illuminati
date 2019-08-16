package me.phoboslabs.illuminati.prossor.init;

import me.phoboslabs.illuminati.annotation.Illuminati;
import me.phoboslabs.illuminati.annotation.enums.PackageType;
import me.phoboslabs.illuminati.prossor.executor.IlluminatiExecutor;
import me.phoboslabs.illuminati.prossor.executor.impl.IlluminatiBackupExecutorImpl;
import me.phoboslabs.illuminati.prossor.executor.impl.IlluminatiDataExecutorImpl;
import me.phoboslabs.illuminati.prossor.executor.impl.IlluminatiTemplateExecutorImpl;
import me.phoboslabs.illuminati.prossor.infra.backup.shutdown.IlluminatiGracefulShutdownChecker;
import me.phoboslabs.illuminati.prossor.infra.restore.impl.RestoreTemplateData;
import me.phoboslabs.illuminati.prossor.properties.IlluminatiPropertiesImpl;
import me.phoboslabs.illuminati.common.IlluminatiCommon;
import me.phoboslabs.illuminati.common.constant.IlluminatiConstant;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiDataInterfaceModelImpl;
import me.phoboslabs.illuminati.common.dto.impl.IlluminatiTemplateInterfaceModelImpl;
import me.phoboslabs.illuminati.common.properties.IlluminatiPropertiesHelper;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 */
public class IlluminatiClientInit {

    private final Logger illuminatiInitLogger = LoggerFactory.getLogger(IlluminatiClientInit.class);

    private static IlluminatiClientInit ILLUMINATI_CLIENT_INIT_INSTANCE;

    private static final AtomicInteger SAMPLING_RATE_CHECKER = new AtomicInteger(1);
    private static int SAMPLING_RATE = 20;
    private static final int CHAOSBOMBER_NUMBER = (int) (Math.random() * 100) + 1;

    private static final IlluminatiExecutor<IlluminatiDataInterfaceModelImpl> ILLUMINATI_DATA_EXECUTOR;
    private static final IlluminatiExecutor<IlluminatiTemplateInterfaceModelImpl> ILLUMINATI_TEMPLATE_EXECUTOR;
    private static final IlluminatiBackupExecutorImpl ILLUMINATI_BACKUP_EXECUTOR;

    private static final RestoreTemplateData RESTORE_TEMPLATE_DATA;

    static {
        IlluminatiCommon.init();

        if (IlluminatiConstant.ILLUMINATI_BACKUP_ACTIVATION) {
            ILLUMINATI_BACKUP_EXECUTOR = IlluminatiBackupExecutorImpl.getInstance();
            ILLUMINATI_BACKUP_EXECUTOR.init();
        } else {
            ILLUMINATI_BACKUP_EXECUTOR = null;
        }

        ILLUMINATI_TEMPLATE_EXECUTOR = IlluminatiTemplateExecutorImpl.getInstance(ILLUMINATI_BACKUP_EXECUTOR);
        ILLUMINATI_TEMPLATE_EXECUTOR.init();

        ILLUMINATI_DATA_EXECUTOR = IlluminatiDataExecutorImpl.getInstance(ILLUMINATI_TEMPLATE_EXECUTOR);
        ILLUMINATI_DATA_EXECUTOR.init();

        if (IlluminatiConstant.ILLUMINATI_BACKUP_ACTIVATION) {
            RESTORE_TEMPLATE_DATA = RestoreTemplateData.getInstance(ILLUMINATI_TEMPLATE_EXECUTOR);
            RESTORE_TEMPLATE_DATA.init();
        } else {
            RESTORE_TEMPLATE_DATA = null;
        }

        final String samplingRate = IlluminatiPropertiesHelper.getPropertiesValueByKey(IlluminatiPropertiesImpl.class,"illuminati", "samplingRate", "20");
        SAMPLING_RATE = StringObjectUtils.isValid(samplingRate) ? Integer.valueOf(samplingRate) : SAMPLING_RATE;
    }

    private IlluminatiClientInit () {}

    public static IlluminatiClientInit getInstance () {
        if (ILLUMINATI_CLIENT_INIT_INSTANCE == null) {
            synchronized (IlluminatiClientInit.class) {
                if (ILLUMINATI_CLIENT_INIT_INSTANCE == null) {
                    ILLUMINATI_CLIENT_INIT_INSTANCE = new  IlluminatiClientInit();
                }
            }
        }

        return ILLUMINATI_CLIENT_INIT_INSTANCE;
    }

    // ################################################################################################################
    // ### public methods                                                                                           ###
    // ################################################################################################################

    public boolean checkIlluminatiIsIgnore (final ProceedingJoinPoint pjp) throws Throwable {
        try {
            final Illuminati illuminati = this.getIlluminatiAnnotation(pjp);
            return illuminati != null ? illuminati.ignore() : true;
        } catch (Exception ignore) {}
        return true;
    }

    public Object executeIlluminati (final ProceedingJoinPoint pjp, final HttpServletRequest request) throws Throwable {
        if (IlluminatiGracefulShutdownChecker.getIlluminatiReadyToShutdown()) {
            return pjp.proceed();
        }

        if (this.isOnIlluminatiSwitch() == false) {
            return pjp.proceed();
        }

        if (this.checkSamplingRate(pjp) == false) {
            this.illuminatiInitLogger.debug("ignore illuminati processor.");
            return pjp.proceed();
        }

        return addToQueue(pjp, request, false);
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
    public Object executeIlluminatiByChaosBomber (final ProceedingJoinPoint pjp, final HttpServletRequest request) throws Throwable {
        if (IlluminatiGracefulShutdownChecker.getIlluminatiReadyToShutdown()) {
            return pjp.proceed();
        }

        if (this.isOnIlluminatiSwitch() == false) {
            return pjp.proceed();
        }

        if (IlluminatiConstant.ILLUMINATI_DEBUG == false) {
            return addToQueue(pjp, request, false);
        }

        return addToQueue(pjp, request, true);
    }

    // ################################################################################################################
    // ### private methods                                                                                          ###
    // ################################################################################################################

    private Illuminati getIlluminatiAnnotation (final ProceedingJoinPoint pjp) {
        final MethodSignature signature = (MethodSignature) pjp.getSignature();
        final Method method = signature.getMethod();

        Illuminati illuminati = method.getAnnotation(Illuminati.class);

        if (illuminati == null) {
            illuminati = pjp.getTarget().getClass().getAnnotation(Illuminati.class);
        }

        return illuminati;
    }

    private int getCustomSamplingRate (final ProceedingJoinPoint pjp) {
        try {
            final Illuminati illuminati = this.getIlluminatiAnnotation(pjp);
            return illuminati != null ? illuminati.samplingRate() : 0;
        } catch (Exception ignore) {}
        return 0;
    }

    private boolean isOnIlluminatiSwitch () {
        if (IlluminatiConstant.ILLUMINATI_SWITCH_ACTIVATION  && IlluminatiConstant.ILLUMINATI_SWITCH_VALUE.get() == false) {
            this.illuminatiInitLogger.debug("illuminati processor is now off.");
            return false;
        }

        return true;
    }

    private Object addToQueue (final ProceedingJoinPoint pjp, final HttpServletRequest request, final boolean isActiveChaosBomber) throws Throwable {
        final long start = System.currentTimeMillis();
        final Map<String, Object> originMethodExecute = getMethodExecuteResult(pjp);
        final long elapsedTime = System.currentTimeMillis() - start;

        Throwable throwable = null;
        if (originMethodExecute.containsKey("throwable")) {
            throwable = (Throwable) originMethodExecute.get("throwable");
        }

        if (isActiveChaosBomber && throwable == null && CHAOSBOMBER_NUMBER == ((int) (Math.random() * 100) + 1)) {
            throwable = new Throwable("Illuminati ChaosBomber Exception Activate");
            request.setAttribute("ChaosBomber", "true");
        }

        final Illuminati illuminati = this.getIlluminatiAnnotation(pjp);
        final PackageType packageType;
        if (illuminati != null) {
            packageType = illuminati.packageType();
        } else {
            packageType = PackageType.DEFAULT;
        }

        ILLUMINATI_DATA_EXECUTOR.addToQueue(IlluminatiDataInterfaceModelImpl
                .Builder(request, (MethodSignature) pjp.getSignature(), pjp.getArgs(), elapsedTime, originMethodExecute)
                .setPackageType(packageType.getPackageType()));

        if (throwable != null) {
            throw throwable;
        }

        return originMethodExecute.get("result");
    }

    private boolean checkSamplingRate (final ProceedingJoinPoint pjp) {
        int customSamplingRate = this.getCustomSamplingRate(pjp);
        if (customSamplingRate == 0) {
            customSamplingRate = SAMPLING_RATE;
        }

        //SAMPLING_RATE_CHECKER.compareAndSet(100, 1);

        // sometimes compareAndSet does not work.
        // So add this code. This code forces a reset to 1 if greater than 100.
        if (SAMPLING_RATE_CHECKER.get() > 100) {
            SAMPLING_RATE_CHECKER.set(1);
            return true;
        }

        return SAMPLING_RATE_CHECKER.getAndIncrement() <= customSamplingRate;
    }

    private Map<String, Object> getMethodExecuteResult (final ProceedingJoinPoint pjp) {
        final Map<String, Object> originMethodExecute = new HashMap<String, Object>();

        try {
            originMethodExecute.put("result", pjp.proceed());
        } catch (Throwable ex) {
            originMethodExecute.put("throwable", ex);
            this.illuminatiInitLogger.error("error : check your process. ("+ex.toString()+")");
            originMethodExecute.put("result", StringObjectUtils.getExceptionMessageChain(ex));
        }

        return originMethodExecute;
    }
}
