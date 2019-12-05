# Project illuminati : illuminati-processor

# illuminati-processor is core module of illuminati

# supported message queues
 * RabbitMQ
 * Kafka

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
      <version>1.2.1</version>
    </dependency>
    
    <dependency>
      <groupId>me.phoboslabs.illuminati</groupId>
      <artifactId>illuminati-processor</artifactId>
      <version>0.9.9.18</version>
    </dependency>
    
    <!-- This is an option. If you add the module, you can turn it on and off without deploying it. -->
    <dependency>
       <groupId>me.phoboslabs.illuminati</groupId>
       <artifactId>illuminati-switch</artifactId>
       <version>1.0.10</version>
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

compile 'me.phoboslabs.illuminati:illuminati-annotation:1.2.1'
compile 'me.phoboslabs.illuminati:illuminati-processor:0.9.9.18'
// This is an option. If you add the module, you can turn it on and off without deploying it.
compile 'me.phoboslabs.illuminati:illuminati-switch:1.0.10'
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
 * so you do exclude the illuminati-processor in pom.xml or build.gradle. it will be solved easily.
 * must clean and compile re deploy.