---
title: Java 22
description: Information about Java 22 including documentation links, new APIs, added features and download options.
---

{{< jdkdetails "22" >}}
{{< /jdkdetails >}}

## Sandbox

Instantly compile and run Java 22 snippets without a local Java installation.

{{< sandbox version="java22" mainclass="Java22" preview="true" >}}
{{< sandboxsource "Java22.java" >}}
import java.text.ListFormat;
import java.util.List;

public class Java22 {

    public static void main(String[] args) {
    	var f = ListFormat.getInstance();
        System.out.printf(f.format(List.of("classes", "interfaces", "enums", "records")));
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}