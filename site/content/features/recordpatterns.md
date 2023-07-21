---
title: Record Patterns (JEP 405)
copyright: Cay S. Horstmann 2022. All rights reserved.
jep: 405
jdkversion: 19
---


Record patterns,  a preview feature of Java 19, let you “deconstruct” record values, binding each component to a variable. Record patterns work with instanceof and switch pattern matching. Guards are supported. They are particularly compelling with nested deconstruction and sealed record hierarchies.

## Deconstructing a Record

Java 19 has one public `record`:

```
public record UnixDomainPrincipal(UserPrincipal user, GroupPrincipal group)
```

Suppose you have an `Object` that might just be an instance thereof. Then you can take it apart like this:

```
if (obj instanceof UnixDomainPrincipal(var u, var g)) {
   // Do something with u and g
}
```

Here, `UnixDomainPrincipal(var u, var g)` is a *record pattern*. If the scrutinee (that is, the value to be matched) is an instance of the record, then the variables `u` and `g` in the pattern are bound to the record components. The code is equivalent to

```
if (obj instanceof UnixDomainPrincipal p) {
   var u = p.user();
   var g = p.group();
   // Do something with u and g
}
```

Instead of `var`, you can also use the actual types of the components:

```
if (obj instanceof UnixDomainPrincipal(UserPrincipal u, DomainPrincipal g)) {
   // Do something with u and g
}
```

Either way, the syntax is meant to remind you of variable declarations. 

You can also use a record pattern in `switch`:

```
switch (obj) {
   case UnixDomainPrincipal(var u, var g):
      // Do something with u and g
      break;
   default:
      break;
}
```

That's potentially nice, but how often does it happen that you have an `Object` that might be a record instance? To see more interesting examples, we need multiple record classes. It gets even better when they extend a sealed interface because then the `switch` can test for exhaustiveness.

## A Sealed Record Family

Ever since Java 1.4, there has been a `CharSequence` interface with methods

```
char charAt(int index);
int length();
CharSequence subSequence(int start, int end);
String toString();
```

Java 8 added a couple of default methods, and Java 11 a static method. We ignore them for this example.

The interface is implemented by `StringBuilder`, the legacy `StringBuffer`, `java.nio.CharBuffer`, a Swing class, and of course `String`. It is mostly used to write code that works with both `String` and `StringBuilder`.

We want to manipulate subsequences. They could touch the beginning or the end, or they lie in the middle. This is where we get a sealed interface and three records:

```
sealed interface SubSequence extends CharSequence permits Initial, Final, Middle { /* ... */ }

record Initial(CharSequence seq, int end) implements SubSequence { /* ... */ }
record Final(CharSequence seq, int start) implements SubSequence { /* ... */ }
record Middle(CharSequence seq, int start, int end) implements SubSequence { /* ... */ }
```

Of course, we need to implement the `CharSequence` methods. That's easily done in the superinterface with a pattern match:

```
default int length() {
   return switch (this) {
      case Initial(var __, var end) -> end;
      case Final(var seq, var start) -> seq.length() - start();
      case Middle(var __, var start, var end) -> end - start;
   };
}
```

No `default` is required because we provided cases for all classes that implement the sealed interface.

Note the double underscore for the variables that we don't care about. (A single underscore is a Java keyword, held in reserve for future use.)

The following sandbox contains the complete example. Note that a record pattern can have a guard:

```
case Initial(var seq, var end) when s == 0
```

{{< sandbox version=java19 preview="true" mainclass="Main" >}}{{< sandboxsource "Main.java" >}}
import java.util.*;

sealed interface SubSequence extends CharSequence permits Initial, Final, Middle {
   CharSequence seq();

   default int start() { return 0; }
   default int end() { return seq().length(); }

   default char charAt(int index) {
      Objects.checkIndex(index, length());
      return seq().charAt(start() + index);
   }

   default int length() {
      return switch (this) {
         case Initial(var __, var end) -> end;
         case Final(var seq, var start) -> seq.length() - start();
         case Middle(var __, var start, var end) -> end - start;
      };
   }
   
   default CharSequence subSequence(int s, int e) {
      return switch (this) {
         case Initial(var seq, var end) when s == 0 -> new Initial(seq, e);
         case Final(var seq, var start) when start + e == seq.length() -> new Final(seq, start + s);
         default -> new Middle(seq(), start() + s, start() + e);
      };
   }
}

