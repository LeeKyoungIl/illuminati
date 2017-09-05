# Project illuminati : illuminati-client-processor

# illuminati client의 core 모듈입니다.

# 지원하는 메시지큐
 * rabbitmq - 대용량 트래픽 테스트 완료 
 * kafka - 테스트 중 
 
## 현 버전 (0.7.2) 에서는 rabbitmq를 권장 합니다.

## Maven Dependency 추가 
    * Maven
    
```java
<dependency>
  <groupId>com.leekyoungil.illuminati</groupId>
  <artifactId>illuminati-client-annotation</artifactId>
  <version>0.5</version>
</dependency>

<dependency>
  <groupId>com.leekyoungil.illuminati</groupId>
  <artifactId>illuminati-client-processor</artifactId>
  <version>0.7.2</version>
</dependency>
```

## Gradle Dependency 추가 
    * Gradle
    
```java
compile 'com.leekyoungil.illuminati:illuminati-client-annotation:0.5'
compile 'com.leekyoungil.illuminati:illuminati-client-processor:0.7.2'
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

## yml 설정방법 - illuminati-{phase}.yml 또는 illuminati-{phase}.properties
 * rabbitmq

```java
#rabbitmq
broker: rabbitmq
clusterList: 192.168.99.100:32789
virtualHost: illuminatiLocal
topic: local-illuminati-exchange
queueName: local-illuminati-exchange.illuminati
userName: illuminati-local
password: illuminati-local
isAsync: true
isCompression: true
parentModuleName: apisample
samplingRate: 100
debug: false
```

 * kafka
```java
#kafka
broker: kafka
clusterList: 192.168.99.100:32789, 192.168.99.101:32789, 192.168.99.102:32789
topic: illuminati-local
isAsync: true
isCompression: true
performance: 1
parentModuleName: apisample
samplingRate: 50
debug: false
```
