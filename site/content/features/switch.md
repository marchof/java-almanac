---
title: Switch Expressions (JEP 361)
copyright: Cay S. Horstmann 2021. All rights reserved.
jep: 361
jdkversion: 14
type: "sandbox"
---


In preparation for a more general pattern matching construct, JEP 361 provides an expression that selects one of multiple values, given an operand that is an integer, string, or `enum` constant, using the familiar `switch` syntax. The JEP also provides additional forms of `switch` expressions and statements, in order to make the feature more regular. The final release occurred in JDK 14.

## The Classic Switch Statement

The C language introduced a `switch` statement which is quite different from the more orderly control flow constructs (`if`, `while`, and so on). Execution jumps to a labeled branch, and falls through subsequent branches, unless explicitly terminated with a `break`. 

Here is the classic example from [Kernighan & Ritchie](https://en.wikipedia.org/wiki/The_C_Programming_Language):

```
switch (c) {
    case '0': case '1': case '2': case '3': case '4':
    case '5': case '6': case '7': case '8': case '9':
        ndigit[c-'0']++;
        break;
    case ' ':
    case '\n':
    case '\t':
        nwhite++;
        break;
    default:
        nother++;
        break;
}
```

The statement was created for one reason: jump tables. If there are many cases, it is worthwhile to set up a table of the case labels and branch starts. If the table is sparse, locate the entry by dead reckoning. Otherwise, sort the table and use binary search. Then simply jump to the matching branch. In ancient times, this technique was called "computed goto". The `switch` statement makes it available in high-level languages.

## Switch Expressions

The familiar `? :` expression lets you choose one value or another:

```
int larger = x > y ? x : y;
```
It is the analog of the `if/else` statement for expressions.

What if you have more than two values to choose from? Then we need an expression analog of the `switch` statement. Instead of designing a multi-way conditional operator or a "match" expression, JEP 361 simply reuses the `switch` syntax:

{{< sandbox version=java14 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        int seasonCode = (int) (5 * Math.random());
        System.out.println(seasonCode);
        String seasonName = switch (seasonCode) {
            case 0 -> "Spring";
            case 1 -> "Summer";
            case 2 -> "Fall";
            case 3 -> "Winter";
            default -> "???";
        };
        System.out.println(seasonName);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

The `->` indicate that each branch yields a value, somewhat like a lambda expression.

The `case` labels can also be strings or `enum` constants.

You can provide multiple labels for each `case`, separated by commas:

{{< sandbox version=java14 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        String seasonName = (new String[] { "Spring", "Summer", "Fall", "winter", "???" })[(int) (5 * Math.random())];
        System.out.println(seasonName);
        int numLetters = switch (seasonName) {
            case "Spring", "Summer", "Winter" -> 6;
            case "Fall" -> 4;
            default -> -1;
        };
        System.out.println(numLetters);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

The match in a `switch` expression must be exhaustive. With integers and strings, you must include a `default` case. With enumerated types, you must either have a `default` case or cases for all constant values.

## Statements in Branches

Suppose you need to do something in one of the branches, such as logging a value. In a `switch` statement, this isn't a problem. Each branch can contain multiple statements. But Java does not have general comma expressions (as in C/C++) or block expressions (as in Scala or Lisp). JEP 361 provides a restricted form inside `switch` expressions.

The `->` token in a `switch` expression can be followed by a statement block, enclosed in `{ }`. The block must be exited through a `yield` statement that yields the block's value. For example:

{{< sandbox version=java14 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        int seasonCode = (int) (5 * Math.random());
        System.out.println(seasonCode);
        String seasonName = switch (seasonCode) {
            case 0 -> {
                System.out.println("spring time!");
                yield "Spring";
            }
            case 1 -> "Summer";
            case 2 -> "Fall";
            case 3 -> "Winter";
            default -> "???";
        };
        System.out.println(seasonName);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Just like `return` exits a method with a value, `yield` exits the block with a value. 

Prior versions ([JEP 325](https://openjdk.java.net/jeps/325) and [354](https://openjdk.java.net/jeps/354)) used the `break` keyword for the same purpose, but this was deemed to be confusing when previewed in JDK 12 and 13.

## The Four Forms of Switch

The classic `switch` statement, which has been a part of Java since version 1.0, differs from the `switch` expression in two ways:

* The classic switch is a statement, not an expression
* In the classic switch, execution "falls through" to the next branch unless 

The "fall through" behavior, while sometimes necessary, is perceived as error-prone. For that reason, there is a `-Xlint:fallthrough` compiler option generates a warning when it occurs. 

JEP 361 provides a form of the `switch` statement without fall through:

{{< sandbox version=java14 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        int seasonCode = (int) (5 * Math.random());
        System.out.println(seasonCode);
        String seasonName = switch (seasonCode) {
            case 0 -> {
                System.out.println("spring time!");
                yield "Spring";
            }
            case 1 -> "Summer";
            case 2 -> "Fall";
            case 3 -> "Winter";
            default -> "???";
        };
        System.out.println(seasonName);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

Here, `->` does not denote the yielding of a value, but merely the absence of fall through.

In the interest of uniformity and convenience, you can now separate `case` values by commas in the classic `switch` statement as well:

```
case "Spring", "Summer", "Winter":
```

instead of

```
case "Spring":
case "Summer":
case "Winter":
```

Finally, for symmetry, a "fall through" version of switch expressions ia provided:

{{< sandbox version=java14 preview="true" mainclass="Sandbox" >}}
{{< sandboxsource "Sandbox.java" >}}
public class Sandbox {
    public static void main(String[] args) {
        int seasonCode = (int) (5 * Math.random());
        System.out.println(seasonCode);
        String seasonName = switch (seasonCode) {
            case 0: 
                System.out.println("spring time!");
                yield "Spring";
            case 1:
                yield "Summer";
            case 2:
                yield "Fall";
            case 3:
                yield "Winter";
            default:
                yield "???";
        };
        System.out.println(seasonName);
    }
}
{{< /sandboxsource >}}
{{< /sandbox >}}

All forms of `switch` throw a `NullPointerException` if the provided operand is `null`. It is not possible to use `null` as a `case` label, and `default` does not match `null`.

The following table shows the four forms along the two axes (expression/statement and disjoint branches/fall through).

{{< rawhtml >}}
<table>
<tr>
<th>  </th> <th> Expression </th> <th> Statement </th>
</tr>
<tr></tr>
<tr>
<th> No fall through </th>
<td>
<pre>
int numLetters = switch (seasonName) {
   case "Summer", "Winter" -> 6;
   case "Spring" -> {
      System.out.println("spring time!");
      yield 6;
   }
   case "Fall" -> 4;
   default -> -1;
};
</pre>
</td>
<td>
<pre>
switch (seasonName) {
   case "Summer", "Winter" ->
      numLetters = 6;
   case "Spring" -> {
      System.out.println("spring time!");
      numLetters = 4;
   }
   case "Fall" -> 4;
      numLetters = 4;
   default ->
      numLetters = -1;
}
</pre>
</td>
</tr>
<tr></tr>
<tr>
<th> Fall through </th>
<td>
<pre>
int numLetters = switch (seasonName) {
   case "Spring":
      System.out.println("spring time!");
   case "Summer", "Winter":
      yield 6;
   case "Fall":
      yield 4;
   default:
      yield -1;
};
</pre>
</td>
<td>
<pre>
switch (seasonName) {
   case "Spring":
      System.out.println("spring time!");
   case "Summer", "Winter":
      numLetters = 6;
      break;
   case "Fall":
      numLetters = 4;
      break;
   default:
      numLetters = -1;
}
</pre>
</td>
</tr>
</table>
{{< /rawhtml >}}

## Recommendations

Prefer `switch` expressions over `switch` statements. If  a `switch` or `if/else if/else` statement sets a variable in each branch, use a single assignment with a `switch` expression instead. 

```
int numLetters = switch (seasonName) {
    case "Spring", "Summer", "Winter" -> 6
    case "Fall" -> 4
    default -> -1
};
```

is better than

```
switch (seasonName) {
    case "Spring", "Summer", "Winter" -> 
        numLetters = 6;
    case "Fall" -> 
        numLetters = 4;
    default -> 
        numLetters = -1;
}
```

Minimize `break` and `yield`. Use them only if you actually need the "fall through" or "statement before expression" behavior. This is very uncommon. 

The `yield` statement may in time join the labeled `break/continue` statements: unloved by all except the authors of certification exam questions.

## Summary

1. There are four forms of `switch`: statements and expressions, with and without fall through
2. A `switch` expression selects one or more values, matching an operand. 
3. The cases in a `switch` expression must be disjoint and exhaustive
4. A `switch` expression or statemnt with `->` tokens does not have fall through behavior
5. A `case` label can be followed by multiple constants, separated by commas
6. In a `switch` expression, the `yield` statement exits and yields a value
7. In a `switch` expression without fall through, the `->` token can be followed by a block statement that yields a value

## References
* [JEP 325: Switch Expressions (Preview), OpenJDK](https://openjdk.java.net/jeps/325)
* [JEP 354: Switch Expressions (Second Preview), OpenJDK](https://openjdk.java.net/jeps/354)
* [JEP 361: Switch Expressions, OpenJDK](https://openjdk.java.net/jeps/361)
