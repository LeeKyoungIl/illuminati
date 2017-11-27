# Project illuminati

![image](https://github.com/LeeKyoungIl/illuminati/blob/master/illuminati-logo.png)

# This is a Platform that collects all the data accruing in your Application and shows the data in real time by using Kibana or other tools.

# illuminati's intention to develop

**There is no garbage data in your Application.**
It is necessary to identify what data is the most important among the collecting data from your Applcation.
Also collect and analysis must be performed in different processes. 
The illuminati is desinged to make collect all data easily and it can be possible scalability working by separated analysis process.

## required
 * Java6 or higher. 
 * Message queue (RabbitMQ or Kafka)
 * Java Application that can use AspectJ

## recommendations
 * ElasticSearch
 * Kibana
 * Spring Cloud Stream - used to create a consumer application

## struct of illuminati Project
 * [ApiSampleApplication](https://github.com/LeeKyoungIl/illuminati/tree/master/ApiServerSample)
 * [illuminati-client-annotation](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati-client/illuminati-client-annotation)
 * [illuminati-client-processor](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati-client/illuminati-client-processor)
 * [illuminati-client-switch](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati-client/illuminati-client-switch)
 * [illuminati-client-jscollector](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati-client/illuminati-client-jscollector)
 * [illuminati-client-elasticsearch](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati-client/illuminati-client-elasticsearch)
 * [illuminati-consumer-es-sample	](https://github.com/LeeKyoungIl/illuminati/tree/feature/es_sample_readme/illuminati-consumer-es-sample)

## data to collect of illuminati.
 1. Applied server information(IP, HOST_NAME...ETC), status of JVM MEMORY.
 2. All of client request information.
    * All of Header and Cookie
    * OS, BROWSER, DEVICE information
    * Global Transaction ID generation enables application parent method call order and content traceability.
    * Execution methods and parameters on the application.
    * Method execution time in Application.
    * Value of Method request parameter. (GET, POST)
    * Result value of the method request on the application.
 3. Collect User Event data from Browser by User action. (Key board press or mouse click)
    
## illuminati is easy to use
 1. Do not need to create a data type. (No DTO required)
 2. Agent installation is not required.
 3. Annotation type is easy to apply.
 
## illuminati operator method
 1. Add dependency of MAVEN or Gradle (illuminati)
 2. Add configuration in illuminati-{**phase**}.yml, properties에 (address of queue... etc)
 3. When execute application with add -Dspring.profiles.active={**phase**}
 4. Add "@Illuminat" Annotation to where you want to collect

## illuminati does not affect the origin application logic
 1. It was developed to have no influence on this logic by using a separate thread and Buffer.
 2. Drop in performance can occur, but there is no big difference in physical server. (It can happen a little more on virtual machines.)
 3. Even if an exception occurs in the original application logic, illuminati can also collect the corresponding exception information.

## struct of illuminati
![image](https://github.com/LeeKyoungIl/illuminati/blob/master/architecture.png)

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
      <groupId>com.leekyoungil.illuminati</groupId>
      <artifactId>illuminati-client-annotation</artifactId>
      <version>1.1.1</version>
   </dependency>

   <dependency>
      <groupId>com.leekyoungil.illuminati</groupId>
      <artifactId>illuminati-client-processor</artifactId>
      <version>0.9.2</version>
   </dependency>
   
   <!-- This is an option. If you add the module, you can turn it on and off without deploying it. -->
   <dependency>
       <groupId>com.leekyoungil.illuminati</groupId>
       <artifactId>illuminati-client-switch</artifactId>
       <version>1.0.0</version>
   </dependency>
</dependencies>
```

## add to Gradle Dependency 
    * Gradle
    
```java
repositories {
    jcenter()
}

compile 'com.leekyoungil.illuminati:illuminati-client-annotation:1.0.1'
compile 'com.leekyoungil.illuminati:illuminati-client-processor:0.9.2'
// This is an option. If you add the module, you can turn it on and off without deploying it.
compile 'com.leekyoungil.illuminati:illuminati-client-switch:1.0.0'
```

## add @Illuminati  to Class
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

## add illuminati consumer
 1. Easily add Consumer using Spring Cloud Stream
 2. Consumer can transfer data. (ElasticSearch, MongoDB, MySQL, Hadoop, etc.)
    * Multiple consumers can receive the same Event data.
    * It is easy to increase throughput by dividing data from many consumers.

## Illuminati data can be used in Kibana
 * Sample of Commerce Data.

![image](https://github.com/LeeKyoungIl/illuminati/blob/master/kibana-sample.png)
 
===============================================================================
 
 

# Application 에서 일어나는 모든 EVENT 데이터를 수집하고 Kibana또는 다른툴을(어떤툴이든) 이용해서 보여주는 플랫폼 입니다.

# illuminati 개발 의도
**쓰레기 데이터란 없습니다.**
Application에서 발생하는 모든데이터를 수집하고 그중에 어떤 데이터가 의미가 있는 데이터 인지는 쌓이는 데이터들은 확인해서 구분해야 합니다.
그리고 데이터 수집과 분석은 서로 다른 프로세스에서 실행 되어야 합니다. 
**illuminati**는 Application에서 발생하는 모든 데이터를 사용자의 필요성에 따라서 수집을 쉽게 할수 있고 그 수집된 데이터의 분석을 처리하는 
프로세스를 분리하여 확장이 가능하고 좀더 빠르게 데이터를 분석할수 있도록 하기위해 만들어졌습니다.

## 필수사항
* Java6 이상
* RabbitMQ 또는 Kafka
* AspectJ를 사용할 수 있는 Java Application 

## 권장사항
* ElasticSearch
* Kibana
* Spring Cloud Stream - Consumer 제작시

## illuminati Project 구조
   * [ApiSampleApplication](https://github.com/LeeKyoungIl/illuminati/tree/master/ApiServerSample)
   * [illuminati-client-annotation](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati-client/illuminati-client-annotation)
   * [illuminati-client-processor](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati-client/illuminati-client-processor)
   * [illuminati-client-switch](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati-client/illuminati-client-switch)
   * [illuminati-client-elasticsearch](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati-client/illuminati-client-elasticsearch)
   * [illuminati-consumer-es-sample	](https://github.com/LeeKyoungIl/illuminati/tree/feature/es_sample_readme/illuminati-consumer-es-sample)

# illuminati에서 수집을 하는 Event 데이터 정보
1. 적용 서버의 정보와(IP, HOST_NAME..등등), JVM MEMORY 사용정보
2. 클라이언트 요청에 관한 모든 정보
    * 모든 HEADER, COOKIE
    * OS, BROWSER, DEVICE 정보
    * Global Transaction ID발급으로 Applicaion상의 메서드 호출 순서,내용 추적가능
    * Application상의 실행 메서드 및 파라메터
    * Application상의 메서드 실행 시간
    * Application상의 메서드 요청의 파라메터값 (GET, POST)
    * Application상의 메서드 요청의 결과값
    
# illuminati는 쉽게 사용할 수 있습니다.
1. 따로 데이터 타입을 만들 필요가 없습니다. (DTO가 필요 없음)
2. Agent설치가 필요 없습니다.
3. Annotation방식으로 간편한 적용이 가능합니다.

# illuminati 사용방법
1. MAVEN, GRADLE Dependency 추가
2. illuminati-{**phase**}.yml, properties에 설정 추가 (queue주소... 등등)
3. Application 실행시 -Dspring.profiles.active={**phase**} 추가
3. 수집을 원하는 곳에 **@Illuminati** Annotation을 추가

# illuminati는 본래의 Application 로직에 영향이 없습니다.
1. Buffer와 별도의 Thread를 사용하여 본 로직에 영향이 없도록 개발되었습니다.
2. 성능하락은 발생할수도 있지만 물리서버에서는 큰 차이는 없습니다. (가상 장비에서는 조금더 발생할수 있습니다.)
3. 본래의 Application로직에서 Exception이 발생하는 경우에도 illuminati에서는 해당 Exception정보도 수집하여 파악이 가능합니다. 

# illuminati 구조

![image](https://github.com/LeeKyoungIl/illuminati/blob/master/architecture.png)

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
      <groupId>com.leekyoungil.illuminati</groupId>
      <artifactId>illuminati-client-annotation</artifactId>
      <version>1.0.1</version>
   </dependency>

   <dependency>
     <groupId>com.leekyoungil.illuminati</groupId>
     <artifactId>illuminati-client-processor</artifactId>
     <version>0.9.2</version>
  </dependency>
      
  <!-- 이것은 옵션 입니다. 해당 모듈을 추가하면 대시 배포 없이 on, off할수 있습니다. -->
  <dependency>
      <groupId>com.leekyoungil.illuminati</groupId>
      <artifactId>illuminati-client-switch</artifactId>
      <version>1.0.0</version>
  </dependency>
</dependencies>
```

## Gradle Dependency 추가 
    * Gradle
    
```java
repositories {
    jcenter()
}

compile 'com.leekyoungil.illuminati:illuminati-client-annotation:1.0.1'
compile 'com.leekyoungil.illuminati:illuminati-client-processor:0.9.2'
//이것은 옵션 입니다. 해당 모듈을 추가하면 대시 배포 없이 on, off할수 있습니다.
compile 'com.leekyoungil.illuminati:illuminati-client-processor:1.0.0'
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

## Illuminati Consumer 추가 
* Spring Cloud Stream을 이용하여 쉽게 Consumer를 추가할수 있음
* Consumer에서 ElasticSearch나 MongoDB, MySQL, Hadoop등 원하는대로 데이터를 전송가능 (Sample은 ES만완성)
   * 여러 컨슈머에서 동시에 같은 Event 데이터를 받을수 있음
   * 여러 컨슈머에서 데이터를 나누어 받아 Throughput을 쉽게 늘릴수 있음 

## Illuminati 데이타를 이용하여 Kibana에서 확인 가능 
* 커머스 데이타 Sample 화면

![image](https://github.com/LeeKyoungIl/illuminati/blob/master/kibana-sample.png)

# License
illuminati is distributed under the [Apache Software License](https://www.apache.org/licenses/LICENSE-2.0) version 2.0.

