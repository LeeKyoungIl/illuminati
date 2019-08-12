# Project illuminati algorithm : levenshtein distance

## What is the Levenshtein distance?
The Levenshtein Algorithm compare A and B string.
Returns 0(zero) If both are equal and Integer value if both are different.
The Integer value is the number of times you need to modify the two values to ve the equal.
Of course, the smaller the value is similar.

## What is different from another algorithm library?
This algorithm library Added flag for case sensitive identify.

## How to use?
Very Simple. 

```java
String targetStr = "Illuminati";
String compareStr = "illuninati";
boolean ignoreCase = false;
int result = Levenshtein.getInstance().distance(targetStr, compareStr, ignoreCase);

System.out.println("result is " : result); // result is 2
```

```java
String targetStr = "Illuminati";
String compareStr = "illuninati";
boolean ignoreCase = true;
int result = Levenshtein.getInstance().distance(targetStr, compareStr, ignoreCase);

System.out.println("result is " : result); // result is 1
```

```java
String targetStr = "illuminati";
String compareStr = "illuminati";
boolean ignoreCase = true;
int result = Levenshtein.getInstance().distance(targetStr, compareStr, ignoreCase);

System.out.println("result is " : result); // result is 0
```

## add to Maven Dependency
    * Maven
    
```java
<dependency>
  <groupId>me.phoboslabs.illuminati</groupId>
  <artifactId>illuminati-client-annotation</artifactId>
  <version>1.2.1</version>
</dependency>

<dependency>
  <groupId>me.phoboslabs.illuminati</groupId>
  <artifactId>illuminati-client-processor</artifactId>
  <version>0.9.9.7</version>
</dependency>
```

## add to Gradle Dependency
    * Gradle
    
```java
compile 'me.phoboslabs.illuminati:illuminati-client-annotation:1.2.1'
compile 'me.phoboslabs.illuminati:illuminati-client-processor:0.9.9.7'
```