record Initial(CharSequence seq, int end) implements SubSequence {
   public Initial { Objects.checkIndex(end, seq.length()); }
   public String toString() { return seq.subSequence(0, end).toString(); }
}

record Final(CharSequence seq, int start) implements SubSequence {
   public Final { Objects.checkIndex(start, seq.length()); }
   public String toString() { return seq.subSequence(start, seq.length()).toString(); }
}

record Middle(CharSequence seq, int start, int end) implements SubSequence {
   public Middle { Objects.checkFromToIndex(start, end, seq.length()); }
   public String toString() { return seq.subSequence(start, end).toString(); }
}

public class Main {
   public static void main(String[] args) {
      CharSequence seq = new Final("Mississippi", 6);    
      System.out.println(seq.length());
      System.out.println(seq.subSequence(0, 3));
      System.out.println(seq.subSequence(0, 3).getClass().getName());
   }
}

{{< /sandboxsource >}}
{{< /sandbox >}}

## Nested Matches

Since `Initial`, `Middle`, and `Final` are themselves `CharSequence` instances, one can take, for example, the `Final` of an `Initial` of a sequence. Such a nesting can be simplified to a `Middle` of the original sequence:

```
static CharSequence simplify(SubSequence seq) {
   return switch (seq) {
      // ...
      case Initial(Final(var cs, var s1), var e2) ->
         new Middle(cs, s1, s1 + e2);
      // ...
   }
}
```

Note the convenient nested match that describes exactly the structure that we want to target.

You can only match nested record patterns, not values. For example, the following are forbidden:

```
case Final(var cs, 0) -> ...; // Error
case Final(null, var e) -> ...; // Error
```

A `switch` can match `0`  or `null` at the top level, but not when it is nested. Instead, use a guard:

```
case Final(var cs, var s) when s == 0 -> cs;
```

You can also define bindings for an entire record, as you do in a type pattern:

```
case Initial(Final(var cs, var s1) fseq, var e2) iseq -> ...
```

Now `fseq` and `iseq` refer to the matched records.

This sandbox has the complete definition of the `simplify` method. The details are fussy, but have a look at the overall structure and the elegance of the variable extraction, guards, and pattern nesting.

{{< sandbox version=java19 preview="true" mainclass="Main" >}}{{< sandboxsource "Main.java" >}}
public class Main {
   public static CharSequence simplify(SubSequence seq) {
      return switch (seq) {
         case Initial(var cs, var e) when e == cs.length() -> cs;
         case Final(var cs, var s) when s == 0 -> cs;
         case Middle(var cs, var s, var e) when s == 0 && e == cs.length() -> cs;
         case Initial(Initial(var cs, var e1), var e2) ->
            new Initial(cs, e2);
         case Initial(Middle(var cs, var s1, var e1), var e2) ->
            new Middle(cs, s1, s1 + e2);
         case Initial(Final(var cs, var s1), var e2) ->
            new Middle(cs, s1, s1 + e2);
         case Middle(Initial(var cs, var e1), var s2, var e2) ->
            new Middle(cs, s2, e2);
         case Middle(Middle(var cs, var s1, var e1), var s2, var e2) ->
            new Middle(cs, s1 + s2, s1 + e2);
         case Middle(Final(var cs, var s1), var s2, var e2) ->
            new Middle(cs, s1 + s2, s1 + e2);
         case Final(Initial(var cs, var e1), var s2) ->
            new Middle(cs, s2, e1);
         case Final(Middle(var cs, var s1, var e1), var s2) ->
            new Middle(cs, s1 + s2, e1);
         case Final(Final(var cs, var s1), var s2) ->
            new Final(cs, s1 + s2);
         default -> seq;
      };      
   }
   
   public static void main(String[] args) {
      var result = simplify(new Final(
         new Initial("Mississippi", 6), 3));
      System.out.println(result + " " + result.getClass().getName());
   }
}
{{< /sandboxsource >}}
{{< sandboxsource "SubSequence.java" >}}
import java.util.*;

public sealed interface SubSequence extends CharSequence permits Initial, Final, Middle {
   CharSequence seq();

   default int start() { return 0; }
   default int end() { return seq().length(); }

   default char charAt(int index) {
      Objects.checkIndex(index, length());
      return seq().charAt(start() + index);
   }

