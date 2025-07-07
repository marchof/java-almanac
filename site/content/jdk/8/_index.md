---
title: Java 8
description: Information about Java 8 including documentation links, new APIs, added features and download options.
---

{{< jdkdetails "8" >}}

Java 8 finally added *lambda* syntax and *method references* as a modern language
constructs. The main application within the JDK API is the new `java.util.streams`
package. An initial set of common functional interfaces is shipped in the new
package `java.util.function`.

The extremely useful [Joda-Time](https://www.joda.org/joda-time/) library has been
integrated into the JDK as a new package `java.time`. Instead of the mostly deprecated
`java.util.Date` and `java.util.Calendar` classes we now have a modern and powerful API
for calculations with dates and times.

Also Oracle started to bundle JavaFX in its JDK distribution (which was removed
again in [Java 11](../11/)).

The contents of Java 8 is specified in the umbrella [JSR 337](https://jcp.org/en/jsr/detail?id=337).

{{< /jdkdetails >}}


## Sandbox

Instantly compile and run Java 8 snippets without a local Java installation.

{{< sandbox version="java8" mainclass="Java8" >}}
{{< sandboxsource "Java8.java" >}}
import java.util.stream.Stream;

public class Java8 {

    public static void main(String[] args) {

        Stream.of("Hello", "Java", "8").map(String::toUpperCase).forEach(System.out::println);

    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}
