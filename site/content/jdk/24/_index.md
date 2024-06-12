---
title: Java 24
description: Information about Java 24 including documentation links, new APIs, added features and download options.
---

{{< jdkdetails "24" >}}
{{< /jdkdetails >}}


## Sandbox

Instantly compile and run Java 24 snippets without a local Java installation.

{{< sandbox version="java24" mainclass="Java24" preview="true" >}}
{{< sandboxsource "Java24.java" >}}
import java.lang.reflect.ClassFileFormatVersion;

public class Java24 {

    public static void main(String[] args) {
    	var v = ClassFileFormatVersion.latest();
        System.out.printf("Hello Java bytecode version %s!", v.major());
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}