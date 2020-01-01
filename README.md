# Project illuminati

![image](https://github.com/LeeKyoungIl/illuminati/blob/master/illuminati-logo.png)

# This is a Platform that collects all the data accruing in your Application and shows the data in real time by using Kibana or other tools.

# illuminati's intention to develop

**There is no garbage data in your Application.**
It is necessary to identify what data is the most important among the collecting data from your Applcation.
Also collect and analysis must be performed in different processes. 
The illuminati is desinged to make collect all data easily and it can be possible scalability working by separated analysis process.

## required
 * Java8 or higher.
 * Message queue (RabbitMQ or Kafka)
 * Java Application that can use AspectJ

## recommendations
 * ElasticSearch (5.x or higher) 
 * Hadoop (3.x or higher)
 * Kibana
 * Spring Cloud Stream - used to create a consumer application

## struct of illuminati Project

 - library     
     * [illuminati-annotation:1.2.2](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati/illuminati-annotation)
     * [illuminati-common:1.4.6](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati/illuminati-common)
     * [illuminati-processor:0.9.9.29](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati/illuminati-processor)
     * [illuminati-switch:1.0.14](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati/illuminati-switch)
     * [illuminati-jscollector:0.5.15](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati/illuminati-jscollector)
     * [illuminati-elasticsearch:0.8.8](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati/illuminati-elasticsearch)
     * [illuminati-hdfs:0.1.6](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati/illuminati-hdfs)
 
 - util
     * [illuminati-util](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati/illuminati-util)
          * [illuminati-levenshtein:1.1.3](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati/illuminati-util/illuminati-levenshtein)

 - sample
     * [ApiSampleApplication](https://github.com/LeeKyoungIl/illuminati/tree/master/ApiServerSample)
     * [illuminati-consumer-es-sample](https://github.com/LeeKyoungIl/illuminati/tree/feature/es_sample_readme/illuminati-consumer-es-sample)

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
 4. It can collect Event data from Browser to Server to Response by One Transaction Id.
    
## illuminati is easy to use
 1. Do not need to create a data type. (No DTO required)
 2. Agent installation is not required.
 3. Annotation type is easy to apply.
 4. Internal method request like the private method can't collect data. For example, if you call the a2 method of class A from the a1 method of class A, you cannot collect data.
 
## illuminati operator method
 1. Add dependency of MAVEN or Gradle (illuminati)
 2. Add configuration in illuminati-{**phase**}.yml, properties에 (address of queue... etc)
 3. When execute application with add -Dspring.profiles.active={**phase**}
 4. Add "@Illuminat" Annotation to where you want to collect
 5. Add to Html in **<script></script>** in **illuminatiJsAgent.min.js** script file.

## illuminati does not affect the origin application logic
 1. It was developed to have no influence on this logic by using a separate thread and Buffer.
 2. Drop in performance can occur, but there is no big difference in physical server. (It can happen a little more on virtual machines.)
 3. Even if an exception occurs in the original application logic, illuminati can also collect the corresponding exception information.
 4. When the Illuminati collects the Data. If a problems aries. Save that data to separate  storage. and If the problem is fixed. data will automaticaly restored. (Backup function)
 5. Grace Shutdown mode is supported. (When Backup function is activated.) 
 
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
      <groupId>me.phoboslabs.illuminati</groupId>
      <artifactId>illuminati-annotation</artifactId>
      <version>1.2.2</version>
   </dependency>

   <dependency>
      <groupId>me.phoboslabs.illuminati</groupId>
      <artifactId>illuminati-processor</artifactId>
      <version>0.9.9.29</version>
   </dependency>
   
   <!-- This is an option. If you add the module, you can turn it on and off without deploying it. -->
   <dependency>
       <groupId>me.phoboslabs.illuminati</groupId>
       <artifactId>illuminati-switch</artifactId>
       <version>1.0.14</version>
   </dependency>

   <!-- This is an option. If you add the module, you can collect Event data from Browser to server to response by one transaction id. -->
   <dependency>
      <groupId>me.phoboslabs.illuminati</groupId>
      <artifactId>illuminati-jscollector</artifactId>
      <version>0.5.15</version>
   </dependency>
</dependencies>
```

## add to Gradle Dependency 
    * Gradle
    
```java
repositories {
    jcenter()
}

compile 'me.phoboslabs.illuminati:illuminati-annotation:1.2.2'
compile 'me.phoboslabs.illuminati:illuminati-processor:0.9.9.29'
// This is an option. If you add the module, you can turn it on and off without deploying it.
compile 'me.phoboslabs.illuminati:illuminati-switch:1.0.14'
<!-- This is an option. If you add the module, you can collect Event data from Browser to server to response by one transaction id. -->
compile 'me.phoboslabs.illuminati:illuminati-jscollector:0.5.15'
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

## (Optional) add to Javascript in HTML & initialization
    * javascript

```java
<script src="/js/illuminatiJsAgent.js"></script>
<script type="text/javascript">
    illuminatiJsAgent.init();
</script>   
```   

## add illuminati consumer
 1. Easily add Consumer using Spring Cloud Stream
 2. Consumer can transfer data. (ElasticSearch, MongoDB, MySQL, Hadoop, etc.)
    * Multiple consumers can receive the same Event data.
    * It is easy to increase throughput by dividing data from many consumers.

## Illuminati data can be used in Kibana
 * Sample of Commerce Data.

![image](https://github.com/LeeKyoungIl/illuminati/blob/master/kibana-sample.png)