   default int length() {
      return switch (this) {
         case Initial(var __, var end) -> end;
         case Final(var seq, var start) -> seq.length() - start();
         case Middle(var __, var start, var end) -> end - start;
      };
   }
   
   default CharSequence subSequence(int s, int e) {
      return switch (this) {
         case Initial(var seq, var end) when s == 0 -> new Initial(seq, e);
         case Final(var seq, var start) when start + e == seq.length() -> new Final(seq, start + s);
         default -> new Middle(seq(), start() + s, start() + e);
      };
   }
}

record Initial(CharSequence seq, int end) implements SubSequence {
   public Initial { Objects.checkIndex(end, seq.length()); }
   public String toString() { return seq.subSequence(0, end).toString(); }
}

record Final(CharSequence seq, int start) implements SubSequence {
   public Final { Objects.checkIndex(start, seq.length()); }
   public String toString() { return seq.subSequence(start, seq.length()).toString(); }
}

record Middle(CharSequence seq, int start, int end) implements SubSequence {
   public Middle { Objects.checkFromToIndex(start, end, seq.length()); }
   public String toString() { return seq.subSequence(start, end).toString(); }
}

{{< /sandboxsource >}}
{{< /sandbox >}}

## Generics

A pattern for a generic type must use a type parameter. For example, given

```
record Pair<T>(T first, T second) {
   public static <U> Pair<U> of(U first, U second) { return new Pair<U>(first, second); }
}
```

the type pattern

```
Pair<String>(var first, var second)
```

is ok, as is

```
Pair<?>(String first, String second)
```

or even

```
Pair<?>(var first, var second)
   // The variables first and second have type Object
```

But a “raw” type pattern is a compile-time error:

```
Pair(String first, String second) // Error
```

When generic types are involved, the compiler may need to work pretty hard to verify exhaustiveness. Consider this incomplete hierarchy of JSON types:

```
sealed interface JSONValue {}
sealed interface JSONPrimitive<T> extends JSONValue {}
record JSONNumber(double value) implements JSONPrimitive<Double> {}
record JSONBoolean(boolean value) implements JSONPrimitive<Boolean> {}
record JSONString(String value) implements JSONPrimitive<String> {}
```

The `switch` in the following method is exhaustive:

```
public static <T> double toNumber(JSONPrimitive<T> v) {
   return switch (v) {
      case JSONNumber(var n) -> n;
      case JSONBoolean(var b) -> b ? 1 : 0;
      case JSONString(var s) -> {
         try {
            yield Double.parseDouble(s);
         } catch (NumberFormatException __) {
            yield Double.NaN;
         }
      }
   };
}
```

At first glance, it appears as if there might be an unbounded number of classes implementing `JSONPrimitive<T>`, but the compiler can track than there are only three of them.

Conversely, the compiler can tell that this switch is not exhaustive:

```
public static Object sum1(Pair<? extends JSONPrimitive<?>> pair) {
   return switch (pair) {
      case Pair<?>(JSONNumber(var left), JSONNumber(var right)) -> left + right;
      case Pair<?>(JSONBoolean(var left), JSONBoolean(var right)) -> left | right;
      case Pair<?>(JSONString(var left), JSONString(var right)) -> left.concat(right);
      // Compiler detects that the switch is not exhaustive
   };
}
```

After all, it would be possible to call this method as 

```
sum1(Pair.of(new JSONNumber(42), new JSONString("Fred")))
```

The compiler notices that these mixed pairs are not covered. That is good.

Here is an attempt to only accept homogeneous pairs:

```
public static <T extends JSONPrimitive<U>, U> Object sum2(Pair<T> pair) {
   return switch (pair) {
      // Error—these generic types do not match Pair<T>
      case Pair<JSONNumber>(JSONNumber(var left), JSONNumber(var right)) -> left + right;
      case Pair<JSONBoolean>(JSONBoolean(var left), JSONBoolean(var right)) -> left | right;
      case Pair<JSONString>(JSONString(var left), JSONString(var right)) -> left.concat(right);
   };
}
```

However, this does not work. The compiler refuses to match `Pair<T>` with `Pair<JSONNumber>`, and rightly so. After all, there are other valid choices for `T`, such as `JSONPrimitive<Double>`. 

