---
title: Java 22
---

{{< jdkdetails "22" >}}
{{< /jdkdetails >}}

## Sandbox

Instantly compile and run Java 22 snippets without a local Java installation.

{{< sandbox version="java22" mainclass="Java22" preview="true" >}}
{{< sandboxsource "Java22.java" >}}
import java.lang.reflect.ClassFileFormatVersion;

public class Java22 {

    public static void main(String[] args) {
    	var v = ClassFileFormatVersion.latest();
        System.out.printf("Hello Java bytecode version %s!", v.major());
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}