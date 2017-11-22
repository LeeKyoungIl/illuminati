# Project illuminati : illuminati-client-jscollector

## illuminati-client-jscollector is client browser side module of illuminati. it's help to collect User event data.
* If you add the module, you can easy to collect User event data from browser. (keyboard press or mouse click)

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
  <groupId>com.leekyoungil.illuminati</groupId>
  <artifactId>illuminati-client-switch</artifactId>
  <version>1.0.2</version>
</dependency>
```

## add to Gradle Dependency
    * Gradle
    
```java
repositories {
    jcenter()
}

compile 'com.leekyoungil.illuminati:illuminati-client-switch:1.0.2'
```

===============================================================================


## illuminati client의 jscollector 모듈입니다.
 * 브라우저상애서 발생하는 event data를 쉽게 수집할수 있습니다. (키보드를 누르거나 마우스를 클릭하여 값을 바꾸는경우)
 
## 브라우저에서부터 시작한 이벤트를 서버를 거쳐 결과가 올때까지 동알한 트랜젝션 아이디로 데이터를 추적할수 있습니다.
 * 'illuminatiUniqueUserId'를 설정할수 있습니다. 해당 값은 유저 단위로 데이터를 확인해야 할때 쉽게 확인할수 있도록 도와줍니다.
 * 'illuminatiGProcId' 는 Global Transaction Id 로 브라우저를 닫거나 특정 시간이 지날때까지 유지하기 때문에 유저가 발생시킨 모든 이벤트를 Web browser 부터 'illuminati processor'와 함께 서버단까지 추적할수 있도록 해줍니다. 해당값은 페이지를 이동해도 변하지 않습니다.
 * 'illuminatiSProcId' 는 Single Transaction Id 로 페이지를 이동하면 값이 변합니다 따라서 페이지 이동전 페이지 내에서 Ajax 요청을 하거나 submit 요청까지 동일한 값을 유지 합니다. 
 * 이 3가지의 값들은 당신의 ajax 요청이나 submit 요청에 같이 포함되어 요청이 되기 때문에 'illuminati-processor'가 자동으로 해당 데이터들을 연결해 줍니다.
     
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
<repositories>
   <repository>
   <id>jcenter</id>
   <url>https://jcenter.bintray.com/</url>
   </repository>
</repositories>

<dependency>
  <groupId>com.leekyoungil.illuminati</groupId>
  <artifactId>illuminati-client-switch</artifactId>
  <version>1.0.1</version>
</dependency>
```

## Gradle Dependency 추가 
    * Gradle
    
```java
repositories {
    jcenter()
}

compile 'com.leekyoungil.illuminati:illuminati-client-switch:1.0.2'
```
