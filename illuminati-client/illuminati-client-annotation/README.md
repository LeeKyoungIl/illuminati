# Project illuminati : illuminati-client-annotation

## illuminati를 사용하기위해 필요한 annotation 모듈 입니다.

## illuminati는 유저가 필요한(원하는) 곳에 손쉽게 적용이 가능 합니다. 
 * 모든 곳에 전부다 데이터를 수집할 필요는 없습니다. 
 * illuminati는 annotation을 이용하여 중요하고 필요한 곳에 선별 적용이 가능합니다.

## Maven Dependency 추가 
    * Maven
    
```java
<dependency>
  <groupId>com.leekyoungil.illuminati</groupId>
  <artifactId>illuminati-client-annotation</artifactId>
  <version>0.8.1</version>
</dependency>

<dependency>
  <groupId>com.leekyoungil.illuminati</groupId>
  <artifactId>illuminati-client-processor</artifactId>
  <version>0.8.2</version>
</dependency>
```

## Gradle Dependency 추가 
    * Gradle
    
```java
compile 'com.leekyoungil.illuminati:illuminati-client-annotation:0.8.1'
compile 'com.leekyoungil.illuminati:illuminati-client-processor:0.8.2'
```


## Class에 @Illuminati 추가 
    * 하위 모든 Method에 적용
    
```java
@Illuminati
@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiSampleController {

    @RequestMapping(value = "test1")
    public String test1 (String a, Integer b) throws Exception {
        String testJson = "{\"test\" : 1}";
        return testJson;
    }
    
    @RequestMapping(value = "test2")
        public String test2 (String a, Integer b) throws Exception {
            String testJson = "{\"test\" : 2}";
            return testJson;
        }
}
```

## Method에 @Illuminati 추가 
    * 해당 Method에 적용
    
```java
@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiSampleController {

    @RequestMapping(value = "test1")
    public String test1 (String a, Integer b) throws Exception {
        String testJson = "{\"test\" : 1}";
        return testJson;
    }
    
    @Illuminati
    @RequestMapping(value = "test2")
        public String test2 (String a, Integer b) throws Exception {
            String testJson = "{\"test\" : 2}";
            return testJson;
        }
}
```
