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
    <version>0.5.15</version>
    <scope>compile</scope>
</dependency>
```

## add to Gradle Dependency
    * Gradle
    
```java
repositories {
    jcenter()
}

compile 'me.phoboslabs.illuminati:illuminati-jscollector:0.5.15'
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