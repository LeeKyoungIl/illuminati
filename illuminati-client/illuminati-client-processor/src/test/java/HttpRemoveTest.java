import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.mockito.Mockito.*;

public class HttpRemoveTest {

    /**
     * this.path = request.getRequestURI();
     * this.queryString = request.getQueryString();
     *
     * this.clientIp = request.getHeader("X-FORWARDED-FOR");
     * if (this.clientIp == null) {
     *  this.clientIp = request.getRemoteAddr();
     * }

     * // some frameworks (ex. grails) we can't be find requestUri by getRequestURI.
     * // so add this line for get requestUri
     * try {
     *  this.anotherPath = request.getAttribute("javax.servlet.forward.request_uri").toString();
     * } catch (Exception ex) {
     *  // ignore this exception
     * }
     */
    @Test
    public void makeClientInfoMapFromHttpServlet () {
        // define the headers you want to be returned
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-FORWARDED-FOR", "192.168.0.1");

        // create an Enumeration over the header keys
        final Iterator<String> iterator = headers.keySet().iterator();
        Enumeration headerNames = new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public String nextElement() {
                return iterator.next();
            }
        };

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeaderNames()).thenReturn(headerNames);
        when(request.getRequestURI()).thenReturn("/foo");
        when(request.getQueryString()).thenReturn("param1=value1&param");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getAttribute("javax.servlet.forward.request_uri")).thenReturn("/foo");
        when(request.getHeader("X-FORWARDED-FOR")).thenReturn("192.168.0.1");

//        expect().andReturn().times();
        while (headerNames.hasMoreElements()) {
            System.out.println("header name: " + headerNames.nextElement());
        }

        Map<String, String> clientInfoMap = new HashMap<String, String>();
        clientInfoMap.put("clientIp", request.getHeader("X-FORWARDED-FOR"));
        clientInfoMap.put("path", request.getRequestURI());
        clientInfoMap.put("remoteAddr", request.getRemoteAddr());
        clientInfoMap.put("queryString", request.getQueryString());
        clientInfoMap.put("anotherPath", request.getAttribute("javax.servlet.forward.request_uri").toString());

        for (Map.Entry<String, String> entry : clientInfoMap.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
        }
    }
}
