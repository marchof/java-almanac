---
title: Java 10
type: sandbox
---

{{< jdkdetails "10" >}}

{{< /jdkdetails >}}


## Sandbox

Instantly compile and run Java 10 snippets without a local Java installation.

{{< sandbox version="java10" mainclass="Java10" >}}
{{< sandboxsource "Java10.java" >}}
public class Java10 {

    public static void main(String[] args) {

        var version = Runtime.version();
        System.out.printf("Hello Java %s!", version.feature());

    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}
