---
title: Java 11
type: sandbox
---

{{< jdkdetails "11" >}}

## New Features

### JVM

* `CONSTANT_Dynamic` ([JEP 309](http://openjdk.java.net/jeps/309))
* Epsilon GC ([JEP 318](http://openjdk.java.net/jeps/318))
* ZGC ([JEP 333](http://openjdk.java.net/jeps/333))
* Nest-Based Access Control ([JEP 181](http://openjdk.java.net/jeps/181))
* Low-Overhead Heap Profiling ([JEP 331](http://openjdk.java.net/jeps/331))

### Language

* Local Variable Syntax for Lambda Parameters ([JEP 323](http://openjdk.java.net/jeps/323))

### Library

* HTTP Client ([JEP 321](http://openjdk.java.net/jeps/321))
* Java EE and CORBA removed ([JEP 320](http://openjdk.java.net/jeps/320))
* Unicode 10 Support ([JEP 327](http://openjdk.java.net/jeps/327))
* Nashorn JavaScript Engine deprecated ([JEP 335](http://openjdk.java.net/jeps/335))
* New Cryptographic Algorithms ([JEP 324](http://openjdk.java.net/jeps/324), [JEP 329](http://openjdk.java.net/jeps/329))
* TLS 1.3 ([JEP 332](http://openjdk.java.net/jeps/332))

### Tools

* Single Source File Launch ([JEP 330](http://openjdk.java.net/jeps/330))
* Flight Recorder ([JEP 328](http://openjdk.java.net/jeps/328))
* Improve Aarch64 Intrinsics ([JEP 315](http://openjdk.java.net/jeps/315))
* Pack200 deprecated ([JEP 336](http://openjdk.java.net/jeps/336))
* No more frames in JavaDoc ([JDK-8196202](https://bugs.openjdk.java.net/browse/JDK-8196202))


## Sandbox

Instantly compile and run Java 11 snippets without a local Java installation.

{{< sandbox version="java11" mainclass="Java11" >}}
{{< sandboxsource "Java11.java" >}}
import java.util.function.Function;

public class Java11 {

    public static void main(String[] args) {
        
        Function<String, String> hello = (var s) -> String.format("Hello %s!", s);
        System.out.println(hello.apply("Java 11"));
        
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

