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
value class X {
    final int i;
    X(int i) { this.i = i; }
}

void main() {
    var x1 = new X(5);
    var x2 = new X(5);
    var x3 = new X(7);
    
    System.out.println(X.class.isValue());
    System.out.println(x1 == x2);
    System.out.println(x1 == x3);
}
{{< /sandboxsource >}}
{{< /sandbox >}}