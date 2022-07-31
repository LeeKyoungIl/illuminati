# Project illuminati : illuminati-mongo

## illuminati-mongo is helping in saving data to mongodb.

* This module provides a must-have function of data saving to mongodb.

## functions.

## add to Maven Dependency

    * Maven

```java

<dependency>
    <groupId>me.phoboslabs.illuminati</groupId>
    <artifactId>illuminati-mongo</artifactId>
    <version>0.1</version>
    <scope>compile</scope>
</dependency>
```

## add to Gradle Dependency

    * Gradle

```java

compile 'me.phoboslabs.illuminati:illuminati-mongo:0.1'
```

## Example

Add properties to '/resources/hdfs/connection-local.properties'

```properties
mongo.connection.uri: your mongdb address
mongo.connection.port: your mongdb port
``` 

You should be made of the Spring Bean or Singleton.
Example of Make the Spring Bean.

```java
@Bean
}
```