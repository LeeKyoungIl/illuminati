# Project illuminati : illuminati-jscollector

## illuminati-jscollector is client browser side module of illuminati. it help to collect User event data.
* If you add the module, you can easy to collect User event data from browser. (keyboard press or mouse click)

## You can be collected a User event data from browser to server, end of results by one transaction id.
 * You can set the 'illuminatiUniqueUserId', this value is easy to check data per user.
 * The illuminatiGProcId is a Global Transaction id. it can be collected event data occured by user with the 'illuminati-processor' from browser to server, end of result. this value is immutable when you move to another page.
 * The 'illuminatiSProcId' is a Single Transaction Id. it change value when move to another page, so it can be retain same value when request Ajax or submit before move on the page.
 * This kind of 3 values are included that your request of Ajax or submit, so the 'illuminati-processor' connect that event data.
     
## 1. add to Maven Dependency 
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
    <artifactId>illuminati-jscollector</artifactId>
    <version>0.5.9</version>
    <scope>compile</scope>
</dependency>
```

## add to Gradle Dependency
    * Gradle
    
```java
repositories {
    jcenter()
}

compile 'me.phoboslabs.illuminati:illuminati-jscollector:0.5.9'
```

## 2. add to Javascript in HTML & initialization
    * javascript

```java
<script src="/js/illuminatiJsAgent.js"></script>
<script type="text/javascript">
    illuminatiJsAgent.init();
</script>   
```   

## Optional features : isAutoCollect : auto collection feature.
 * When you finished by the step2 that you can collect event data of user per 15 second automatically.
 * But if it feel uncomfortable, that it can provide collect event data only once when something request to server (submit or ajax).

## isAutoCollect : auto collection feature.
* javascript

```java
<script src="/js/illuminatiJsAgent.js"></script>
<script type="text/javascript">
    illuminatiJsAgent.setIsAutoCollect(false);
    illuminatiJsAgent.init();
</script>   
```  
 * 'isAutoCollect' value set 'true' then it collect event data of user every 15 second automatically. (default)
 * 'isAutoCollect' value set 'false' then it can provide collect event data only once when something request to server (submit or ajax).
 
## Optional features : illuminatiUniqueUserId (User distinguishable value)
 * If you want to see event data per user, then it set something (id or user number..), you can search data per user.

## illuminatiUniqueUserId sample
* javascript

```java
<script src="/js/illuminatiJsAgent.js"></script>
<script type="text/javascript">
    illuminatiJsAgent.setUniqueUserId('id');
    illuminatiJsAgent.init();
</script>   
```  

## illuminati-jscollector tested browser.
 * Chrome - Mac, Windows 
 * Firefox - Mac, Windows
 * safari - Mac
 * Internet Explorer 11 - Windows 
 * Internet Explorer Edge - Windows 

===============================================================================


## illuminati client의 jscollector 모듈입니다.
 * 브라우저상애서 발생하는 event data를 쉽게 수집할수 있습니다. (키보드를 누르거나 마우스를 클릭하여 값을 바꾸는경우)
 
## 브라우저에서 시작한 유저 이벤트를 서버를 거쳐 특정한 프로세스가 실행 되는동안 동알한 트랜젝션 아이디로 이벤트 데이터를 추적할수 있습니다.
 * 'illuminatiUniqueUserId'를 설정할수 있습니다. 해당 값은 유저 단위로 데이터를 확인해야 할때 쉽게 확인할수 있도록 도와줍니다.
 * 'illuminatiGProcId' 는 Global Transaction Id 로 브라우저를 닫거나 특정 시간이 지날때까지 유지하기 때문에 유저가 발생시킨 모든 이벤트를 Web browser 부터 'illuminati processor'와 함께 서버단까지 추적할수 있도록 해줍니다. 해당값은 페이지를 이동해도 변하지 않습니다.
 * 'illuminatiSProcId' 는 Single Transaction Id 로 페이지를 이동하면 값이 변합니다 따라서 페이지 이동전 페이지 내에서 Ajax 요청을 하거나 submit 요청까지 동일한 값을 유지 합니다. 
 * 이 3가지의 값들은 당신의 ajax 요청이나 submit 요청에 같이 포함되어 요청이 되기 때문에 'illuminati-processor'가 자동으로 해당 데이터들을 연결해 줍니다.
     
## 1. Maven Dependency 추가 
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
    <artifactId>illuminati-jscollector</artifactId>
    <version>0.5.9</version>
    <scope>compile</scope>
</dependency>
```

## Gradle Dependency 추가 
    * Gradle
    
```java
repositories {
    jcenter()
}

compile 'me.phoboslabs.illuminati:illuminati-jscollector:0.5.9'
```

## 2. Javascript 추가 & 초기화
    * javascript

```java
<script src="/js/illuminatiJsAgent.js"></script>
<script type="text/javascript">
    illuminatiJsAgent.init();
</script>   
```   

## 옵션 기능 : isAutoCollect (자동수집 기능)
 * 2번까지 마치면 추가로 어떠한 작업없이 자동으로 illuminati에 유저의 event data 가 매 15초마다 변경점이 있을경우 기록됩니다.
 * 다만 자동수집이 부담스러울경우 서버에 어떠한 (submit, ajax)요청을 할때 같이 event data 가 수집되는 기능을 제공합니다.

## isAutoCollect 설정법
* javascript

```java
<script src="/js/illuminatiJsAgent.js"></script>
<script type="text/javascript">
    illuminatiJsAgent.setIsAutoCollect(false);
    illuminatiJsAgent.init();
</script>   
```  
 * isAutoCollect 값을 true 로 설정하면 15초마다 수집한 event data 가 기록됩니다. (기본값)
 * isAutoCollect 값을 false 로 설정하면 서버에 어떠한 (submit, ajax)요청을 할때 event data 가 기록됩니다.
 
## 옵션 기능 : illuminatiUniqueUserId (유저 구분자 설정 기능)
 * 유져별로 event data 를 보고 싶은경우 해당 값을 설정해 주면 유저별로 데이터를 검색할수 있습니다.

## illuminatiUniqueUserId 설정법
* javascript

```java
<script src="/js/illuminatiJsAgent.js"></script>
<script type="text/javascript">
    illuminatiJsAgent.setUniqueUserId('id');
    illuminatiJsAgent.init();
</script>   
```  

## illuminati-jscollector 테스트가 완료된 브라우저 
 * Chrome - Mac, Windows 
 * Firefox - Mac, Windows
 * safari - Mac
 * Internet Explorer 11 - Windows 
 * Internet Explorer Edge - Windows 