Moreover, the types are erased at runtime. The `switch` can only check whether *some* `Pair` holds two `JSONNumber` instances.

In light of erasure, it actually makes sense to always use unbounded wildcards, like this: 

```
public static <T extends JSONPrimitive<U>, U> Object sum3(Pair<T> pair) {
   return switch (pair) {
      case Pair<?>(JSONNumber(var left), JSONNumber(var right)) -> left + right;
      case Pair<?>(JSONBoolean(var left), JSONBoolean(var right)) -> left | right;
      case Pair<?>(JSONString(var left), JSONString(var right)) -> left.concat(right);
      default -> throw new AssertionError(); // Sadly Java can't tell this won't happen
   };
}
```

Unfortunately, the `default` clause is still necessary. In theory, there is enough information to determine that the pair components must be instances of the same type, but the Java type system can't prove it.

Also, you cannot sharpen the return type to `U`:

```
public static <T extends JSONPrimitive<U>, U> U sum3(Pair<T> pair)
```

This too exceeds the capabilities of the Java type system. In Scala 3, these inferences work, but I am told that it took nontrivial work to get there.

Here is a sandbox so that you can play with the code of this section.

{{< sandbox version=java19 preview="true" mainclass="Main" >}}{{< sandboxsource "Main.java" >}}
sealed interface JSONValue {}
sealed interface JSONPrimitive<T> extends JSONValue {}
record JSONNumber(double value) implements JSONPrimitive<Double> {}
record JSONBoolean(boolean value) implements JSONPrimitive<Boolean> {}
record JSONString(String value) implements JSONPrimitive<String> {}

record Pair<T>(T left, T right) {
   public static <U> Pair<U> of(U left, U right) { return new Pair<U>(left, right); }
}

public class Main {
   public static <T> double toNumber(JSONPrimitive<T> v) {
      return switch (v) {
         case JSONNumber(var n) -> n;
         case JSONBoolean(var b) -> b ? 1 : 0;
         case JSONString(var s) -> {
            try {
               yield Double.parseDouble(s);
            } catch (NumberFormatException __) {
               yield Double.NaN;
            }
         }
      };
   }

   public static Object sum1(Pair<? extends JSONPrimitive<?>> pair) {
      return switch (pair) {
         case Pair<?>(JSONNumber(var left), JSONNumber(var right)) -> left + right;
         case Pair<?>(JSONBoolean(var left), JSONBoolean(var right)) -> left | right;
         case Pair<?>(JSONString(var left), JSONString(var right)) -> left.concat(right);
         // Compiler correctly detects that the switch is not exhaustive
         // Comment out the following line to verify
         default -> null;
      };
   }

   /* 
   public static <T extends JSONPrimitive<U>, U> Object sum2(Pair<T> pair) {
      return switch (pair) {
         // Error—these generic types do not match Pair<T>
         case Pair<JSONNumber>(JSONNumber(var left), JSONNumber(var right)) -> left + right;
         case Pair<JSONBoolean>(JSONBoolean(var left), JSONBoolean(var right)) -> left | right;
         case Pair<JSONString>(JSONString(var left), JSONString(var right)) -> left.concat(right);
      };
   }
   */

   public static <T extends JSONPrimitive<U>, U> Object sum3(Pair<T> pair) {
      return switch (pair) {
         case Pair<?>(JSONNumber(var left), JSONNumber(var right)) -> left + right;
         case Pair<?>(JSONBoolean(var left), JSONBoolean(var right)) -> left | right;
         case Pair<?>(JSONString(var left), JSONString(var right)) -> left.concat(right);
         default -> throw new AssertionError(); // Sadly Java can't tell this won't happen
      };
   }

   /*
   // This won't compile even though it is sound
   public static <T extends JSONPrimitive<U>, U> U sum3(Pair<T> pair) {
      return switch (pair) {
         case Pair<?>(JSONNumber(var left), JSONNumber(var right)) -> left + right;
         case Pair<?>(JSONBoolean(var left), JSONBoolean(var right)) -> left | right;
         case Pair<?>(JSONString(var left), JSONString(var right)) -> left.concat(right);
         default -> throw new AssertionError(); // Sadly Java can't tell this won't happen
      };
   }
   */

