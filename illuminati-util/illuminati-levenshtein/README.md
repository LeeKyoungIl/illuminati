# Project illuminati util : levenshtein distance

## What is the Levenshtein distance?
The Levenshtein Algorithm compare A and B string.
Returns 0(zero) If both are equal and Integer value if both are different.
The Integer value is the number of times you need to modify the two values to ve the equal.
Of course, the smaller the value is similar.

## What is different from another algorithm library?
This algorithm library added flag for case sensitive identify.<br>
And I tried to make it using only one the for loop syntax.

## See here for a description of Levenshtein distance algorithm

[About_Levenshtein_algorithm](https://leekyoungil.github.io/blog/2019/08/13/About_Levenshtein_algorithm.html)

## How to use?
Very Simple. 

```java
String targetStr = "Illuminati";
String compareStr = "illuninati";
boolean ignoreCase = false;
int result = Levenshtein.getInstance().distance(targetStr, compareStr, ignoreCase);

System.out.println("result is " + result); // result is 2
```

```java
String targetStr = "Illuminati";
String compareStr = "illuninati";
boolean ignoreCase = true;
int result = Levenshtein.getInstance().distance(targetStr, compareStr, ignoreCase);

System.out.println("result is " + result); // result is 1
```

```java
String targetStr = "illuminati";
String compareStr = "illuminati";
boolean ignoreCase = true;
int result = Levenshtein.getInstance().distance(targetStr, compareStr, ignoreCase);

System.out.println("result is " + result); // result is 0
```

## add to Maven Dependency
    * Maven
    
```java
<dependency>
  <groupId>me.phoboslabs.illuminati</groupId>
  <artifactId>illuminati-levenshtein</artifactId>
  <version>1.0.0</version>
</dependency>
```

## add to Gradle Dependency
    * Gradle
    
```java
compile 'me.phoboslabs.illuminati:illuminati-levenshtein:1.0.0'
```
