---
title: Java 17
type: sandbox
---

{{< jdkdetails "17" >}}

Java 17 is the next long term support (LTS) release after Java 11.

{{< /jdkdetails >}}

## Sandbox

Instantly compile and run Java 17 snippets without a local Java installation.

{{< sandbox version="java17" mainclass="Java17" preview="true" >}}
{{< sandboxsource "Java17.java" >}}
import java.util.HexFormat;

public class Java17 {

    public static void main(String[] args) {
        var fmt = HexFormat.ofDelimiter(", ").withPrefix("0x");
        System.out.println(fmt.formatHex("I ❤️ Java".getBytes()));
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}
