---
title: Pattern matching for instanceof (JEP 394)
copyright: Cay S. Horstmann 2020. All rights reserved.
jep: 394
jdkversion: 16
type: "sandbox"
---

The motivation in [JEP 394](http://openjdk.java.net/jeps/305) starts out with this statement: "Nearly every program includes some sort of logic that combines testing if an expression has a certain type or structure, and then conditionally extracting components of its state for further processing." This example follows:

```
if (obj instanceof String) {
    String s = (String) obj;    // grr...
    . . .
}
```

One would hope that this kind of type inquiry is not a part of "nearly every program". But certainly, there are times when it is necessary, and the classic Java approach of `instanceof` followed by a cast is rather verbose. Do we really need three occurrences of the type name `String`?

That is what "pattern matching for `instanceof`" fixes. 

{{< sandbox version="java16" mainclass="InstanceofDemo" >}}
{{< sandboxsource "InstanceofDemo.java" >}}
public class InstanceofDemo {
    public static void main(String[] args) {
        Object what = Math.random() < 0.5 ? 42 : "42";
        if (what instanceof Integer i) {
            System.out.println("Integer with value " + i.intValue());
        }
        else if (what instanceof String s) {
            System.out.println("String of length " + s.length());
        }
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

As you can see, this form of the `instanceof` test declares a variable. If the test passes, the variable is simply set to the value that was tested. The point is that the variable has the correct type. In the example, we can call `i.intValue()` and `s.length()` since `i` is an `Integer` and `s` is a `String`. 

As always, if the tested value is `null`, the `instanceof` test fails.

## Variable Scope

When an `instanceof` pattern introduces a variable, what is its scope? In the preceding example, it seems clear that the scope of `i` is the body of the `if` statement. But actually, the scope begins earlier. You can already use the variable in a compound expression that includes the `instanceof` test, provided that it is known that the test has passed:

{{< sandbox version="java16" mainclass="InstanceofDemo" >}}
{{< sandboxsource "InstanceofDemo.java" >}}
public class InstanceofDemo {
    public static void main(String[] args) {
        Object what = Math.random() < 0.5 ? 42 : "42";
        if (what instanceof Integer i && i.intValue() > 0) {
            System.out.println("Positive integer with value " + i.intValue());
        }
        else if (what instanceof String s) {
            System.out.println("String of length " + s.length());
        }
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}
  
However, the expression

```
what instanceof Integer i || i.intValue() > 0
```

would be a compile-time error. The second operand of the `||` operator is executed when the `instanceof` test fails. Then `i` is not defined.

You *can* use an `||` if the `instanceof` test is negated:

{{< sandbox version="java16" mainclass="InstanceofDemo" >}}
{{< sandboxsource "InstanceofDemo.java" >}}
public class InstanceofDemo {
    public static void main(String[] args) {
        Object what = Math.random() < 0.5 ? Math.random() < 0.5 ? 42 : -42 : "1729";
        if (!(what instanceof Integer i) || i.intValue() <= 0) {
            System.out.println("No good: " + what + " is not an integer or not positive");
        }
        else {
            System.out.println("Hooray: a positive integer with value " + i.intValue());
        }
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

This is perhaps a little mind-bending. If the `instanceof` test fails, the negation is `true` and the right hand side of the `||` is never executed. In this case, `i` is not bound, and the program prints "No good".

If the `instanceof` test passes, the negation is `false`, and the right hand side of the `||` *is* executed. And `i` is defined because the test passed. 

You can only reach the `else` clause when the `instanceof` test passes, and therefore, `i` is also in scope in the `else` clause.

Note that these scope rules do not apply to the `&` and `|` operators. They don't short-circuit. That is, both sides are always evaluated, and any variables used in the right operand must be defined unconditionally.

However, the conditional operator is fair game:

{{< sandbox version="java16" mainclass="InstanceofDemo" >}}
{{< sandboxsource "InstanceofDemo.java" >}}
public class InstanceofDemo {
    public static void main(String[] args) {
        Object what = Math.random() < 0.5 ? 42 : "1729";
        System.out.println(what instanceof Integer i 
            ? "Hooray: " + i.intValue() 
            : "No good: " + what);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Here, `i` is in scope in the `?` part. 

## Useless Matches are Errors

A pattern match that always succeeds is flagged as an error:

{{< sandbox version="java16" mainclass="InstanceofDemo" >}}
{{< sandboxsource "InstanceofDemo.java" >}}
public class InstanceofDemo {
    public static void main(String[] args) {
        Double x = Math.PI;
        if (x instanceof Number n) { // Error
           System.out.println(n.longValue());
        }
        if (x instanceof Number) { // Ok
           System.out.println(x.longValue());
        }
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Here, the match `x instanceof Number n` is useless. It always succeeds and should therefore be omitted. This is a compile-time error.

The test `x instanceof Number` is equally useless, but ever since Java 1.0, such tests were allowed. 

## Use of `instanceof` in `equals`

It has traditionally been rather painful to implement the `equals` method:

```
public final class Point {
    public int x;
    public int y;

    public boolean equals(Object other) {
        if (other instanceof Point) {
            Point p = (Point) other;
            return x == p.x && y == p.y;
        }
    }
```

An `instanceof` pattern makes this nicer:

{{< sandbox version="java16" mainclass="InstanceofDemo" >}}
{{< sandboxsource "Point.java" >}}
public final class Point {
    public int x;
    public int y;
    
    public Point(int x, int y) { this.x = x; this.y = y; }
    
    public boolean equals(Object other) {
        return other instanceof Point p && x == p.x && y == p.y;
    }
}
{{< /sandboxsource >}}
{{< sandboxsource "InstanceofDemo.java" >}}
public class InstanceofDemo {
    public static void main(String[] args) {
        Point a = new Point(3, 4);
        Point b = new Point(3, 4);
        System.out.println(a.equals(b) ? "Equal" : "Not equal");
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

## Why are these called patterns?

Java intends to follow other languages that support matching values against patterns with complex criteria. For example, if you have a value `what`, you might want to know if it is a point at the origin, another point, or something else entirely:

```
switch (what) {
    case Point(0, 0) => ... // It's the point at the origin
    case Point p => ... // A point other than (0, 0)
    default => ... // Something else
}
```

Note that in the second `case`, a variable `p` is introduced so that you can work with the value as a `Point`.

Java will, in time, have a syntax more or less like this. For now, the `instanceof` with a variable declaration is a "low-hanging fruit" whose syntax resembles what will likely be a part of the pattern matching syntax.

## References

* [JEP 305: Pattern Matching for instanceof (Preview), OpenJDK](http://openjdk.java.net/jeps/305)
* [JEP 375: Pattern Matching for instanceof (Second Preview), OpenJDK](http://openjdk.java.net/jeps/375)
* [JEP 394: Pattern Matching for instanceof, OpenJDK](http://openjdk.java.net/jeps/394)

