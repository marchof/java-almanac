---
title: Java 25
description: Information about Java 25 including documentation links, new APIs, added features and download options.
---

{{< jdkdetails "25" >}}

Java 25 is the latest long term support (LTS) release after [Java 21](../21).

{{< /jdkdetails >}}


## Sandbox

Instantly compile and run Java 25 snippets without a local Java installation.

{{< sandbox version="java25" mainsource="Java25.java" preview="true" >}}
{{< sandboxsource "Java25.java" >}}
import module java.base;

void main() {
    var v = ClassFileFormatVersion.latest();
    IO.println("Hello Java bytecode version %s!".formatted(v.major()));
}

{{< /sandboxsource >}}
{{< /sandbox >}}
