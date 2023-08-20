---
title: Java 20
description: Information about Java 20 including documentation links, new APIs, added features and download options.
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