   public static void main(String[] args) {
      System.out.println(toNumber(new JSONString("42")));
      System.out.println(sum1(Pair.of(new JSONNumber(29), new JSONNumber(13))));
      // This won't compile, and it shouldn't
      // System.out.println(sum1(Pair.of(new JSONNumber(29), new JSONString("13"))));
   }
}

{{< /sandboxsource >}}
{{< /sandbox >}}

## Match Exceptions

Consider a record pattern match:

```
switch (cs) {
   case Initial(var s, var n) -> ...
   ...
}
```

When this code runs, it makes an instanceof test, a cast, and then invokes the record's component methods:

```
cs instanceof Initial
```

When that test succeeds, the component accessors of the record are invoked:

```
var s = ((Initial) cs).seq()
var n = ((Initial) cs).end()
```

What if those methods throw an exception?

In that case, the `switch` throws a `MatchError` whose cause is that exception. Check it out in this sandbox:

{{< sandbox version=java19 preview="true" mainclass="Main" >}}{{< sandboxsource "Main.java" >}}
import java.util.*;

sealed interface SubSequence extends CharSequence permits Initial, Final, Middle {
   CharSequence seq();

   default int start() { return 0; }
   default int end() { return seq().length(); }

   default char charAt(int index) {
      Objects.checkIndex(index, length());
      return seq().charAt(start() + index);
   }

   default int length() {
      return switch (this) {
         case Initial(var __, var end) -> end;
         case Final(var seq, var start) -> seq.length() - start();
         case Middle(var __, var start, var end) -> end - start;
      };
   }
   
   default CharSequence subSequence(int s, int e) {
      return switch (this) {
         case Initial(var seq, var end) when s == 0 -> new Initial(seq, e);
         case Final(var seq, var start) when start + e == seq.length() -> new Final(seq, start + s);
         default -> new Middle(seq(), start() + s, start() + e);
      };
   }
}

record Initial(CharSequence seq, int end) implements SubSequence {
   public int end() {
      Objects.checkIndex(end, seq.length());
      return end;
   }
   public String toString() { return seq.subSequence(0, end).toString(); }
}

record Final(CharSequence seq, int start) implements SubSequence {
   public int start() {
      Objects.checkIndex(start, seq.length());
      return start;
   }
   public String toString() { return seq.subSequence(start, seq.length()).toString(); }
}

record Middle(CharSequence seq, int start, int end) implements SubSequence {
   public int start() {
      Objects.checkFromToIndex(start, end, seq.length());
      return start;
   }
   public int end() {
      Objects.checkFromToIndex(start, end, seq.length());
      return end;
   }
   public String toString() { return seq.subSequence(start, end).toString(); }
}

public class Main {
   public static void main(String[] args) {
      CharSequence seq = new Middle("Mississippi", 5, 30);    
      System.out.println(seq.length());
   }
}

{{< /sandboxsource >}}
{{< /sandbox >}}

Rémi Forax has a [more amusing example](https://mail.openjdk.org/pipermail/amber-spec-observers/2022-June/003626.html) which generates a linked list of match errors.

I think it is best not to override component accessors so that they throw exceptions. It is ok to throw an exception in the constructor when construction parameters are `null` or out of range. But once the record instance is constructed, one should be able to extract its state, no matter what it is.

## How Momentous Are Record Patterns?

Records are nice when they match your needs—a class that describes immutable objects that are “just data”. As of today, the JDK has one of them. Of course, they are a recent feature, so surely there will be more to come. You probably have a few classes in your code base that would work well as records, and maybe you have started declaring your own.

For record patterns to be useful, you need multiple records that implement a common interface. You saw a nontrivial example with the `SubSequence` records. Another example is an expression hierarchy with records `Sum`, `Difference`, `Product`, `Quotient`. Or a functional list or tree with a record for the nodes. These examples are, depending on your point of view, foundational or academic. Either way, they are unlikely to feature prominently in your business logic.

Record patterns are a piece of the pattern matching toolset that Java is building up. Useful libraries may emerge in the future that make good use of them. Even more so when Java supports value types. A JSON library would be a plausible example. Keep record patterns on your radar, but pay no attention to those who tell you that they are a crucial reason to update your JDK.

## References

* [JEP 405: Record Patterns (Preview), OpenJDK](https://openjdk.java.net/jeps/405)
* [JEP 427: Pattern Matching for switch (Third Preview), OpenJDK](https://openjdk.java.net/jeps/427)
