---
title: Java 12
type: sandbox
---

{{< jdkdetails "12" >}}

## New Features

### JVM

* Shenandoah: A Low-Pause-Time Garbage Collector (Experimental, [JEP 189](http://openjdk.java.net/jeps/189))

### Language

* Switch Expressions (Preview, [JEP 325](http://openjdk.java.net/jeps/325))

### Library

* JVM Constants API ([JEP 334](http://openjdk.java.net/jeps/334))


# Sandbox

Instantly compile and run Java 12 snippets without a local Java installation.

{{< sandbox version="java12" mainclass="Java12" >}}
{{< sandboxsource "Java12.java" >}}
import java.util.function.Function;

public class Java12 {

    public static void main(String[] args) {
        
        Function<String, String> hello = s -> String.format("Hello %s!", s);
        System.out.println("Java 12".transform(hello));
        
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

