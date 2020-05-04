# Project illuminati : illuminati-hdfs

## illuminati-hdfs is helping in saving data to HDFS.
* This module provides a must-have function of data saving to HDFS.

## functions.
 * addFile
 * readFile
 * deleteFile
 * mkdir
     
## add to Maven Dependency 
    * Maven
    
```java

<dependency>
    <groupId>me.phoboslabs.illuminati</groupId>
    <artifactId>illuminati-hdfs</artifactId>
    <version>0.1.15</version>
    <scope>compile</scope>
</dependency>
```

## add to Gradle Dependency
    * Gradle
    
```java

compile 'me.phoboslabs.illuminati:illuminati-hdfs:0.1.15'
```

## Example

Add properties to '/resources/hdfs/connection-local.properties'
 
```properties
hdfs.connection.uri: your HDFS namenode address
hdfs.connection.port: your HDFS namenode port
hdfs.connection.authentication: SIMPLE
hdfs.connection.authorization: FALSE
hdfs.connection.user: hdfs
hdfs.connection.home: /
hdfs.connection.timeout: 6000
hdfs.connection.dfsSupportAppend: true
``` 

You should be made of the Spring Bean or Singleton.
Example of Make the Spring Bean.

```java
@Bean
public HDFSDataBroker hdfsDataBroker() {
    StringBuilder filePath = new StringBuilder("hdfs/connection-")
                                    .append("local")
                                    .append(".properties");

    return new HDFSDataBroker(PropertiesUtil.getPropertiesFromFile(filePath.toString()));
}
```