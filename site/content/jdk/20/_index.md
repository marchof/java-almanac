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
import javax.lang.model.SourceVersion;

public class Java20 {

    public static void main(String[] args) {
        System.out.printf("Hello Java %s!", SourceVersion.RELEASE_20);
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}