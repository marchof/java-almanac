---
title: Java 21
description: Information about Java 21 including documentation links, new APIs, added features and download options.
---

{{< jdkdetails "21" >}}

Java 21 is a current long term support (LTS) release.

{{< /jdkdetails >}}

## Sandbox

Instantly compile and run Java 21 snippets without a local Java installation.

{{< sandbox version="java21" mainclass="Java21" preview="true" >}}
{{< sandboxsource "Java21.java" >}}
import java.lang.reflect.ClassFileFormatVersion;

public class Java21 {

    public static void main(String[] args) {
    	var v = ClassFileFormatVersion.latest();
        System.out.printf("Hello Java bytecode version %s!", v.major());
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}