---
title: Text Blocks (JEP 378)
copyright: Cay S. Horstmann 2020. All rights reserved.
jep: 378
jdkversion: 15
type: "sandbox"
---


Text blocks, added in their final form to Java 15, are a new form of string literals that is particularly suited for multiline text. Their usage is mostly straightforward, but you need to be aware how whitespace and escape sequences are processed.

## The String Literals Landscape

Sometimes, you need string literals that contain code in another programming language, such as HTML or SQL code, or perhaps plain text such as a license statement. With the classic Java string literals, that's not a lot of fun:

```
String myDoc = "<html>\n  <head>\n    <title>My Document</title>\n  </head>"
  + "  <body>\n"
  + "    <p>Hello, World!</p>\n"
  + "  </body>\n</html>";
```

Other programming languages have string literal types that allow newlines. For example, in JavaScript, you can use a template literal:

```
let myDoc = `<html>
  <head>
    <title>My Document</title>
  </head>
  <body>
    <p>Hello, World!</p>"
  </body>
</html>`
```

In JavaScript, this form of literals is called a *template* because you can include interpolated expressions:

```
let myTitle = `<title>: ${name}'s Document</title>`
```

That's nice too, and a number of other programming languages support interpolation.

For characters that you can't or don't want to include literally (non-printable characters, non-ASCII Unicode), you use escapes: 

```
String request = "GET / HTTP/1.0\r\nHost: horstmann.com\r\n";
```

Here each line ends in a carriage return-newline pair.

What if your string has backslashes? Then you need to escape them too. That gets tedious:

```
String path = "c:\\users\\nate";
```

*Raw* string literals turn off escape sequences, so that you can include backslashes without fear. Again in JavaScript:

```
let path = raw`c:\users\nate`
```

Here you see another phenomenon: flavors of string literals, typically indicated by a prefix such as `raw`. Some languages allow for arbitrary flavors. In JavaScript, the implementor of a flavor provides a function; in Scala, a macro.

That's the string literal design space: 

* Multiline
* Interpolation
* Raw vs. escapes
* Flavors

## Text Blocks

JEP 378, which is included with Java 15, provides *text blocks* for multiline strings. A text block starts with `"""` followed by optional white space and a line feed. It ends with `"""`: 

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        String greeting = """
Hello
World
""";
        System.out.println(greeting);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

This string contains two `\n`: one after `Hello` and one after
        `World`. The newline after the opening `"""` is not included in the string literal.

The resulting string is an ordinary `java.lang.String` object. There is no way to find out that it was created as a text block.

It is a syntax error if an opening `"""` is followed by anything other than whitespace:

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        String error = """This is a syntax error""";
        System.out.print(error);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

If you don't want a newline after the last line, you put the closing `"""` immediately behind the last character:

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
import java.util.Scanner;

public class Sandbox {
    public static void main(String[] args) {
        String prompt = """
Hello, my name is Hal.
Please enter your name: """;
        System.out.print(prompt);
        String simulatedInput = "Fred";
        System.out.println(simulatedInput);
        System.out.printf("Hello, %s, nice to meet you!%n", simulatedInput);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

What happened to the space after the colon? Hold that thought for a couple of sections.

## Escapes

You don't have to escape quotation marks, except if the string ends in a quotation mark:

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        String thought1 = """
I thought: "I don't have to escape quotation marks" and smiled.""";
        String thought2 = """
But then I realized: "Oh no--sometimes I still have to!\"""";
        System.out.println(thought1);
        System.out.println(thought2);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Or if you have three or more quotation marks in a row:

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        String myFaceInASCII = """
""\"""\"""
| o  o |
|  ==  |
\\------/""";
        System.out.print(myFaceInASCII);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

You must always escape backslashes. The other escape sequences (`\b`, `\f`, `\n`, `\r`, `\t`, `\'`, and, ugh, octal escapes) work exactly as in classic string literals. 

As an aside, the Java compiler handles Unicode escapes `\uxxxx` at a much earlier stage of processing. They are never observed as escapes in string literals because they are already replaced with their values.

Two new escapes are added: `\s` is an “essential” space that won't be removed—see the next section. A backslash that is the last character in its line continues the line—that is, no newline is added. The following example produces a string without any newlines.

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        String query = """
SELECT Books, Books.Publisher_Id, Books.Price, Publishers.Name, Publishers.URL \
FROM Books, Publishers \
WHERE Books.Publisher_Id = Publishers.Publisher_Id""";
        System.out.println(query);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Note that the `\` truly must be the last character—no trailing whitespace is allowed. This escape only works in text blocks, not in classic strings literals.

## Whitespace

Perhaps surprisingly, the biggest complexity with text blocks comes from the handling of white space. There are three distinct mechanisms at play: normalizing line endings, stripping trailing whitespace, and stripping common indent. We will examine all three in the following sections.

Note that whitespace is handled *before* escapes (including line continuations) are processed. 

### Normalizing Line Endings

Line endings in text blocks (other than the one after the opening `"""` and after continuation escapes) are turned into `\n`, even if the source file uses DOS-style line endings (`\r\n`). That is sensible—you wouldn't want code like the following depend on whether the source file was edited on Windows or Linux:

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        String greeting = """
Hello
World""";
        System.out.println(greeting.length());
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

### Removing Trailing White Space

Trailing whitespace is removed from each line. That way, there are no surprises if an invisible space is added accidentally. That is also sensible, and consistent with ignoring whitespace after the opening `"""`.

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        String greeting = """
Hello   
World   """;
        System.out.println("Trailing whitespace removed: ");
        System.out.println("|" + greeting + "|");
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

To preserve trailing whitespace, use the `\s` escape. In this prompt, the trailing spaces are not removed:

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
import java.util.Scanner;

public class Sandbox {
    public static void main(String[] args) {
        String prompt = """
Hello, my name is Hal.
Please enter your name:\s""";
        System.out.print(prompt);
        String simulatedInput = "Fred";
        System.out.println(simulatedInput);
        System.out.printf("Hello, %s, nice to meet you!%n", simulatedInput);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Any spaces before the `\s` are preserved as well:

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
import java.util.stream.Stream;

public class Sandbox {
    public static void main(String[] args) {
        String snowmanWithColoredBackground = """
<pre style="background: skyblue;">
        \s
   (")  \s
  ( : ) \s
 (     )\s
---------
</pre>""";
        Stream.of(snowmanWithColoredBackground.split("\n")).forEach(line -> System.out.println("|" + line + "|"));
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

### Indent Stripping

Consider a typical text block:

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        String myNameInADiv = """
<div>
  Cay 
</div>
""";
        System.out.print(myNameInADiv);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Some people prefer to have the text block indented with the rest of the code, so that it doesn't look “out of place”:

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        String myNameInADiv = """
            <div>
              Cay 
            </div>
            """;
        System.out.print(myNameInADiv);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Then the *common whitespace prefix* is removed, so that the text block consists of the three lines

```
<div>
  Cay
