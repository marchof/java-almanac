---
title: Java 19
---

{{< jdkdetails "19" >}}
{{< /jdkdetails >}}

## Sandbox

Instantly compile and run Java 19 snippets without a local Java installation.

{{< sandbox version="java19" mainclass="Java19" preview="true" >}}
{{< sandboxsource "Java19.java" >}}
import java.util.Objects;
import javax.lang.model.SourceVersion;

public class Java19 {

    public static void main(String[] args) {
        var message = "Hello Java " + SourceVersion.RELEASE_19 + "!";
        System.out.println(message);
        System.out.println(Objects.toIdentityString(message));
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}
