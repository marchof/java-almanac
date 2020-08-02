---
title: Java 12
type: sandbox
---

{{< jdkdetails "12" >}}

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

