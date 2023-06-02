---
title: Pattern Matching for switch (JEP 427)
copyright: Cay S. Horstmann 2022. All rights reserved.
jep: 427
jdkversion: 19
---


Pattern matching for switch expressions and statements appeared as a preview feature in Java 17 ([JEP 406](https://openjdk.java.net/jeps/406)) and Java 18 ([JEP 420](https://openjdk.java.net/jeps/420)). This article covers the third round of preview in Java 19 ([JEP 427](https://openjdk.java.net/jeps/427)). The feature is mostly straightforward, with a few sharp edges. At the end of each section is a “sandbox” with somewhat contrived code to try out the syntax variations.

## Type Checks with Switch

If you have many branches that check the same value, it can be clearer to refactor the code as a `switch:`

```
if (code == 200) message = "Ok";
else if (code == 301) message = "Moved permanently";
else if (code == 404) message = "Not found";
...
```

becomes

```
message = switch (code) {
   case 200 -> "Ok";
   case 301 -> "Moved permanently";
   case 404 -> "Not found;
   ...
}
```

A sequence of type checks can be similarly repetitive: 

```
if (out instanceof ByteArrayOutputStream bout) bout.writeBytes(str.getBytes());
else if (out instanceof DataOutputStream dout) dout.writeUTF(str);
else if (out instanceof ObjectOutputStream oout) oout.writeObject(str); 
else out.write(str.getBytes()); 
```

Note the use of [pattern matching for `instanceof`](https://javaalmanac.io/features/instanceof-patterns/). The code snippet declares variables (`bout`, `dout`, `oout`) that contain `out`, cast to the matching type.

The equivalent pattern matching `switch` is:

```
switch (out) {
   case ByteArrayOutputStream bout -> bout.writeBytes(str.getBytes());
   case DataOutputStream dout -> dout.writeUTF(str);
   case ObjectOutputStream oout -> oout.writeObject(str);
   default -> out.write(str.getBytes());
}
```

This example uses a statement/no fall through `switch`. There are  [three more forms](https://javaalmanac.io/features/switch/): expression/no fall through, statement/fall through, and expression/fall through. You can use type patterns with all of them.

You *must* use a variable after the type, even if you don't need it. In the preceding example, you cannot replace the `default` case with 

```
case OutputStream -> out.write(str.getBytes()); // Error—no variable
```

Instead, you can use 

```
case OutputStream __ -> ...
```

Note the double underscore. A single underscore is a keyword (held for future use).

In the sandbox, try using fall through for the second `switch`. Try using a `default` in the last case.

{{< sandbox version=java19 preview="true" mainclass="Main" >}}{{< sandboxsource "Main.java" >}}
import java.io.*;
import java.util.*;

public class Main {
   public static void main(String[] args) throws Exception {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      run(bytes, bytes);
      bytes = new ByteArrayOutputStream();
      run(new DataOutputStream(bytes), bytes);
      bytes = new ByteArrayOutputStream();
      run(new ObjectOutputStream(bytes), bytes);
      run(System.out, null);
   }

   public static void run(OutputStream out, ByteArrayOutputStream bytes) throws Exception {
      System.out.printf("%n=== %s ===%n", out.getClass().getName());
      String str = "Hello\n";

      // A switch statement with no fall through, type patterns
      switch (out) {
         case ByteArrayOutputStream bout -> bout.writeBytes(str.getBytes());
         case DataOutputStream dout -> dout.writeUTF(str);
         case ObjectOutputStream oout -> oout.writeObject(str);
         case OutputStream __ -> out.write(str.getBytes());
      };

      if (bytes != null) { 
         bytes.close();
         System.out.println(Arrays.toString(bytes.toByteArray()));
      }   
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

## Guards

Sometimes, it is convenient to select a case of a `switch` only when a certain condition is fulfilled. Such a condition is called a *guard*. The contextual keyword `when` introduces a guard:

```
switch (out) {
   case ByteArrayOutputStream bout -> bout.writeBytes(str.getBytes());
   case DataOutputStream dout -> dout.writeUTF(str);
   case ObjectOutputStream oout when str.length() > 0 -> oout.writeObject(str);
   default -> out.write(str.getBytes());
}
```

If the guard condition is not fulfilled, the case is not selected and the next case is tested.

In the [Java 18 preview](https://openjdk.java.net/jeps/420), guards were written as 

```
case ObjectOutputStream oout && str.length() > 0
```

It seemed reasonable to use `&&` to combine multiple tests, but there were subtle issues. The current design is similar to Scala `match` expressions, where the `if` keyword is used for guards.

This sandbox demonstrates a typical use of type patterns. An XML node can be an element, text node, comment, entity reference, processing instruction, or one of several other exotic things. This code only handles the first two, leaving the others as an exercise to the reader. Note the `when` clause for skipping whitespace.

{{< sandbox version=java19 preview="true" mainclass="Main" >}}{{< sandboxsource "Main.java" >}}
import org.w3c.dom.*;
import org.xml.sax.*;
import java.io.*;
import javax.xml.parsers.*;

public class Main {
   public static StringBuilder print(Node node, int indent) {
      var result = new StringBuilder();
      switch (node) {
         case Text t when !t.getData().isBlank() -> 
            result.append(" ".repeat(indent))
              .append(t.getData())
              .append("\n");
         case Element e -> {
            result.append(" ".repeat(indent))
               .append("<")
               .append(e.getTagName())
               .append(">\n");
            var children = e.getChildNodes();
            for (int i = 0; i < children.getLength(); i++)
               result.append(print(children.item(i), indent + 2));
            result.append(" ".repeat(indent))
               .append("</")
               .append(e.getTagName())
               .append(">\n");
         }
         default -> {}
      }
      return result;
   }

   public static void main(String[] args) throws Exception {
      var xml = "<div>Hello, <em>World</em></div>";
      var doc = DocumentBuilderFactory
         .newInstance()
         .newDocumentBuilder()
         .parse(new InputSource(new StringReader(xml)))
         .getDocumentElement();
      System.out.println(print(doc, 0));
   }
}

{{< /sandboxsource >}}
{{< /sandbox >}}

## Null Handling

The classic `switch` throws a `NullPointerException` when the tested value is `null`. That makes sense when switching on strings or enumerations. But with a type match, the issue is less clear. Ever since Java 1.0, `instanceof` tests have been tolerant of `null`. A test such as `null instanceof String` simply returns `false`.

You can now add a `null` case to a `switch`. In that case, the switch does not throw a `NullPointerException`.

A `null` case can be as simple as

```
case null -> ...
```

You can also combine it with type tests:

```
switch (obj) { 
   case String s, null -> ... // s is obj cast as a String or null
   ...
}
```

The order doesn't matter: `case null, String s -> ...` does the same.

In the fall through form, you use

```
case String s, null: 
```

or

```
case String s:
case null:
```

You can also group the `null` and `default` cases:

```
case null, default -> ...
```

In this sandbox, fix the second switch so that it doesn't throw a `NullPointerException` if `ex` is `null`!

{{< sandbox version=java19 preview="true" mainclass="Main" >}}{{< sandboxsource "Main.java" >}}
import java.io.*;
import java.sql.*;
import java.util.*;

public class Main {
   public static void main(String[] args) {
      run(new NullPointerException());
      run(new SQLException(new MissingResourceException("No database", null, null)));
      run(new IOException("File not found"));
      run(null);
   }

   public static void run(Throwable ex) {
      System.out.printf("%n=== %s ===%n", ex == null ? "null" : ex.getClass().getName());

      Throwable ex2 = switch (ex) {
         case IOException ioe -> new UncheckedIOException(ioe);
         case Exception __ when ex.getCause() != null -> ex.getCause();
         default -> ex;
      };

      ex2.printStackTrace();
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

## Dominance

Before type patterns, cases of a `switch` were always disjoint. It was a compile-time error to include the same constant in multiple cases. However, type patterns can overlap:

```
switch (out) {
   case Appendable app -> ...
   case Closeable cl -> ...
   default -> ...
}
```

There are classes that implement both the `Appendable` and `Closeable` interface, such as `PrintStream`. Then the *first* matching case applies. Other matching cases are not executed (unless execution happens to fall through).

It is an error if a case is unreachable. Consider this example:

```
switch (ex) {
   case RuntimeException rex -> ...
   case NullPointerException nex -> ... // Error
   default -> ...
}
```

The second case is unreachable since `NullPointerException` is a subtype of `RuntimeException`. We say that the first case *dominates* the second. This is a compile-time error.

An unguarded type pattern dominates a pattern with the same case and a guard:

```
switch (ex) {
   case Exception __ -> ... 
   case Exception __ when ex.getCause() != null -> ... // Error
   default -> ...
}
```

The second case can never execute, and a compile-time error occurs.

Since the compiler cannot determine when a guard is true, the guards are never used for dominance checking. Consider

```
case Integer n when n >= 600 -> ... 
case Integer n when n > 599 -> ... // Not a compile-time error
```

The second case can never execute. But the compiler does not know that.

A type pattern dominates a constant pattern. Guards are ignored. Both 

```
case Integer n
case Integer n where n > 600
```

dominate

```
case 404
```

The point is that you should list constant patterns first, then type patterns:

```
case 200 -> ...
case 404 -> ...
case Integer n when n >= 600 -> ...
case Integer n -> ...
```

Weirdly enough, the `default` case doesn't dominate anything, and you can place it anywhere:  

```
case 200 -> ...
case 404 -> ...
default -> ... // Ok
case Integer n when n >= 600 -> ...
```

With the no fall through form, there is no reason to do this. Just put the `default` case last.

By the way, the rules for constant cases have not changed. The constants *and selector* must be of type `int`, `char`, `short`, `byte` or their wrapper classes, `String`, or an enumerated type.

You can't have 

```
case System.out -> ...
```

or 

```
Number num = ...
switch (num) {
   case 404 -> ...
   ...
}
```

Here is a little exercise to practice the dominance rules. And yes, it is weird that you can use `case Object n` or `case Number n` when the only possible type match is `Integer`.

{{< sandbox version=java19 preview="true" mainclass="Main" >}}{{< sandboxsource "Main.java" >}}
public class Main {
   public static void main(String[] args) {
      run(200);
      run(404);
      run(500);
      run(600);
   }

   public static void run(Integer status) {
      System.out.printf("%n=== %d ===%n", status);

      String text = switch (status) {
         // Sort these lines according to the dominance rules
         default -> "Valid";
         case Number n when n.intValue() >= 600 -> "Invalid";
         case 200 -> "Ok";
         case 404 -> "Not found";
      };

      System.out.println(text);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

## Exhaustiveness

A switch *expression* must be *exhaustive*; that is, yield a value for every input. Of course, any switch with a `default` case is exhaustive.

If the switch input is a [sealed type](https://javaalmanac.io/features/sealedtypes/), there is a known, finite number of subtypes. The switch is exhaustive if there are type patterns covering all subtypes.

It is possible that a sealed type evolves, acquiring additional subtypes. Then a switch over that type may no longer be exhaustive. That is problematic if the source file containing the switch is not recompiled. After all, it might be in a third party JAR. In order to detect such a scenario at runtime, the compiler adds a `default` case that throws an `IncompatibleClassChangeError`.

The compiler cannot interpret guards, so you need to have at least one unguarded pattern. For example, 

```
case Integer n when n >= 600 -> ...
case Integer n when n < 600 -> ...
```

is not exhaustive. Rewrite it as follows:

```
case Integer n when n >= 600 -> ...
case Integer n -> ...
```

Even though there is no technical need for switch *statements* to be exhaustive, the compiler will check that all “modern” switch statements are. That applies to any switch statement that uses type or `null` patterns. You may need to add 

```
default: break;
```

or

```
default -> {}
```

In a switch statement with only constant cases, there is no exhaustiveness check. All your old switches will compile as usual.

This sandbox shows exhaustiveness checking with sealed classes. Try adding another subclass `JSONComment`. (I know, JSON won't ever have comments.)

{{< sandbox version=java19 preview="true" mainclass="Main" >}}{{< sandboxsource "Main.java" >}}
public class Main {
   public static void main(String[] args) {
      JSONArray arr = new JSONArray();
      arr.add(new JSONNumber(13));
      arr.add(JSONNull.INSTANCE);
      JSONObject obj = new JSONObject();
      obj.put("name", new JSONString("Harry"));
      obj.put("married", JSONBoolean.FALSE);
      run(arr);
      run(obj);
      run(arr.get(0));
      run(obj.get("name"));
      run(obj.get("married"));      
      run(arr.get(1));
   }

   public static void run(JSONValue jval) {
      var type = switch (jval) {
         case JSONArray __ -> "array";
         case JSONObject __ -> "object";
         case JSONNumber __ -> "number";
         case JSONString __ -> "string";
         case JSONBoolean __ -> "boolean";
         case JSONNull __ -> "null";
      };
      System.out.println(type + " " + jval);
   }
}
{{< /sandboxsource >}}
{{< sandboxsource "JSONValue.java" >}}
import java.util.*;

public sealed interface JSONValue permits JSONArray, JSONObject, JSONPrimitive {}

final class JSONArray extends ArrayList<JSONValue>  implements JSONValue {}

final class JSONObject extends HashMap<String, JSONValue> implements JSONValue {}

sealed interface JSONPrimitive extends JSONValue
      permits JSONNumber, JSONString, JSONBoolean, JSONNull {}

final record JSONNumber(double value) implements JSONPrimitive {}

final record JSONString(String value) implements JSONPrimitive {}

enum JSONBoolean implements JSONPrimitive {
   FALSE, TRUE;
}

enum JSONNull implements JSONPrimitive {
   INSTANCE;
}

{{< /sandboxsource >}}
{{< /sandbox >}}

## Variable Scope

A type pattern introduces a variable. You can use that variable in a `when` clause:

```
case String s when s.length() > 3 -> ...
```

You can also use the variable in the code to the right of the `->` or `:` token.

```
case String s when s.length() > 3 -> s.substring(0, 3) + "..."
```

This is unsurprising. The only potentially confusing situation comes from fall through. Consider:

```
case Number n: ... // Must have break/yield here
case String s when s.length() > 3: ... // No break/yield required
default: ...
```

You cannot fall through a type pattern. That is, you cannot fall into the the second case. After all, falling through skips the test and goes directly into the code that follows. However, you can fall through from a type pattern into another case that isn't a type pattern. In the preceding example, it is ok to fall through the default.

Here is a complete example, as contrived as all fall through examples that I have ever seen.

{{< sandbox version=java19 preview="true" mainclass="Main" >}}{{< sandboxsource "Main.java" >}}
import java.text.*;
import java.util.*;

public class Main {
   public static void main(String[] args) {
      Locale loc = Locale.forLanguageTag("de-DE");
      run("Bob", loc);
      run("Fred", loc);
      run("Math.PI", loc);
      run(loc, loc);
   }

   public static void run(Object obj, Locale loc) {
      System.out.printf("%n=== %s ===%n", obj);

      String formatted = switch (obj) {
         case Number n: yield NumberFormat.getNumberInstance(loc).format(n);
         case String s when s.length() > 3: obj = s.substring(0, 3) + "...";
         default: yield obj.toString();
         case String s: yield '"' + s + '"';
      };

      System.out.println(formatted);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

## How momentous are type patterns?

Type patterns provide a concise way of formulating repeated `instanceof` tests. How often do you use `instanceof`? The JDK source of over 5 million lines has just over 11,000 `instanceof` tests, of which 10% were preceded by `else`.

Clearly, there are times where type tests are necessary. Reviewing the JDK source revealed some common themes. Heterogeneous tree structures (XML, menus and submenus, parse trees). Ad-hoc formatting/parsing of strings, numbers, dates, arrays, and so on. Ad-hoc polymorphism with input sources and output targets. Special handling of certain user interface components. Analyzing exceptions. Handling the results from reflective calls.

My verdict: Nice to have in those cases, but not something that most people will use a lot.

As you saw, the devil is in the details. Put `default` last. Stay away from fall through!

## References

* [JEP 394: Pattern Matching for instanceof, OpenJDK](https://openjdk.java.net/jeps/394)
* [JEP 406: Pattern Matching for switch (Preview), OpenJDK](https://openjdk.java.net/jeps/406)
* [JEP 420: Pattern Matching for switch (Second Preview), OpenJDK](https://openjdk.java.net/jeps/420)
* [JEP 427: Pattern Matching for switch (Third Preview), OpenJDK](https://openjdk.java.net/jeps/427)
