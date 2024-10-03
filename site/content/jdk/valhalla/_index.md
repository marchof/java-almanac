---
title: Valhalla Sandbox
---

Online sandbox for the latest early access build of [Valhalla project](https://jdk.java.net/valhalla/).

{{< sandbox version="javavalhalla" mainclass="Valhalla" preview="true" >}}
{{< sandboxsource "Valhalla.java" >}}
value class X {

    final int i;

    X(int i) {
        this.i = i;
    }

}

public class Valhalla {

    public static void main(String[] args) {
        var x1 = new X(5);
        var x2 = new X(5);
        var x3 = new X(7);

        System.out.println(x1 == x2);
        System.out.println(x1 == x3);
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}