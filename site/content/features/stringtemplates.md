---
title: String Templates (JEP 430)
copyright: Cay S. Horstmann 2023. All rights reserved.
jep: 430
jdkversion: 21
type: "sandbox"
---


String templates, previewed in Java 21, are a mechanism for producing objects from templates that contain string fragments and embedded expressions. The syntax is different from that of other languages, but the differences are minor and make sense for Java. The JDK provides processors for plain interpolation and formatted output. It is easy to implement your own processors.
      

## String Interpolation

Many strings are composed of fixed and variable parts. In Java, such strings are traditionally formed through concatenation:

```
String message = "Hello, " + name + "! Next year, you'll be " + (age + 1) + ".";
```

That is tedious to read and write, and a bit error-prone since it is easy to mess up spaces and quotation marks.

Formatted output can be a better alternative:

```
message = "Hello, %s! Next year, you'll be %d.".formatted(name, age + 1);
```

or

```
message = MessageFormat.format("Hello, {0}! Next year, you''ll be {1,choice,0≤{1}|50<even wiser}.", 
   name, age + 1);
```

Now the contents of the composite string is easier to make out, but the variable parts are separated from their final location.

Many programming languages provide *interpolation* of expressions that are embedded inside a string. (The Merriam-Webster dictionary [defines “interpolate”](https://www.merriam-webster.com/dictionary/interpolate) as “to insert between other things or parts”.) Java 21 adds a preview feature with the same capability. Here is how it looks like: 

```
message = STR."Hello, \{name}! Next year, you'll be \{age + 1}.";
```

`STR` is a *template processor* and `"Hello, \{name}! Next year, you'll be \{age + 1}."` is a *string template* with *embedded expressions* `\{name}` and `\{age + 1}`. The entire expression on the right hand side of the assignment is a *template expression*.

Here is a sandbox to play with these alternatives.

{{< sandbox version=java21 preview="true" mainclass="Demo" >}}{{< sandboxsource "Demo.java" >}}
import java.text.MessageFormat;

public class Demo {
   public static void main(String[] args) {
      String name = "Fred";
      int age = 42;
      String message = "Hello, " + name + "! Next year, you'll be " + (age + 1) + ".";
      System.out.println(message);
      message = "Hello, %s! Next year, you'll be %d.".formatted(name, age + 1);
      System.out.println(message);
      message = MessageFormat.format("Hello, {0}! Next year, you''ll be {1,choice,0≤{1}|50<even wiser}.", name, age + 1);
      System.out.println(message);
      message = STR."Hello, \{name}! Next year, you'll be \{age + 1}.";
      System.out.println(message);
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

# Template Expressions

A template expression starts with an expression whose value is a template processor, an instance of a class that implements the `StringTemplate.Processor` interface. That interface has a single method `process`.

The template processor is followed by a dot, which the JEP describes unhelpfully as “a dot character (U+002E), as seen in other kinds of expressions.” Why a dot? Perhaps to remind the reader that this is a shorthand for a method call:

```
STR.process(templateArgument)
```

To the right of the dot is the *template argument*, which can be one of four things:

1. A *string template* (as in the example)
2. A *text block template*
3. A string literal
4. A text block

A string template is delimited by single quotes, just like a string literal, but it contains at least one embedded expression. (As it happens, `\{` is not a valid escape sequence in a string literal.)

Similarly, a text block template is just like a [text block](https://javaalmanac.io/features/textblocks/), but with embedded expressions. Here is an example:

```
STR."""
<div>
   <p class="greeting">Hello, \{name}!</p>
   <p>Next year, you'll be \{age + 1}.</p>
</div>"""
```

In both string templates and text block templates, you can add comments and line breaks inside  embedded expressions:

```
String message = STR."Hello, \{ name /* Ok to add a comment */ }! Next year, you'll be \{
      age + 1 // Ok to add line breaks
   }.";
```

A template is *not* a `String`. It is an instance of the  `StringTemplate` class. You will see soon what that class does. 

Unlike a string literal or text block, a template is only a valid expression when it follows a template processor and a dot. The processor is responsible for turning the text and embedded expressions of the template into a result. Without a processor, a template doesn't have a value.

You are also allowed to place a string literal or text block after a template processor and a dot. You can regard that as the special case of a template with zero embedded expressions. It can also be a temporary starting point that is later turned into a template:

```
message = STR."Hello, TODO! Next year, you'll be TODO.";
```

This sandbox shows the four kinds of template arguments.

{{< sandbox version=java21 preview="true" mainclass="TemplateArguments" >}}{{< sandboxsource "TemplateArguments.java" >}}
public class TemplateArguments {
   public static void main(String[] args) {
      String name = "Fred";
      int age = 42;

      // 1. String template
      String message = STR."Hello, \{ name /* Ok to add a comment */ }! Next year, you'll be \{
            age + 1 // Ok to add line breaks
         }.";
      System.out.println(message);

      // 2. Text block template
      String div = STR."""
<div>
   <p class="greeting">Hello, \{name}!</p>
   <p>Next year, you'll be \{age + 1}.</p>
</div>""";
      System.out.println(div);

      // 3. String literal
      message = STR."Hello, TODO! Next year, you'll be TODO.";
      System.out.println(message);

      // 4. Text block
      div = STR."""
<div>
   <p class="greeting">Hello, TODO!</p>
   <p>Next year, you'll be TODO.</p>
</div>""";
      System.out.println(div);
   }
}

{{< /sandboxsource >}}
{{< /sandbox >}}

# Predefined Template Processors

The Java 21 API contains three template processors: the interpolating `STR` processor that you have already seen, as well as processors for formatting and for obtaining an unprocessed template.

The `STR` processor, a static field of the class `java.lang.StringTemplate` is special—it is always imported. In other words, every Java file has these automatic imports:

```
import java.lang.*;
import static java.lang.StringTemplate.STR;
```

The other processors must be imported manually:

```
import static java.util.FormatProcessor.FMT;
import static java.lang.StringTemplate.RAW;
```

The `FMT` processor carries out `printf` style formatting:

```
String line = FMT."%-20s\{item.description} | %5d\{item.quantity} | %10.2f\{item.price}%n";
```

The format specifiers must immediately precede the embedded expressions.

The `RAW` processor yields the template argument as a `StringTemplate` instance:

```
StringTemplate template = RAW."Hello, \{name}! Next year, you'll be \{age + 1}.";
```

See the following section for more information about the `StringTemplate` class.

Note that a template processor can produce an object of any class. The `FMT` processor yields a `String`, but the `RAW` processor yields an object of the class `StringTemplate`.

Here is a sandbox with these examples. Try adding a space after a format specifier. Also try incrementing `age` after the template with the embedded expression `\{age + 1}` was formed. Is the value updated? Should it be?

{{< sandbox version=java21 preview="true" mainclass="TemplateProcessors" >}}{{< sandboxsource "TemplateProcessors.java" >}}
import static java.util.FormatProcessor.FMT;
import static java.lang.StringTemplate.RAW;

public class TemplateProcessors {
   record Item(String description, int quantity, double price) {}

   public static void main(String[] args) {
      String name = "Fred";
      int age = 42;
      var item = new Item("Blackwell Toaster", 2, 29.95);
      String line = FMT."%-20s\{item.description} | %5d\{item.quantity} | %10.2f\{item.price}%n";
      System.out.print(line);
      item = new Item("Zappa Microwave Oven", 1, 109.95);
      line = FMT."%-20s\{item.description} | %5d\{item.quantity} | %10.2f\{item.price}%n";
      System.out.print(line);
      StringTemplate template = RAW."Hello, \{name}! Next year, you'll be \{age + 1}.";
      System.out.println(template);
   }
}

{{< /sandboxsource >}}
{{< /sandbox >}}

## The `StringTemplate` class

When a template expression is evaluated, the template arguments are placed in an object that is then passed to the `format` method of the template processor.

The name `StringTemplate` is perhaps unfortunate.  *Any* of the four possible template argument types is turned into a `StringTemplate` instance. `TemplateArguments` might have been a better choice for the class name.

A `StringTemplate` instance stores this information:

* The *fragments*; that is, the strings between the embedded expressions
* The values of the embedded expressions

The `fragments` and `values` methods yield lists of fragments and values.

```
String name = "Fred";
int age = 42;
StringTemplate template = RAW."Hello, \{name}! Next year, you'll be \{age + 1}.";
template.fragments() // ["Hello, ", "! Next year, you'll be ", "."]
template.values() // ["Fred", 43]
```

Note that there is one more fragment than there are values.

The static `interpolate` method merges a list of fragments and values. As you will see in the next section, this is useful for implementing your  own template processors.

I don't think that many programmers will create `StringTemplate` instances, but there are three ways to do so:

* With the `RAW` processor:
  ```
  RAW."Hello, \{name}!"
  ```
* With the static `of` methods:
  ```
  StringTemplate.of(List("Hello, ", "!"), List(name))
  StringTemplate.of("Hello, Sailor!")
  ```
* With the static `combine` methods:
  ```
  StringTemplate.combine(RAW."Hello, \{name}! ", RAW."Next year, you'll be \{age + 1}.")
  ```

There are two `combine` methods, with parameter types `StringTemplate...` and `List<StringTemplate>`. 

Finally, the `process` method lets you apply a processor, just in case you prefer

```
template.process(processor)
```

over

```
processor.process(template)
```

This sandbox shows the API in action, even though you probably only need to worry about the first two methods. 

{{< sandbox version=java21 preview="true" mainclass="StringTemplateDemo" >}}{{< sandboxsource "StringTemplateDemo.java" >}}
import static java.lang.StringTemplate.RAW;
import java.util.List;

public class StringTemplateDemo {
   public static void main(String[] args) {
      String name = "Fred";
      int age = 42;
      StringTemplate template = RAW."Hello, \{name}! Next year, you'll be \{age + 1}.";
      System.out.println(template.fragments());
      System.out.println(template.values());

      // Two interpolate methods: one static, one instance
      System.out.println(StringTemplate.interpolate(
         template.fragments().stream().map(String::toUpperCase).toList(), template.values()));
      System.out.println(StringTemplate.of(
         template.fragments().stream().map(String::toUpperCase).toList(),
            template.values()).interpolate());

      // Demo of the of and combine methods.
      // For each entry in System.getProperties(), one StringTemplate is created.
      // They are all combined and then processed with the STR processor
      // Note that each StringTemplate has two fragments, and combine fuses
      // the second fragment with the first fragment of its successor. 
      template = StringTemplate.combine(
         System.getProperties()
            .entrySet()
            .stream()
            .map(e -> StringTemplate.of(List.of(e.getKey() + ": ", "\n"),
               List.of(e.getValue())))
            .toList());
      System.out.println(template.process(STR));
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

## Writing Your Own Template Processor

The `format` method of a template processor turns `StringTemplate` instances into objects. Since `StringTemplate.Processor` is a functional interface, you can construct an instance from a lambda expression. Here is a simple example that places the values in boxes:

{{< sandbox version=java21 preview="true" mainclass="MyFirstTemplateProcessor" >}}{{< sandboxsource "MyFirstTemplateProcessor.java" >}}
public class MyFirstTemplateProcessor {
   public static StringTemplate.Processor<String, RuntimeException> BOX =
      (StringTemplate template) -> {
         var result = new StringBuilder();
         for (int i = 0; i < template.fragments().size(); i++) {
            if (i > 0) {
               result.append('[');
               result.append("" + template.values().get(i - 1));
               result.append(']'); 
            }
            result.append(template.fragments().get(i));
         }
         return result.toString();
      };

   public static void main(String[] args) {
      String name = "Fred";
      int age = 42;
      System.out.println(BOX."Hello, \{name}! Next year, you'll be \{age + 1}.");
   }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

`StringTemplate.Processor` is a generic class with two type parameters: the result type and the type of the exception that the processor can throw. 

Here, the values are transformed and then interpolated with the fragments. The `interpolate` method is helpful in this common situation:

```
public static StringTemplate.Processor<String, RuntimeException> BOX =
   template -> StringTemplate.interpolate(template.fragments(),
      template.values().stream().map(v -> "[" + v + "]").toList());
```

As an aside, in the JEP, all template processors are written in uppercase, even if they are not static variables. This is not a requirement. The processor is an expression like any other. If a processor is a local variable, or a method invocation, it is perfectly fine to use lowercase letters. For example:

```
public static StringTemplate.Processor<String, RuntimeException> box(String left, String right) {
   return template -> StringTemplate.interpolate(template.fragments(),
      template.values().stream().map(v -> left + v + right).toList());
}
...
String result = box("{", "}")."Hello, \{name}! Next year, you'll be \{age + 1}.";
```

String processors can protect against injection attacks. When creating HTML, XML, JSON, SQL, and so on, from arbitrary values, you want to escape special characters such as quotation marks and bracket delimiters. The JEP has a useful example of using a `PreparedStatement` for a SQL query. This sandbox shows a simpler example for processing XML.

{{< sandbox version=java21 preview="true" mainclass="Xml" >}}{{< sandboxsource "Xml.java" >}}
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.w3c.dom.ls.*;
import org.xml.sax.*;

public class Xml {
   private static String escape(String str) {
      return str.replace("&", "&amp;")
         .replace("<", "&lt;")
         .replace(">", "&gt;")
         .replace("\"", "&quot;")
         .replace("'", "&apos;");
   }

   public static StringTemplate.Processor<Document, Exception> XML = 
      template -> {
         String escaped = StringTemplate.interpolate(template.fragments(),
            template.values().stream().map(String::valueOf).map(Xml::escape).toList());
         return parse(escaped);
   };

   // Here are two methods for parsing and printing XML. Don't pay too much attention
   // to the byzantine API from an earlier age...
   private static Document parse(String xml) throws SAXException, IOException, ParserConfigurationException {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      return builder.parse(new ByteArrayInputStream(xml.getBytes()));
   }

   private static String prettyPrint(Document doc) {
      DOMImplementation impl = doc.getImplementation();
      var implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
      LSSerializer ser = implLS.createLSSerializer();
      ser.getDomConfig().setParameter("format-pretty-print", true);
      return ser.writeToString(doc);
   }
   
   public static void main(String[] args) throws Exception {
      String name = "J.R. \"Bob\" Dobbs <dobbs@subgenius.com>";
      int age = 42;
      Document doc = XML."""
<div>
   <p class="greeting">Hello, \{name}!</p>
   <p>Next year, you'll be \{age + 1}.</p>
</div>""";
      System.out.println(prettyPrint(doc));
   }
}

{{< /sandboxsource >}}
{{< /sandbox >}}

Now it's your turn. Complete the code below so that one can write a `Map<String, Object>` as 

```
MAP."key1 \{value1} key2 \{value2} ..."
```

Trim the fragments and ignore the last one.

{{< sandbox version=java21 preview="true" mainclass="Challenge" >}}{{< sandboxsource "Challenge.java" >}}
import java.time.*;

public class Challenge {
   public static ... MAP = ...;
   public static void main(String[] args) {
      LocalDate today = LocalDate.now();
      Map<String, Object> todayMap = MAP."""
day \{today.getDayOfMonth()}
month \{today.getMonth()}
year \{today.getYear()}
""");
      System.out.println(todayMap);
   }
}

{{< /sandboxsource >}}
{{< /sandbox >}}

## Conclusion

String templates provide interpolation in a way that should be familiar to anyone who has used this feature in another programming language. Here are the highlights:

* Embedded expressions are enclosed in `\{...}`. This makes sense for Java since `\{` has previously not been a valid escape sequence in strings.
* Template expressions have the form `processor.argument`, where the argument is a string template, text block template, string, or text block.
* To write your own template processor, provide a method that turns the fragments and values of a `StringTemplate` instance into an object.

## References

* [JEP 430: String Templates (Preview), OpenJDK](https://openjdk.java.net/jeps/430)


