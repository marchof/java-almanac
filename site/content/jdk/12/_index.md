---
title: Java 12
type: sandbox
---

{{< jdkdetails "12" >}}

The first Java release that included a *preview feature* ([JEP 12](https://openjdk.java.net/jeps/12)).
Such features are only available if the compiler and JVM is launched with the
`--enable-preview` option. Previews allow to get early feedback on important changes in the Java language.

{{< /jdkdetails >}}


## Sandbox

Instantly compile and run Java 12 snippets without a local Java installation.

{{< sandbox version="java12" mainclass="Java12" >}}
{{< sandboxsource "Java12.java" >}}
import java.util.function.Function;

public class Java12 {

    public static void main(String[] args) {
        
        Function<String, String> hello = s -> String.format("Hello %s!", s);
        System.out.println("Java 12".transform(hello));
        
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

