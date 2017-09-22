# Project illuminati : illuminati-client-switch

## illuminati-client-switch is module of illuminati. it's helps Illuminati to be easy to use.
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
<dependency>
  <groupId>com.leekyoungil.illuminati</groupId>
  <artifactId>illuminati-client-switch</artifactId>
  <version>1.0.0</version>
</dependency>
```

## add to Gradle Dependency
    * Gradle
    
```java
compile 'com.leekyoungil.illuminati:illuminati-client-switch:1.0.0'
```

===============================================================================


## illuminati client의 switch 모듈입니다.
 * illuminati를 다시 배포할 필요없이 쉽게 켜고 끌수 있습니다.
 
## illuminati 설정파일을 웹에 업로드해두고 사용할수 있습니다.
 * [이곳](https://github.com/LeeKyoungIl/illuminati/tree/master/illuminati-config-properties)에는 illuminati-swith의 설정파일 sample이 있습니다.
 * 확장자는 어떤것을 사용하던 상관이 없습니다.
 * 'illuminatiSwitchValue' key값은 꼭 필요합니다.
 * 'illuminatiSwitchValue'의 값은 true나 false여야 합니다.
    * true : illuminati를 사용합니다.
    * false : illuminati를 끕니다.
    
## illuminati-switch를 사용하기 위한 규칙이 있습니다.
* [이곳](https://github.com/LeeKyoungIl/illuminati/blob/master/ApiServerSample/src/main/resources/illuminati-local.yml) 이곳에 설정파일 sample이 있습니다.
* 'illuminatiSwitchValueURL', 'illuminatiSwitchValueURLCheckInterval' 키값을 설정파일에 추가해야 합니다. (illuminati-{phase}.yml or properties)
* example 
```java
illuminatiSwitchValueURL: {웹에 올려둔 설정파일의 http 패스}
illuminatiSwitchValueURLCheckInterval: {갱신주기 (ms)}

illuminatiSwitchValueURL: https://raw.githubusercontent.com/LeeKyoungIl/illuminati/feature/with_spring_cloud_config/illuminati-config-properties/illuminati-switch-local.yml
illuminatiSwitchValueURLCheckInterval: 5000
``` 
 
## Maven Dependency 추가 
    * Maven
    
```java
<dependency>
  <groupId>com.leekyoungil.illuminati</groupId>
  <artifactId>illuminati-client-switch</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Gradle Dependency 추가 
    * Gradle
    
```java
compile 'com.leekyoungil.illuminati:illuminati-client-switch:1.0.0'
```
