# Project illuminati : illuminati-switch

## illuminati-switch is module of illuminati. it's helps Illuminati to be easy to use.
* If you add the module, you can turn it on and off without deploying it.

## you can turn ON or OFF illuminati by reading the configuration file you uploaded on the web.
* [click](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati-config-properties) Here is a sample configuration of illuminati switch.
* extension does not matter.
* 'illuminatiSwitchValue' key must be required.
* 'illuminatiSwitchValue' value must be true or false.
    * true : turn ON illuminati
    * false : turn OFF illuminati

## there is a rule of illuminati-switch.
* [click](https://github.com/LeeKyoungIl/illuminati/blob/master/ApiServerSample/src/main/resources/illuminati-local.yml) Here is a sample configuration of illuminati configuration file.
* need to add 'illuminatiSwitchValueURL', 'illuminatiSwitchValueURLCheckInterval' in illuminati-{phase}.yml or properties
* example 
```java
illuminatiSwitchValueURL: {config file uploaded http path}
illuminatiSwitchValueURLCheckInterval: {update cycle (ms)}

illuminatiSwitchValueURL: https://raw.githubusercontent.com/LeeKyoungIl/illuminati/feature/with_spring_cloud_config/illuminati-config-properties/illuminati-switch-local.yml
illuminatiSwitchValueURLCheckInterval: 5000
```

## add to Maven Dependency
    * Maven
    
```java
<repositories>
   <repository>
   <id>jcenter</id>
   <url>https://jcenter.bintray.com/</url>
   </repository>
</repositories>

<dependency>
  <groupId>me.phoboslabs.illuminati</groupId>
  <artifactId>illuminati-switch</artifactId>
  <version>1.0.13</version>
</dependency>
```

## add to Gradle Dependency
    * Gradle
    
```java
repositories {
    jcenter()
}

compile 'me.phoboslabs.illuminati:illuminati-switch:1.0.13'
```