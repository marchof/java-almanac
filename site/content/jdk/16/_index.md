---
title: Java 16
---

{{< jdkdetails "16" >}}

The OpenJDK source tree has moved from Mercurial to Git and is now hosted on
[GitHub](https://github.com/openjdk/jdk). The migration is described in
[JEP 369](https://openjdk.java.net/jeps/369).

{{< /jdkdetails >}}

## Sandbox

Instantly compile and run Java 16 snippets without a local Java installation.

{{< sandbox version="java16" mainclass="Java16" preview="true" >}}
{{< sandboxsource "Java16.java" >}}
public class Java16 {

    record Lang(String name, int version) {}

    public static void main(String[] args) {
        var java = new Lang("Java", 16);
        System.out.printf("Hello %s!", java);
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

