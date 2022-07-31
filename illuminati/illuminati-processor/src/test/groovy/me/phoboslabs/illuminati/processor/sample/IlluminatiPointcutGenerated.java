package me.phoboslabs.illuminati.processor.sample;

import me.phoboslabs.illuminati.processor.adaptor.IlluminatiAdaptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import javax.servlet.http.HttpServletRequest;

//@Component
@Aspect
public class IlluminatiPointcutGenerated {

    private final IlluminatiAdaptor illuminatiAdaptor;

    public IlluminatiPointcutGenerated() {
        this.illuminatiAdaptor = IlluminatiAdaptor.getInstance();
    }

    @Pointcut("@within(me.phoboslabs.illuminati.annotation.Illuminati) || @annotation(me.phoboslabs.illuminati.annotation.Illuminati)")
    public void illuminatiPointcutMethod() {
    }

    @Around("illuminatiPointcutMethod()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        if (illuminatiAdaptor.checkIlluminatiIsIgnore(pjp)) {
            return pjp.proceed();
        }
        HttpServletRequest request = null;
        try {
//             request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception ignore) {
        }
        return illuminatiAdaptor.executeIlluminati(pjp, request);
    }
}
