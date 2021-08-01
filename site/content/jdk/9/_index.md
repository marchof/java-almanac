---
title: Java 9
type: sandbox
---

{{< jdkdetails "9" >}}

In quite a opinionated move Java got its own module system. While the original idea
was to modularize the enormous JDK API itself the *Java Platform Module System
(JPMS)* is now considered as the standard way to bundle any library.

{{< /jdkdetails >}}


## Sandbox

Instantly compile and run Java 9 snippets without a local Java installation.

{{< sandbox version="java9" mainclass="Java9" >}}
{{< sandboxsource "Java9.java" >}}
public class Java9 {

    public static void main(String[] args) {

        System.out.println("Hello " + String.class.getModule());

    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}
