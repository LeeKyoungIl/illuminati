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

## sampling rate 기능 
 * 설정파일중 samplingRate항목이 있는데 이는 요청중에 얼마나 일루미나티로 데이터를 수집할 것인지 설정하는 기능 입니다. 
 * 예를들어 100을 설정하면 모든 요청을 다 수집하며 30으로 하면 요청중 30%만 수집을 합니다. 
 * 성능상에 약간의 이득을 볼수는 있으나 그 차이가 크지 않으니 100%로 설정을 하는것을 권장 합니다.

## 카오스 봄버 기능 
![image](https://github.com/LeeKyoungIl/illuminati/blob/master/chaos_bomber.png)

 * 카오스 봄버는 illuminati를 적용한 메서드들중에 임의로 Exception을 발생시키는 기능 입니다. 
 * 모든 코드는 Exception에 대비하고 Exception이 발생 했을때 복구를 할 수 있어야 합니다. 
 * 해당 기능은 위험한 기능으로 설정 파일의 debug모드가 true일때만 동작 합니다.
 
## 카오스 봄버는 application.properties 혹은 illuminati.yml에 따로 설정을 해야 합니다. 
 * illuminati.yml
```java
# it is very dangerous function. it is activate when debug is true.
# after using this function. you must have to re compile.(clean first)
chaosBomber: true
```

## illuminati는 쉽게 비활성화 할수 있습니다. 
 * 이미 @illuminati가 적용된 모든 소스를 수정하기는 어렵습니다. 
 * pom.xml 혹은 build.gradle에서 illuminati-client-processor 부분만 주석처리를 하면 됩니다.
 * 꼭 clean후 다시 컴파일을 하여 배포를 하면 소스 수정 없이 비활성화를 할수 있습니다.
