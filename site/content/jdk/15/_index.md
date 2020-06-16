---
title: Java 15
type: sandbox
---

{{< jdkdetails "15" >}}

## New Features

### JVM

* ZGC: A Scalable Low-Latency Garbage Collector ([JEP 377](http://openjdk.java.net/jeps/377))

### Language

* Pattern Matching for instanceof (Second Preview) ([JEP 375](http://openjdk.java.net/jeps/375))
* Text Blocks ([JEP 378](http://openjdk.java.net/jeps/378))
* Records (Second Preview) ([JEP 384](http://openjdk.java.net/jeps/384))

### Library

* Hidden Classes ([JEP 371](http://openjdk.java.net/jeps/371))
* Remove the Nashorn JavaScript Engine ([JEP 372](http://openjdk.java.net/jeps/372))

### Tools



## Sandbox

Instantly compile and run Java 15 snippets without a local Java installation.

{{< sandbox version="java15" mainclass="Java15" preview="true" >}}
{{< sandboxsource "Java15.java" >}}
public class Java15 {
    
    final static String helloTemplate = """
        Hello Java
        ==========
        
        %s""";
    
    record Platform(String name, int release) {
    }

    public static void main(String[] args) {
        System.out.printf(helloTemplate, new Platform("Java", 15));
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

