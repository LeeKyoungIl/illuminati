# Project illuminati

![image](https://github.com/LeeKyoungIl/illuminati/blob/master/illuminati-logo.png)

# Application 에서 일어나는 모든 EVENT 데이터를 수집하고 Kibana또는 다른툴을(어떤툴이든) 이용해서 보여주는 플랫폼 입니다.

## 필수사항
* Java6 이상
* RabbitMQ 또는 Kafka
* AspectJ를 사용할 수 있는 Java Web Application 

## 권장사항
* ElasticSearch
* Kibana
* Spring Cloud Stream - Consumer 제작시

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

```

## Gradle Dependency 추가 
    * Maven
    
```java

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
* Consumer에서 ElasticSearch나 MongoDB, MySQL, Hadoop등 원하는대로 데이터를 전송가능
* 여러 컨슈머에서 동시에 같은 Event 데이터를 받을수 있음

## Illuminati 데이타를 이용하여 Kibana에서 확인 가능 
* 커머스 데이타 Sample 화면

![image](https://github.com/LeeKyoungIl/illuminati/blob/master/kibana-sample.png)

