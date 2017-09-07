# Project illuminati : illuminati-client-elasticsearch

## illuminati client의 elasticsearch 모듈입니다.
 * illuminati에서 수집한 데이터는 kibana를 이용해서 쉽게 분석이 가능합니다.
 * kibana는 elasticsearch를 사용하기 때문에 illuminati도 elasticsearch에 데이터를 보내줘야 합니다.
 
## illuminati 에서 수집된 데이터를 받아서 처리하는 consumer 모듈에 적용해서 사용해야 합니다.
 * 이곳에는 Spring Cloud Stream을 이용한 Consumer 모듈 Sample 프로젝트가 있으니 사용법을 참고하시면 됩니다.
 
## Maven Dependency 추가 
    * Maven
    
```java
<dependency>
  <groupId>com.leekyoungil.illuminati</groupId>
  <artifactId>illuminati-client-elasticsearch</artifactId>
  <version>0.6.6</version>
</dependency>
```

## Gradle Dependency 추가 
    * Gradle
    
```java
compile 'com.leekyoungil.illuminati:illuminati-client-elasticsearch:0.6.6'
```
