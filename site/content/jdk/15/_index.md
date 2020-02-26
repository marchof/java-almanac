---
title: Java 15
type: sandbox
---

{{< jdkdetails "15" >}}

## New Features

### JVM


### Language


### Library


### Tools



# Sandbox

Instantly compile and run Java 15 snippets without a local Java installation.

{{< sandbox version="java15" mainclass="Java15" preview="true" >}}
{{< sandboxsource "Java15.java" >}}
import java.util.function.Function;

public class Java15 {
    
    record Platform(String name, int release) {
        
    }

    public static void main(String[] args) {
        System.out.printf("Hello %s!", new Platform("Java", 15));
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

