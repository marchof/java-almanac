---
title: Java 13
type: sandbox
---

{{< jdkdetails "13" >}}

## New Features

### JVM

* Dynamic CDS Archives ([JEP 350](http://openjdk.java.net/jeps/350))
* ZGC: Uncommit Unused Memory ([JEP 351](http://openjdk.java.net/jeps/351))

### Language

* Switch Expressions (Preview) ([JEP 354](http://openjdk.java.net/jeps/354))
* Text Blocks (Preview) ([JEP 355](http://openjdk.java.net/jeps/355))

### Library

* Reimplement the Legacy Socket API ([JEP 353](http://openjdk.java.net/jeps/353))


## Sandbox

Instantly compile and run Java 13 snippets without a local Java installation.

{{< sandbox version="java13" mainclass="Java13" preview="false" >}}
{{< sandboxsource "Java13.java" >}}
import java.nio.CharBuffer;

public class Java13 {
    
    public static void main(String[] args) {
        CharBuffer buffer = CharBuffer.allocate(20);
        buffer.put("What? Hello Universe");

        CharBuffer slice = buffer.slice(6, 14);

        buffer.position(12);
        buffer.put("Java 13!");

        System.out.println(slice);
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

