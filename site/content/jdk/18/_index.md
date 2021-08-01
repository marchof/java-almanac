---
title: Java 18
type: sandbox
---

{{< jdkdetails "18" >}}
{{< /jdkdetails >}}

## Sandbox

Instantly compile and run Java 18 snippets without a local Java installation.

{{< sandbox version="java18" mainclass="Java18" preview="true" >}}
{{< sandboxsource "Java18.java" >}}
import javax.lang.model.SourceVersion;

public class Java18 {

    public static void main(String[] args) {
        System.out.println("Compiled with " + SourceVersion.RELEASE_18);
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}