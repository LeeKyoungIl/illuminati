package me.phoboslabs.illuminati.processor.test


import me.phoboslabs.illuminati.processor.adaptor.IlluminatiAdaptor
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.reflect.MethodSignature
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import java.lang.reflect.Method

class IlluminatiAdaptorTest extends Specification {

    def setup() {
        System.setProperty("spring.profiles.active", "test")
    }

    def "illuminati adaptor initialize test"() {
        given:
        def illuminatiAdaptor = IlluminatiAdaptor.getInstance()
        def joinPoint = Mock(ProceedingJoinPoint)
        def signature = Mock(MethodSignature)
        def testService = new TestService()

        signature.getMethod() >> this.testMethod()
        joinPoint.getSignature() >> signature
        joinPoint.getTarget() >> testService
        joinPoint.proceed() >> new TestDto()

        def request = Mock(HttpServletRequest)

        when:
        def result = illuminatiAdaptor.executeIlluminati(joinPoint, request)

        then:
        result != null
    }

    private Method testMethod() {
        return TestService.class.getDeclaredMethod("getTest", String.class, int.class)
    }
}
