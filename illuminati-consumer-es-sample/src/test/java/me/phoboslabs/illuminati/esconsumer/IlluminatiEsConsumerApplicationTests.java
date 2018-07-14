package me.phoboslabs.illuminati.esconsumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IlluminatiEsConsumerApplicationTests {

    @Test
    public void contextLoads() {
        String test = "abc.def.gh.i";
        StringBuilder myName = new StringBuilder("abc.def.gh.i.");

        for (int i=0; i<test.length(); i++) {
            char a = test.charAt(i);

            if (a == '.' && i < test.length()) {
                myName.setCharAt(i+1, Character.toUpperCase(test.charAt(i+1)));
            }
        }

        System.out.println(myName.toString().replace(".", ""));
    }

    @Test
    public void regexTest () {
        String userAgent = "Mozilla/5.0 (Linux; Android 6.0.1; SM-G928S Build/MMB29K; wv) AppleWbKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/59.0.3071.125 Mobile Safari/537.36;SAMPLE 6.3.8";

        Pattern pattern = Pattern.compile("SAMPLE\\s([0-9].*\\d)");
        Matcher matcher = pattern.matcher(userAgent);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
        }
    }

    @Test
    public void testMethod () {
        String testData = "public groovy.lang.MetaClass com.illuminati.item.ExchangeService.getProperty(java.lang.String)";
        String[] excludeMethodName = new String[]{".getMetaClass()", ".getProperty(java.lang.String)"};
        // this only for grails... FuXX grails
        for (String excludeMethodNameData : excludeMethodName) {
            if (testData.indexOf(excludeMethodNameData) == -1) {
                System.out.println("1 : " + excludeMethodNameData);
            } else {
                System.out.println("2 : " + excludeMethodNameData);
            }
        }
    }

}
