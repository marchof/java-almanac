---
title: Java 15
type: sandbox
---

{{< jdkdetails "15" >}}

## New Features

### JVM

* Disable and Deprecate Biased Locking ([JEP 374](http://openjdk.java.net/jeps/374))
* ZGC: A Scalable Low-Latency Garbage Collector ([JEP 377](http://openjdk.java.net/jeps/377))
* Shenandoah: A Low-Pause-Time Garbage Collector ([JEP 379](http://openjdk.java.net/jeps/379))
* Remove the Solaris and SPARC Ports ([JEP 381](http://openjdk.java.net/jeps/381))

### Language

* Pattern Matching for instanceof (Second Preview) ([JEP 375](http://openjdk.java.net/jeps/375))
* Text Blocks ([JEP 378](http://openjdk.java.net/jeps/378))
* Records (Second Preview) ([JEP 384](http://openjdk.java.net/jeps/384))

### Library

* Edwards-Curve Digital Signature Algorithm (EdDSA) ([JEP 339](http://openjdk.java.net/jeps/339))
* Sealed Classes (Preview) ([JEP 360](http://openjdk.java.net/jeps/360))
* Hidden Classes ([JEP 371](http://openjdk.java.net/jeps/371))
* Remove the Nashorn JavaScript Engine ([JEP 372](http://openjdk.java.net/jeps/372))
* Reimplement the Legacy DatagramSocket API ([JEP 373](http://openjdk.java.net/jeps/373))
* Foreign-Memory Access API (Second Incubator) ([JEP 383](http://openjdk.java.net/jeps/383))
* Deprecate RMI Activation for Removal ([JEP 385](http://openjdk.java.net/jeps/385))


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