</div>
```

What about tabs, I hear you cry.

Here are the rules:

1\. A whitespace is any Unicode code point other than `\n` or `\r` for which `Character.isWhitespace` is true. That includes, of course, spaces and tabs as well as 19 other code points. Run this code snippet to find URLs describing them all.
          {{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        for (int i = 0; i < 17 * 65536; i++)
            if (Character.isWhitespace(i) && i != '\r' && i != '\n')
                System.out.printf("https://codepoints.net/U+%04X%n", i);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

I don't know how many programmers use [form feeds](https://codepoints.net/U+000C) or [hair spaces](https://codepoints.net/U+200A) for indenting their code, but Java has them covered.



2\. Entirely blank lines are not considered in this process, except for the line ending with the closing `"""`.

3\. The indent is the length of the smallest whitespace prefix. In the example above, the indent is 12, as you can see from the following, where white spaces are helpfully replaced with `.`:

```
        String myNameInADiv = """
............<div>
..............Cay 
............</div>
............""";
```



4\. It doesn't matter what kind of whitespace character is used. Suppose we use a mixture of spaces and tabs (helpfully indicated as ⇥):

```
        String myNameInADiv = """
............<div>
   ⇥   ⇥   ⇥..Cay
.. 
   ⇥........</div>
   ⇥   ⇥   ⇥""";
```

In this case, the shortest whitespace prefix has length 3 (in the fifth line), and the first three whitespace characters are stripped from each non-blank line, whether or not they are tabs. The result is a string with four lines:

```
.........<div>
..Cay

......</div>
```

The third line is entirely blank because trailing whitespace is removed. Note that it doesn't matter that it only had two spaces. Entirely blank lines are ignored when measuring the indent. Except for the last one. Its whitespace is considered, before it is discarded, being a blank line preceding the closing `"""`. 

I am not making this up. Here is the program. It is difficult to see the difference between the tabs and spaces, but if you copy out the contents, save to a file, and view a hex dump, you can confirm them.

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
import java.util.stream.Stream;

public class Sandbox {
    public static void main(String[] args) {
        String myNameInADiv = """
            <div>
			  Cay
  
	        </div>
			""";
        Stream.of(myNameInADiv.split("\n")).forEach(line -> System.out.println("|" + line + "|"));
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

If you and all your collaborators always use spaces or always use tabs, the algorithm should work without surprises. 

Otherwise, you may want to say “no thanks” and live with the “out of place” look. It actually has advantages. The “foreign” code in the text block stands out as different from the surrounding Java code. And if the text block contains long lines, as it often will, you may not want them pushed to the right.

## New String Methods

Two methods were added to the `String` class that process whitespace and escapes in exactly the same way as the text block parser. It seems pretty unlikely that you need to do this by hand. 

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
import java.util.stream.Stream;

public class Sandbox {
    public static void main(String[] args) {
        String myStringToBeProcessed = "            <div>\n\t\t\t  Cay\\s\n  \n\t        </div>\n\t\t\t";
        String processed1 = myStringToBeProcessed.stripIndent();
        String processed2 = processed1.translateEscapes();
        Stream.of(processed1.split("\n")).forEach(line -> System.out.println("|" + line.replace(" ", ".") + "|"));
        Stream.of(processed2.split("\n")).forEach(line -> System.out.println("|" + line.replace(" ", ".") + "|"));
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Note that the `stripIndent` method carries out all whitespace processing tasks. It normalizes line endings and strips trailing whitespace as well as common indents.

A third method, `formatted`, is intended to provide an alternative to interpolation. The call `s.formatted(args)` is exactly the same as `String.format(s, args)`, saving you five characters. Have a look at this example. It's not quite as nice as actual interpolation, but easier on the eyes than concatenating strings:

{{< sandbox version=java15 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        String item = """
<item>
  <description>%s</description>
  <price>$%.2f</price>
</item>""".formatted("Blackwell Toaster", 29.9);
        System.out.println(item);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

## Summary

1. Text blocks are convenient for multiline string literals
2. Text blocks are not raw: You need to escape backslashes and triple quotes
3. If you like indenting your text blocks and want the indents stripped, be sure not mix tabs and spaces
4. Use `\s` to prevent stripping of trailing spaces
5. There is no interpolation, but the new `formatted` method saves five characters over the static `String.format`


