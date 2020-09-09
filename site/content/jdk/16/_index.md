---
title: Java 16
type: sandbox
---

{{< jdkdetails "16" >}}

{{< /jdkdetails >}}

## Sandbox

Instantly compile and run Java 16 snippets without a local Java installation.

{{< sandbox version="java16" mainclass="Java16" preview="true" >}}
{{< sandboxsource "Java16.java" >}}
import javax.lang.model.SourceVersion;

public class Java16 {
    
    public static void main(String[] args) {
        System.out.printf("Hello %s!", SourceVersion.RELEASE_16);
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

