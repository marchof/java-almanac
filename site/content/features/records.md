---
title: Records (JEP 395)
copyright: Cay S. Horstmann 2020. All rights reserved.
jep: 395
jdkversion: 16
type: "sandbox"
---

Records were a major preview feature of JDK 14 and are an official feature since JDK 16. A record is an immutable class whose state is visible to all — think of a `Point` with `x` and `y` coordinates. There is no need to hide them. Records make it very easy to declare such classes. A constructor, accessors, `equals`, `hashCode`, and `toString` come for free. 

Why Records?
------------

A core concept of object-oriented design is encapsulation — the hiding of private implementation details. Encapsulation enables evolution — changing the internal representation for greater efficiency or to support new features.

But sometimes, there is nothing to encapsulate. Consider your typical `Point` class that represents a point on a plane, with an x and a y coordinate. 

Of course, you could make public instance variables

```
class Point {
   public double x;
   public double y;
   ...
}
```

In fact, `java.awt.Point` does just that. But then `Point` instances are mutable. If you want immutability, you need to provide a constructor and accessors for the coordinates. And of course you want an `equals` method, and then you also need a `hashCode` method. And maybe `toString` and serialization.

That's what records give you. You declare

```
record Point(double x, double y) {}
```

and you are done.

Of course, records have limited applicability. How limited? A [report](http://cr.openjdk.java.net/~cushon/amalloy/JDKRecordsProposalReport.html) from Alan Malloy compares records with an annotation processor for a similar purpose that is used in-house at Google. From his experience, records might be about as commonly used as `enum`. That is a good way of thinking about records. Like `enum`, a `record` is a restricted form of a class, optimized for a specific use case. In the most common case, the declaration is as simple as it can be, and there are tweaks for customization.

What You Get for Free
---------------------

When you declare a record, you get all these goodies:

*   A private instance variable for each of the variables in the thing-that-looks-like-a-constructor-heading after the record name
*   A “canonical” constructor that sets all instance variables. You construct a `Point` instance as `new Point(3, 4)`. 
*   Accessors for each instance variable. If `p` is a `Point`, you get the coordinates as `p.x()` and `p.y()`. (Note: Not `p.getX()`.)
*   Public methods `equals`, `hashCode`, `toString`. For example, `p.toString()` yields the string `"Point[x=1.0, y=0.0]"`.
*   Serialization provided the record implements the `Serializable` interface:
    
{{< sandbox version="java16" mainclass="RecordDemo" >}}
{{< sandboxsource "RecordDemo.java" >}}
import java.io.*;

record Point(double x, double y) implements Serializable {}

public class RecordDemo {
   public static void main(String[] args) throws IOException, ClassNotFoundException {
      Point p = new Point(3, 4);
      Point q = new Point(3, 4);
      
      System.out.println("Accessors: " + p.x() + " " + p.y());
      System.out.println("toString: " + p);
      System.out.println("hashCode: " + p.hashCode());
      System.out.println("Are p and q the same? " + (p == q));
      System.out.println("Are p and q equal? " + p.equals(q));
      
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      ObjectOutputStream oout = new ObjectOutputStream(bout);
      oout.writeObject(p);
      oout.close();
      ObjectInputStream oin = new ObjectInputStream(
         new ByteArrayInputStream(bout.toByteArray()));
      Point r = (Point) oin.readObject();
      System.out.println("Serialized and deserialized: " + r);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}




Other Things That You Can Do
----------------------------

A record can have any number of instance methods:

{{< sandbox version="java16" mainclass="RecordDemo" >}}
{{< sandboxsource "RecordDemo.java" >}}
record Point(double x, double y) {
   public double distance(Point q) {
      return Math.sqrt((x - q.x) * (x - q.x) + (y - q.y) * (y - q.y));
   }
}

public class RecordDemo {
   public static void main(String[] args) {
      Point p = new Point(0, 0);
      Point q = new Point(3, 4);
      System.out.println("Distance from the origin: " + p.distance(q));
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}


You can provide your own implementation for any of the required instance methods:

{{< sandbox version="java16" mainclass="RecordDemo" >}}
{{< sandboxsource "RecordDemo.java" >}}
record Point(double x, double y) {
    public String toString() { return "[" + x + ", " + y + "]"; }
}

public class RecordDemo {
   public static void main(String[] args) {
      Point p = new Point(3, 4);
      System.out.println("Our very own toString: " + p);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Static fields and methods are fine:

{{< sandbox version="java16" mainclass="RecordDemo" >}}
{{< sandboxsource "RecordDemo.java" >}}
record Point(double x, double y) {
   public static Point ORIGIN = new Point(0, 0);
   public static double distance(Point p, Point q) {
      return Math.sqrt((p.x - q.x) * (p.x - q.x) + (p.y - q.y) * (p.y - q.y));
   }
}

public class RecordDemo {
   public static void main(String[] args) {
      Point p = new Point(3, 4);
      double d = Point.distance(Point.ORIGIN, p);
      System.out.println("Distance from the origin: " + d);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}


You can implement any interfaces:

{{< sandbox version="java16" mainclass="RecordDemo" >}}
{{< sandboxsource "RecordDemo.java" >}}
record Point(double x, double y) implements Comparable<Point> {
   public int compareTo(Point other) {
      int dx = Double.compare(x, other.x);
      return dx != 0 ? dx : Double.compare(y, other.y);
   }
}

public class RecordDemo {
   public static void main(String[] args) {
      Point[] points = {
         new Point(4, 3),
         new Point(3, 5),
         new Point(3, 4)
      };
      java.util.Arrays.sort(points);
      System.out.println(java.util.Arrays.toString(points));
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Records can be local—defined inside a method—just like local classes: 

{{< sandbox version="java16" mainclass="RecordDemo" >}}
{{< sandboxsource "RecordDemo.java" >}}
import java.util.*;
import java.util.stream.*;

public class RecordDemo {
   public static void main(String[] args) {
      record Point(double x, double y) {}
      Random gen = new Random();
      List<Point> points = Stream.generate(() -> new Point(gen.nextDouble(), gen.nextDouble()))
         .limit(10)      
         .sorted(Comparator.comparing(Point::x).thenComparing(Point::y))
         .toList();
      System.out.println("Ten random points, sorted for your convenience: " + points);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}


Parameterized records — no problem:

{{< sandbox version="java16" mainclass="RecordDemo" >}}
{{< sandboxsource "RecordDemo.java" >}}
record Point<T>(T x, T y) {}

public class RecordDemo {
   public static void main(String[] args) {
      var p = new Point<Double>(3.0, 4.0);
      System.out.println("Double coordinates: " + p);
      System.out.println("String coordinates: " + new Point<String>("three", "four"));
      System.out.println("Point coordinates: " + new Point<Point<Double>>(p, p));
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Constructors: Canonical, Custom, and Compact
--------------------------------------------

Every record has a canonical constructor that sets all instance variables.

You can add "custom" constructors in addition to the canonical constructor. The first statement of such a constructor must invoke another constructor, so that ultimately the canonical constructor is invoked. The following record has two constructors: the canonical constructor and a custom constructor yielding the origin.

{{< sandbox version="java16" mainclass="RecordDemo" >}}
{{< sandboxsource "RecordDemo.java" >}}
record Point(double x, double y) {
   public Point() { this(0, 0); }
}

public class RecordDemo {
   public static void main(String[] args) {
      Point p = new Point(3, 4);
      Point q = new Point();
      System.out.println("Canonical constructor: " + p);
      System.out.println("Our very own constructor: " + q);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

You can also provide your own implementation of the canonical constructor. When you do so, you can declare the constructor in the usual way:

{{< sandbox version="java16" mainclass="RecordDemo" >}}
{{< sandboxsource "RecordDemo.java" >}}
record Point(double x, double y) {
   public Point(double x, double y) {
      if (x == y || x == -y) { onDiagonal++; } 
      this.x = x;
      this.y = y;
   }
   public static int onDiagonal = 0;
}

public class RecordDemo {
   public static void main(String[] args) {
      Point p = new Point(3, 4);
      Point q = new Point(3, -3);
      Point r = new Point(0, 0);
      
      System.out.println("Points on diagonal lines: " + Point.onDiagonal);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

This is rather verbose and not what you want to do in practice. Instead, you should use the "compact" form. Omit the constructor parameters and the instance variable initialization:

{{< sandbox version="java16" mainclass="RecordDemo" >}}
{{< sandboxsource "RecordDemo.java" >}}
record Point(double x, double y) {
   public Point {
      if (x == y || x == -y) { onDiagonal++; } 
   }
   public static int onDiagonal = 0;
}

public class RecordDemo {
   public static void main(String[] args) {
      Point p = new Point(3, 4);
      Point q = new Point(3, -3);
      Point r = new Point(0, 0);
      
      System.out.println("Points on diagonal lines: " + Point.onDiagonal);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

You can modify the constructor parameters before they are assigned to the instance variables. Here we normalize an angle in [polar coordinates](https://en.wikipedia.org/wiki/Polar_coordinate_system) so that it is between 0 and 2π:

{{< sandbox version="java16" mainclass="RecordDemo" >}}
{{< sandboxsource "RecordDemo.java" >}}
record PolarPoint(double r, double theta) {
   public PolarPoint {
      theta = Math.IEEEremainder(theta, 2 * Math.PI);
      if (theta < 0) theta += 2 * Math.PI;
   }
}

public class RecordDemo {
   public static void main(String[] args) {
      PolarPoint p = new PolarPoint(1, 20.5 * Math.PI);
      System.out.println("Normalized angle: " + p);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Note that the assignment of the parameters `r` and `theta` to the instance variables `this.r` and `this.theta` happens *at the end* of the canonical constructor. You cannot read or modify the instance variables in the body of the canonical constructor.


What You Can't Do
-----------------

Most importantly, records cannot have any instance variables other than the “record components” — the variables declared with the canonical constructor. The state of a record object is entirely determined by the record components.

You cannot extend a record — it is implicitly `final`.

A record cannot extend another class, not even another record. (Any record implicitly extends `java.lang.Record`, just like any enumerated type implicitly extends `java.lang.Enum`. The `Record` superclass has no state and only abstract `equals`, `hashCode`, and `toString` methods.)

There are no “inner records”. A record that is defined inside another class or method is automatically `static`. That is, it doesn't have a reference to its enclosing class (which would be an additional instance variable).

The canonical constructor cannot throw checked exceptions:

```
record SleepyPoint(double x, double y) {
   public SleepyPoint throws InterruptedException { // Error
      Thread.sleep(1000); 
   }
}
```

Reflection
----------

The `isRecord` method can tell whether a `Class` instance is a record.

Reflection reports the record components as private fields.

You can also call `getRecordComponents` to get an array of `java.lang.reflect.RecordComponent` instances. Such an instance describes the record component, just like `java.lang.reflect.Field` describes a field.

To read the value of a component reflectively, you can get the accessor method from the `RecordComponent` object.

{{< sandbox version="java16" mainclass="RecordDemo" >}}
{{< sandboxsource "RecordDemo.java" >}}
import java.lang.reflect.*;

record Point(double x, double y) {}
   
public class RecordDemo {
   public static void main(String[] args) throws ReflectiveOperationException {
      System.out.println("It's a record: " + Point.class.isRecord());
      Field[] fields = Point.class.getDeclaredFields();
      System.out.println("Fields: " + java.util.Arrays.toString(fields));
      RecordComponent[] components = Point.class.getRecordComponents();
      System.out.println("Components: " + java.util.Arrays.toString(components));

      var p = new Point(3, 4);
      RecordComponent rc = p.getClass().getRecordComponents()[0];
      Object value = rc.getAccessor().invoke(p);
      System.out.println("Component " + rc.getName() + " has value " + value);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Some Further Observations
-------------------------

1\.  Some languages have tuples or product types. In those languages, you can model a point as a pair of `double`. But in Java, we like names. Point components should have names `x` and `y`, and we want the whole thing to be a `Point`, distinct from any other pairs of `double`.

2\. A record variable holds a reference to an object. That is, records are _not_ value or inline types — another new kid on the block. Project Valhalla will let you define

```
inline class Point {
   private double x;
   private double y;
   ...
}
```

Then a `Point` variable holds a flat 16 bytes of data, not a reference to an object. But the fields are still encapsulated. In time, you should be able to declare an `inline record`, with flat layout and no encapsulation.

3\. Records are only as immutable as their fields are. Nothing stops you from having mutable components:

{{< sandbox version="java16" mainclass="RecordDemo" >}}
{{< sandboxsource "RecordDemo.java" >}}
record Point(double... coordinates) {
   public String toString() {
      return java.util.Arrays.toString(coordinates);
   }
}

public class RecordDemo {
   public static void main(String[] args) {
      Point p = new Point(0, 0);
      System.out.println("The origin: " + p);
      p.coordinates()[0] = 3;
      System.out.println("Not anymore: " + p);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Because arrays are mutable, you can change the elements of `coordinates`.

This is not a good idea, but the Java language won't stop you. In general, Java has no mechanism for expressing immutability. It is no different with reccords.

4\. The implementations of `hashCode`, `equals`, and `toString` in the JDK are _not normative_. In particular, the current behavior of combining two hash codes as `31 * h1 + h2` could change. The behavior of `equals` is constrained by the general `Object.equals` contract, but there is no guarantee that the order of comparisons is fixed. You should not rely on the exact format of the `toString` result either.

5\. It is envisioned that in the future, records can be used for pattern matching, with a syntax somewhat like:

```
switch (obj) { 
   case instanceof Point(x, 0) p: ... // Maybe the future - not in JDK 14
   ...
}
```



## References

* [JEP 359: Records (Preview), OpenJDK](http://openjdk.java.net/jeps/359)
* [JEP 384: Records (Second Preview), OpenJDK](http://openjdk.java.net/jeps/384)
* [JEP 395: Records, OpenJDK](http://openjdk.java.net/jeps/395)
