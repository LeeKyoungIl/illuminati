# Project illuminati util : object util

# What is the object util?
This is a project that made necessary functions while developing.

 - nullSafeEquals

### nullSafeEquals
It is null-safe when making comparisons in conditional statements.

#### How to use?

For example

```java
class Bar {
    String testBarString = "test";
}

class Foo {
    Bar bar = new Bar();
}

Foo foo = new Foo();

if ( foo.getBar() != null && "test".equals(foo.getBar().getTestBarString()) ) {
    // TODO
}
```

In the past, when comparing objects, We had to do the Null check firstly.
So I make simply function.

```java
// AS-IS
if ( foo.getBar() != null && "test".equals(foo.getBar().getTestBarString()) ) {
    // TODO
}

// TO-BE
if ( ObjectUtil.nullSafeEquals(foo, "bar.testBarString", "test') ) {
    // TODO
}
```

## add to Maven Dependency
    * Maven
    
```java
<dependency>
  <groupId>me.phoboslabs.illuminati</groupId>
  <artifactId>illuminati-objectutil</artifactId>
  <version>1.0.0</version>
</dependency>
```

## add to Gradle Dependency
    * Gradle
    
```java
compile 'me.phoboslabs.illuminati:illuminati-objectutil:1.0.0'
```