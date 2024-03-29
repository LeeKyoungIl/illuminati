# Project illuminati : illuminati-elasticsearch

## illuminati-elasticsearch is module of illuminati. it's helps ElasticSearch to be easy to use.
* Data collected by illuminati can be easily analyzed using kibana.
* Because kibana uses elasticsearch, illuminati also needs to send data to elasticsearch.

## it should be applied to the consumer module that receives and processes the data collected by illuminati.
* [click](https://github.com/LeeKyoungIl/SpringCamp2017) Here is a sample project of Consumer Module using Spring Cloud Stream and kafka.

## add to Maven Dependency
    * Maven
    
```java
<dependency>
  <groupId>me.phoboslabs.illuminati</groupId>
  <artifactId>illuminati-elasticsearch</artifactId>
  <version>0.8.14</version>
</dependency>
```

## add to Gradle Dependency
    * Gradle
    
```java
compile 'me.phoboslabs.illuminati:illuminati-elasticsearch:0.8.14'
```

## add config file - over version 0.8.14
Add config file to the Resources folder.<br>
However, you need to create an elasticsearch folder in the config folder in the the Resources folder.

```yml
elasticsearchInfo:
 user: // If you haven't set up authentication, 
 pass: // you can leave this field blank.
 host: localhost // host address
 port: 9200 // host port
```