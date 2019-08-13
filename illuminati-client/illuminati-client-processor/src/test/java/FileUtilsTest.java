import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class FileUtilsTest {

//    @Test
//    public void propertiesReadTest () {
//        IlluminatiProperties illuminatiProperties = getIlluminatiPropertiesFromModel1("com/leekyoungil/illuminati/ApiServerSample/resources/illuminati.properties");
//
//        System.out.println(illuminatiProperties);
//    }
//
//    public IlluminatiProperties getIlluminatiPropertiesFromModel1 (String configPropertiesFileName) {
//        Properties prop = new Properties();
//        InputStream input = null;
//
//        try {
//            File file = new File("illuminati.properties");
//            if (file.exists()) {
//                System.out.println("tet");
//            } else {
//                System.out.println(file.getAbsolutePath());
//            }
//
//            input = new FileInputStream(file);
//
//            if(input==null){
//                System.out.println("Sorry, unable to find " + configPropertiesFileName);
//                return null;
//            }
//
//            prop.load(input);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            if (input != null) {
//                try {
//                    input.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        if (prop.isEmpty()) {
//            return null;
//        }
//
//        return new IlluminatiProperties(prop);
//    }

//    @Test
//    public void stringTest () {
////        String a = "/grails/gift/list.dispatch";
////
////        System.out.println(a.replace("/grails", "").substring(0, a.indexOf(".dispatch")));
////        System.out.println(a.indexOf(".dispatch") + 9);
////
////        if (a.indexOf("/grails") == 0) {
////            a = a.replace("/grails", "");
////        }
////
////        if (a.indexOf(".dispatch") > -1) {
////            a = a.substring(0, 10);
////        }
////
////        System.out.println(a);
//    }
}
