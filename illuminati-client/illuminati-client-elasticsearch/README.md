# Project illuminati : illuminati-client-elasticsearch

## illuminati-client-elasticsearch is module of illuminati. it's helps ElasticSearch to be easy to use.
* Data collected by illuminati can be easily analyzed using kibana.
* Because kibana uses elasticsearch, illuminati also needs to send data to elasticsearch.

## it should be applied to the consumer module that receives and processes the data collected by illuminati.
* [click](https://github.com/LeeKyoungIl/SpringCamp2017) Here is a sample project of Consumer Module using Spring Cloud Stream and kafka.

## add to Maven Dependency
    * Maven
    
```java
<dependency>
  <groupId>com.leekyoungil.illuminati</groupId>
  <artifactId>illuminati-client-elasticsearch</artifactId>
  <version>0.6.9</version>
</dependency>
```

## add to Gradle Dependency
    * Gradle
    
```java
compile 'com.leekyoungil.illuminati:illuminati-client-elasticsearch:0.6.9'
```

===============================================================================


## illuminati client의 elasticsearch 모듈입니다.
 * illuminati에서 수집한 데이터는 kibana를 이용해서 쉽게 분석이 가능합니다.
 * kibana는 elasticsearch를 사용하기 때문에 illuminati도 elasticsearch에 데이터를 보내줘야 합니다.
 
## It should be applied to the consumer module that receives and processes the data collected by illuminati.
 * [이곳](https://github.com/LeeKyoungIl/SpringCamp2017)에는 Spring Cloud Stream을 이용한 Consumer 모듈 Sample 프로젝트가 있으니 사용법을 참고하시면 됩니다.
 
## Maven Dependency 추가 
    * Maven
    
```java
<dependency>
  <groupId>com.leekyoungil.illuminati</groupId>
  <artifactId>illuminati-client-elasticsearch</artifactId>
  <version>0.6.9</version>
</dependency>
```

## Gradle Dependency 추가 
    * Gradle
    
```java
compile 'com.leekyoungil.illuminati:illuminati-client-elasticsearch:0.6.9'
```
