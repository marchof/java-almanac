---
title: Java 28
description: Information about Java 28 including documentation links, new APIs, added features and download options.
---

{{< jdkdetails "28" >}}
{{< /jdkdetails >}}

## Sandbox

Instantly compile and run Java 28 snippets without a local Java installation.

{{< sandbox version="java28" mainsource="Java28.java" preview="true" >}}
{{< sandboxsource "Java28.java" >}}
import module java.base;

void main() {
    var v = ClassFileFormatVersion.latest();
    IO.println("Hello Java bytecode version %s!".formatted(v.major()));
}

{{< /sandboxsource >}}
{{< /sandbox >}}