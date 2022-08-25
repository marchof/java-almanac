---
title: Java 20
type: sandbox
---

{{< jdkdetails "20" >}}
{{< /jdkdetails >}}

## Sandbox

Instantly compile and run Java 20 snippets without a local Java installation.

{{< sandbox version="java20" mainclass="Java20" preview="true" >}}
{{< sandboxsource "Java20.java" >}}
import java.lang.reflect.ClassFileFormatVersion;

public class Java20 {

    public static void main(String[] args) {
    	var v = ClassFileFormatVersion.latest();
        System.out.printf("Hello Java bytecode version %s!", v.major());
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}