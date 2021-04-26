---
title: Java 15
type: sandbox
---

{{< jdkdetails "15" >}}

{{< /jdkdetails >}}

## Sandbox

Instantly compile and run Java 15 snippets without a local Java installation.

{{< sandbox version="java15" mainclass="Java15" preview="true" >}}
{{< sandboxsource "Java15.java" >}}
public class Java15 {
    
    final static String helloTemplate = """
        +===========+
        |   Hello   |
        +===========+
        |  Java %s  |
        +===========+""";

    public static void main(String[] args) {
        System.out.printf(helloTemplate, 15);
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

