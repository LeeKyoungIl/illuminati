# Project illuminati : illuminati-client-processor

# illuminati-client-processor is core module of illuminati

# supported message queues
 * RabbitMQ - completed test of heavy traffic
 * Kafka - not yet complete

## I recommend the RabbitMQ for the current version.(0.9.9.6)

## add to Maven Dependency
    * Maven
    
```java
<repositories>
   <repository>
   <id>jcenter</id>
   <url>https://jcenter.bintray.com/</url>
   </repository>
</repositories>

<dependencies>
    <dependency>
      <groupId>me.phoboslabs.illuminati</groupId>
      <artifactId>illuminati-client-annotation</artifactId>
      <version>1.2.0</version>
    </dependency>
    
    <dependency>
      <groupId>me.phoboslabs.illuminati</groupId>
      <artifactId>illuminati-client-processor</artifactId>
      <version>0.9.9.6</version>
    </dependency>
    
    <!-- This is an option. If you add the module, you can turn it on and off without deploying it. -->
    <dependency>
       <groupId>me.phoboslabs.illuminati</groupId>
       <artifactId>illuminati-client-switch</artifactId>
       <version>1.0.7</version>
    </dependency>

    <!-- This is an option. If you add the module, you can back up to H2DB automatically when you have a problem with your broker. -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>1.4.196</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

## add to Gradle Dependency
    * Gradle
    
```java
repositories {
    jcenter()
}

compile 'me.phoboslabs.illuminati:illuminati-client-annotation:1.2.0'
compile 'me.phoboslabs.illuminati:illuminati-client-processor:0.9.9.6'
// This is an option. If you add the module, you can turn it on and off without deploying it.
compile 'me.phoboslabs.illuminati:illuminati-client-switch:1.0.7'
// This is an option. If you add the module, you can back up to H2DB automatically when you have a problem with your broker. 
compile 'com.h2database:h2:1.4.196'
```

## add @Illuminati to Class
    * apply to all sub methods
    
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

## add @Illuminati to Method
    * apply to all target method
    
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

## how to set to yml - illuminati-{phase}.yml or illuminati-{phase}.properties
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
# Sampling rate function
 * you can find 'samplingRate' in configuration. it's   how much data should be collected during application working by the illuminati.
 * For example if you set '100'. it's collect all of data during application working. Or '30' it's collect 30 percent of all request data.
 * If you can set below 100. A little more performance than 100. But difference is not big. So I recommend set 100.   

# Chaos Bomber function
![image](https://github.com/LeeKyoungIl/illuminati/blob/master/chaos_bomber.png)
 * The Chaos Bomber is generate exception during work application by random. 
 * We must prepare for exception. And there should be no problem in application working.
 * This function is dangerous. so the Chaos Bomber is activate on debug mode.
 
## Chaos Bomber must set separately for application.yml or illuminati.yml
 * illuminati.yml
```java
# it is very dangerous function. it is activate when debug is true.
# after using this function. you must have to re compile.(clean first)
chaosBomber: true
```

## Backup function
![image](https://github.com/LeeKyoungIl/illuminati/blob/master/backup.png)

 * IF Your system has problems on sending to data to external broker. (network or broker shutdown or etc..) After backup the data, if the system is restored. resend the data.
 * Backup storage use H2database to prevent data loss. (Mysql, File, Embedded broker will be added)
 * IF YOU add an H2Database dependency to a project with the illuminati, Backup data by automatically, If When a back event occurs. after restore the data will deleted.

# illuminati is easy to disable in your application.
 * if you already apply  illuminati in your application. if you want exclude it. it's hard to remove all illuminati in your code. 
 * so you do exclude the illuminati-client-processor in pom.xml or build.gradle. it will be solved easily.
 * must clean and compile re deploy.
 
 
===============================================================================


# illuminati client의 core 모듈입니다.

# 지원하는 메시지큐
 * rabbitmq - 대용량 트래픽 테스트 완료 
 * kafka - 테스트 중 
 
## 현 버전 (0.9.9.6) 에서는 rabbitmq를 권장 합니다.

## Maven Dependency 추가 
    * Maven
    
```java
<repositories>
   <repository>
   <id>jcenter</id>
   <url>https://jcenter.bintray.com/</url>
   </repository>
</repositories>

<dependencies>
    <dependency>
      <groupId>me.phoboslabs.illuminati</groupId>
      <artifactId>illuminati-client-annotation</artifactId>
      <version>1.2.0</version>
    </dependency>
    
    <dependency>
     <groupId>me.phoboslabs.illuminati</groupId>
     <artifactId>illuminati-client-processor</artifactId>
     <version>0.9.9.6</version>
    </dependency>
      
    <!-- 이것은 옵션 입니다. 해당 모듈을 추가하면 대시 배포 없이 on, off할수 있습니다. -->
    <dependency>
      <groupId>me.phoboslabs.illuminati</groupId>
      <artifactId>illuminati-client-switch</artifactId>
      <version>1.0.7</version>
    </dependency>

    <!-- 이것은 옵션 입니다. 해당 모듈을 추가하면 대시 broker에 문제가 생겼을때 H2DB로 자동으로 백업할수 있습니다. -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>1.4.196</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

## Gradle Dependency 추가 
    * Gradle
    
```java
repositories {
    jcenter()
}

compile 'me.phoboslabs.illuminati:illuminati-client-annotation:1.2.0'
compile 'me.phoboslabs.illuminati:illuminati-client-processor:0.9.9.6'
//이것은 옵션 입니다. 해당 모듈을 추가하면 대시 배포 없이 on, off할수 있습니다.
compile 'me.phoboslabs.illuminati:illuminati-client-switch:1.0.7'
//이것은 옵션 입니다. 해당 모듈을 추가하면 대시 broker에 문제가 생겼을때 H2DB로 자동으로 백업할수 있습니다.
compile 'com.h2database:h2:1.4.196'
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

## 백업 기능
![image](https://github.com/LeeKyoungIl/illuminati/blob/master/backup.png)

 * 외부의 브로커로 데이터를 전송할때 문제가 생기면 (네트웍이슈나 브로커 이슈) 해당 데이터를 백업하고 있다가 다시 정상적인 상태가 되면 전송하는 기능입니다.
 * 백업 저장소는 H2Database를 사용하여 데이터의 유실을 막을수 있습니다. (Mysql, File, Embedded broker 추가 예정) 
 * illuminati 를 적용한 프로젝트에 h2database 의존성을 추가하면 자동으로 데이터가 저장이 되며 백업 이벤트가 발생했을경우 데이터 복구가 완료되면 자동으로 저장된 데이터는 삭제 됩니다.
 
 
## illuminati는 쉽게 비활성화 할수 있습니다. 
 * 이미 @illuminati가 적용된 모든 소스를 수정하기는 어렵습니다. 
 * pom.xml 혹은 build.gradle에서 illuminati-client-processor 부분만 주석처리를 하면 됩니다.
 * 꼭 clean후 다시 컴파일을 하여 배포를 하면 소스 수정 없이 비활성화를 할수 있습니다.
