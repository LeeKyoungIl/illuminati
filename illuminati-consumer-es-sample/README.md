# Project illuminati : illuminati-consumer-es-sample

## illuminati에서 수집한 데이터를 elasticsearch로 동기화 해주는 consumer sample입니다.
 * spring cloud stream을 이용하여 쉽게 만들수 있습니다.
 * 쉽게 확장이 가능 합니다.
 
## rabbitmq로 사용한 sample입니다. 
 * kafka를 이용한 spring cloud stream sample은 [이곳을](https://github.com/LeeKyoungIl/SpringCamp2017) 참고하세요. 
 
## Maven Dependency 추가 
    * Maven
    
```java
<dependency>
  <groupId>com.leekyoungil.illuminati</groupId>
  <artifactId>illuminati-client-elasticsearch</artifactId>
  <version>0.7.5</version>
</dependency>
```

## Gradle Dependency 추가 
    * Gradle
    
```java
compile 'com.leekyoungil.illuminati:illuminati-client-elasticsearch:0.7.5'
```
