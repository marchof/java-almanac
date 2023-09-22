---
title: Valhalla Sandbox
---

Online sandbox for the latest early access build of [Valhalla project](https://jdk.java.net/valhalla/).

{{< sandbox version="javavalhalla" mainclass="Valhalla" preview="true" >}}
{{< sandboxsource "Valhalla.java" >}}
value class X {
}

identity record Id(String key) {
}

public class Valhalla {

    public static void main(String[] args) {
        System.out.println(new X());
        System.out.println(new Id("me"));
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}