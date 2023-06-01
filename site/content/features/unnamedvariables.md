---
title: Unnamed Patterns and Variables (JEP 443)
copyright: Cay S. Horstmann 2023. All rights reserved.
jep: 443
jdkversion: 21
type: "sandbox"
---


Sometimes, Java syntax requires you to specify a variable name even when you never refer to it. JEP 443 allows you to use an underscore in many of these cases. This feature is probably most useful in record patterns, where an underscore can even replace a type + name. This article shows all situations where you can use the “unnamed” underscore.

Both in record patterns and variable declarations, you specify variables by providing a type (or `var`) and a name. It can happen that you don't need the name, or even the type. Under certain circumstances, you can then use an underscore `_` placeholder for the name, or for both type and name. 

Note that the variable name `_` was deprecated for removal in Java 8 and removed in Java 9. This pertains only to a single underscore. Identifiers containing underscores, such as `UTF_8`, continue to be fine.

If you have ancient source code where `_` is used as a variable name, chances are good that you used `_` for exactly the use case that unnamed variables are meant to address. Then you need to do nothing. If you need to update your code, you can always use two underscores `__` or, if you like, any [Connecting Punctuation Character](https://www.fileformat.info/info/unicode/category/Pc/list.htm). My favorite choice is ﹏, U+FE4F WAVY LOW LINE.

## Record Patterns

When matching components of a record pattern, you may not need the names of all variables. In this example, we classify the location of points:

```
record Point(int x, int y) {}
...
Point p = new Point(3, 4);
String description = switch (p) {
   case Point(var x, var y) when x == 0 && y == 0 -> "origin";
   case Point(var x, var _) when x == 0 -> "on y-axis";
   case Point(var _, var y) when y == 0 -> "on x-axis";
   case Point(var _, var _) -> "somewhere else";
};
```

To find out whether a point is on the x- or y-axis, we only need to look at one component. The `_` replace variable names that are not needed for the match. You can have more than one `_` in the same pattern, as in the last case of the preceding example.

You can abbreviate `var _` to `_`, simplifying the example further:

```
description = switch (p) {
   case Point(var x, var y) when x == 0 && y == 0 -> "origin";
   case Point(var x, _) when x == 0 -> "on y-axis";
   case Point(_, var y) when y == 0 -> "on x-axis";
   case Point(_, _) -> "somewhere else";
};
```

This is called an “unnamed pattern”.

The same syntax holds for `instanceof` patterns:

```
if (p instanceof Point(_, var y) && y == 0) System.out.println("on x-axis");
```

In all preceding examples, I used `var` for the type, but you can also specify the type rather than have it inferred:

```
if (p instanceof Point(int _, int y) && y == 0) System.out.println("on x-axis");
```

The unnamed pattern `_` and `var _` can only occur inside records. They are not allowed at the top level. There, just use `default`.

This sandbox shows unnamed variables and the unnamed pattern with records.

{{< sandbox version=java21 preview="true" mainclass="Unnamed1" >}}{{< sandboxsource "Unnamed1.java" >}}
record Point(int x, int y) {}

public class Unnamed1 {

   public static void main(String[] args) {
      Point p = new Point(3, 0);
      String description = switch (p) {
         case Point(var x, var y) when x == 0 && y == 0 -> "origin";
         case Point(var x, var _) when x == 0 -> "on y-axis";
         case Point(var _, var y) when y == 0 -> "on x-axis";
         case Point(var _, var _) -> "somewhere else";
      };
      System.out.println(description);

      description = switch (p) {
         case Point(var x, var y) when x == 0 && y == 0 -> "origin";
         case Point(var x, _) when x == 0 -> "on y-axis";
         case Point(_, var y) when y == 0 -> "on x-axis";
         case Point(_, _) -> "somewhere else";
         // Weirdly, in build 21-ea+24-2086, the following line is necessary
         default -> throw new MatchException("This can't happen", null); 
      };
      System.out.println(description);

      if (p instanceof Point(var x, var y) && x == 0 && y == 0) System.out.println("origin");
      else if (p instanceof Point(var x, _) && x == 0) System.out.println("on y-axis");
      else if (p instanceof Point(_, var y) && y == 0) System.out.println("on x-axis");
      else if (p instanceof Point) System.out.println("somewhere else");
   }
}

{{< /sandboxsource >}}
{{< /sandbox >}}

## Type Patterns

You can use unnamed variables with type patterns:

```
case Double _, Float _ -> "a floating-point number";
```

Here is a more complex situation, with nested type patterns and an unnamed pattern: 

```
record Box<T>(T value) {}
...
Box<Number> box = new Box(...);
String description = switch (box) {
   case Box(Float _), Box(Double _) -> "a boxed floating-point number";
   case Box(_) -> "something other than a floating-point number in this box";
};
```

In the first case, the type is used for matching. This is different from the example of the preceding section, where the type of the component was known.

There are a couple of subtleties. When you have two cases, as in the first example, and you add a `when` clause, it applies to both cases:

```
   case Box(Float _), Box(Double _) when box.floatValue() > 0 -> "a boxed positive floating-point number";
```

You can't apply a `when` clause to separate cases:

```
   case Point(var x, _) when x = 0, Point(_, var y) when y = 0 // Error

```

More subtly, as always with `switch`, we have to talk about `null`. What happens to the `switch` when `box` is `null`?. For backwards compatibility, a `switch` without `case null` is “null-hostile” and throws a `NullPointerException`

What if `box` is `new Box(null)`? Do we get a `NullPointerException`? A `MatchException`? No—`Box(_)` matches `new Box(null)`. The null-hostility only applies to the top level.

This sandbox shows type patterns with unnamed variables.

{{< sandbox version=java21 preview="true" mainclass="Unnamed2" >}}{{< sandboxsource "Unnamed2.java" >}}
record Box<T>(T value) {}

public class Unnamed2 {

   public static void main(String[] args) {
      Object obj = Double.valueOf(3.14);
      String description = switch (obj) {
         case Double _, Float _ -> "a floating-point number";
         default -> "something other than a floating-point number";
      };
      System.out.println(description);

      Box<Number> box = new Box(Integer.valueOf(42));
      // box = new Box(null);
      // box = null;
      description = switch (box) {
         case Box(Float _), Box(Double _) -> "a boxed floating-point number";
         case Box(_) -> "something other than a floating-point number in this box";
      };
      System.out.println(description);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

## Exceptions

The use of `_` as a “don't care” pattern is easy and compelling, but there are other situations where we truly don't care about a variable name. Sometimes, when catching an expression, you never look at the exception. With JEP 443, you don't have to come up with a variable name for the unused object. This sandbox shows a typical example.

{{< sandbox version=java21 preview="true" mainclass="Unnamed3" >}}{{< sandboxsource "Unnamed3.java" >}}
public class Unnamed3 {
   public static double parseDouble(String s) {
      try {
         return Double.parseDouble(s);
      } catch (NumberFormatException _) {
         return Double.NaN;
      }
   }

   public static void main(String[] args) {
      System.out.println(parseDouble("3.14"));
      System.out.println(parseDouble("Fred"));
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

## Try with Resources

Consider a typical “`try` with resources” statement:

```
try (var obj = someAutoCloseable()) {
   ...
}
```

In almost all cases, you need to refer to `obj` inside the block. For example:

```
try (var out = new PrintWriter(...)) {
   out.println(...);
   out.println(...);
} // out.close() called here
```

But what if you don't? Then you can use

```
try (var _ = ...) {
   ...
} // unnamedVariable.close() called here
```

The following sandbox shows an example with a `DynamicScope` object that manages nested assignments from names to values. When you open a new scope, its names shadow those of previous assignments. When the scope is closed, the new assignments are forgotton. The whole point of the `try` block is to call the `close` method that implements the relinquishment of old assignments. The name of the object is immaterial, as long as the `close` method is called.

This is not a common situation, but it is nevertheless plausible. Seemingly, the language implementors were on the lookout for any situations where a variable name is not required.

{{< sandbox version=java21 preview="true" mainclass="Unnamed4" >}}{{< sandboxsource "Unnamed4.java" >}}
import java.util.*;

class DynamicScope implements AutoCloseable {
   private List<HashMap<String, Object>> namedValues = new LinkedList<>();

   public DynamicScope open() {
      namedValues.addFirst(new HashMap<>());
      return this;
   }
   public void close() {
      namedValues.removeFirst();
   }
   public void put(String name, Object value) {
      if (namedValues.size() == 0) throw new IllegalStateException("Not open");
      else namedValues.get(0).put(name, value);
   }
   public Object get(String name) {
      for (var map : namedValues)
         if (map.containsKey(name)) return map.get(name);
      throw new NoSuchElementException(name);
   }   
}

public class Unnamed4 {
   public static void main(String[] args) {
      var scope = new DynamicScope();
      try (var _ = scope.open()) {      
         scope.put("name", "Fred");
         System.out.println(scope.get("name"));
         try (var _ = scope.open()) {
            scope.put("name", "Wilma");
            System.out.println(scope.get("name"));         
         }
         System.out.println(scope.get("name"));
      }
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

## Lambdas

When a method has a parameter whose type is a functional interface, it can happen that you pass a lambda expression where not all arguments are needed. Here is an example. The `Map::merge` method has three parameters:

* A key 
* An initial value for the key, to be used if the key was not present
* A function that combines the current and the initial value

To increment a value, you use:

```
map.merge(key, 1, (v, w) -> v + w);
```

or

```
map.merge(key, 1, Integer::sum);
```

I always find it a bit baffling that the initial value is used in the lambda. I just want to say: “Set the value to 1 or increment it”. I think it looks pretty clear with the `_` parameter:

```
map.merge(key, 1, (v, _) -> v + 1);
```

Here is another example, splitting up a list into two random sublists:

```
Map<Boolean, List<String>> sublists = list.stream().collect(Collectors.partitioningBy(
   _ -> Math.random() < 0.5))
```

Normally, we partition by some property of the elements, but here, we don't care.

Here is a sandbox with these examples:

{{< sandbox version=java21 preview="true" mainclass="Unnamed5" >}}{{< sandboxsource "Unnamed5.java" >}}
import java.util.*;
import java.util.stream.*;

public class Unnamed5 {
   public static void main(String[] args) {
      Map<Integer, Integer> map = new HashMap<>();
      int n = 42;
      map.merge(n, 1, (v, _) -> v + 1);
      map.merge(n, 1, (v, _) -> v + 1);
      System.out.println(map);
      var partitioned = Stream.of("Mary had a little lamb".split(" ")).collect(Collectors.partitioningBy(_ -> Math.random() < 0.5));
      System.out.println(partitioned);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

## Variable Declarations

If you want to be explicit about the fact that you are ignoring the return value of a method call, you can assign it to an unnamed variable:

```
Scanner in = ...;
while (in.hasNext()) {
   count++;
   var _ = in.next();
}
```

I don't know if I would ever do this, but it might be useful to placate an overly zealous linting tool.

You can even use anonymous variables in a `for` loop. In this enhanced `for` loop, we don't care about the elements in an iterable (presumably, one without a `size` method):

```
for (var _ : iterable) count++;
```

With a classic `for` loop, you can initialize unnamed variables with an expression that you evaluate for its side effect:

```
for (Scanner words = new Scanner("Mary  had a little lamb"), _ = words.useDelimiter("\\s+");
      words.hasNext(); words.next()) {
   count++;
}
```

Note that all variables that are declared in the loop header must have the same type. In this case, by good fortune (or actually, low cunning by the author), the return type of `Scanner::useDelimiter` is `Scanner`, but you won't usually be so lucky.

This final sandbox shows these marginally useful examples of unnamed variable declarations.

{{< sandbox version=java21 preview="true" mainclass="Unnamed6" >}}{{< sandboxsource "Unnamed6.java" >}}
import java.util.*;

public class Unnamed6 {
   public static <T> Iterable<T> toIterable(Iterator<T> iterator) {
      return () -> iterator; 
   }

   public static void main(String[] args) {
      Scanner in = new Scanner("Mary had a little lamb");
      int count = 0;
      while (in.hasNext()) {
         count++;
         var _ = in.next();
      }
      System.out.println(count);

      in = new Scanner("Mary had a little lamb");
      count = 0;
      for (var _ : toIterable(in)) count++;
      System.out.println(count);

      count = 0;
      for (Scanner words = new Scanner("Mary  had a little lamb"), _ = words.useDelimiter("\\s+");
            words.hasNext(); words.next()) {
         count++;
      }
      System.out.println(count);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

## References

* [JEP 443: String Unnamed Patterns and Variables (Preview), OpenJDK](https://openjdk.java.net/jeps/443)


