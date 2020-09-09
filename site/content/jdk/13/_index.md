---
title: Java 13
type: sandbox
---

{{< jdkdetails "13" >}}

{{< /jdkdetails >}}

## Sandbox

Instantly compile and run Java 13 snippets without a local Java installation.

{{< sandbox version="java13" mainclass="Java13" preview="false" >}}
{{< sandboxsource "Java13.java" >}}
import java.nio.CharBuffer;

public class Java13 {
    
    public static void main(String[] args) {
        CharBuffer buffer = CharBuffer.allocate(20);
        buffer.put("What? Hello Universe");

        CharBuffer slice = buffer.slice(6, 14);

        buffer.position(12);
        buffer.put("Java 13!");

        System.out.println(slice);
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

