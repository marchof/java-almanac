---
title: Java 11
description: Information about Java 11 including documentation links, new APIs, added features and download options.
---

{{< jdkdetails "11" >}}

Java 11 is the first *Long Term Support* (LTS) release with the new release schedule.

{{< /jdkdetails >}}


## Sandbox

Instantly compile and run Java 11 snippets without a local Java installation.

{{< sandbox version="java11" mainclass="Java11" >}}
{{< sandboxsource "Java11.java" >}}
import java.util.function.Function;

public class Java11 {

    public static void main(String[] args) {

        Function<String, String> hello = (var s) -> String.format("Hello %s!", s);
        System.out.println(hello.apply("Java 11"));

    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

