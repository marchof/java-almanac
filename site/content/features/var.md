---
title: var Keyword (JEP 286)
jep: 286
jdkversion: 10
type: "sandbox"
author: "Marc R. Hoffmann"
---

The `var` keyword simplifies the declaration of local variables. By using this
keyword the variable type is inferred from the initialization expression. This
allows more concise variable declarations without usually redundant type
declarations.

{{< sandbox version="java17" mainclass="VarKeyword" >}}
{{< sandboxsource "VarKeyword.java" >}}
import java.util.Arrays;

public class VarKeyword {

    public static void main(String[] args) {
        var world = "World";
        var subjects = Arrays.asList(world, "Galaxy", "Universe");
        
        for (var s : subjects) {
            System.out.println(s);
        }
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

The `var` can be used in the following places:

* Local variable declaration with initialization
* Variables declared in `for` loops (classic or enhanced)
* Variables in `try`-with-resources blocks

A local variable declared with `var` might also be explicitly declared final:

    final var word = "World";

But since local variables which are not reassigned are nowadays *implicitly final*
the unnecessary `final` declaration contradicts the original objective for
conciseness. 


## Lambda Parameters

With Java 11 and [JEP 323](https://openjdk.java.net/jeps/323) for consistency it
became possible to use `var` also for lambda parameters, which are implicitly typed:

    Predicate<String> toolong = (var s) -> s.length() > 42;
 
This provides a concise syntax to add annotations to lambda parameters:
  
    Predicate<String> toolong = (@NonNull var s) -> s.length() > 42;
 

## Limitations

The usage of the `var` keyword is limited to local variable declarations with
initializers. It can *not* be used for other declarations such as:

* Uninitialized local variables
* Local variables initialized with `null`
* Local variables where the resulting type cannot be inferred (e.g. lambdas and method references)
* Fields
* Method parameters or return types
* `catch` clauses

Also note that for parameterized types using the *diamond operator* will fall
back to the base type of the respective type parameters. For example

    var list = new ArrayList<>();
    
will result in the type `ArrayList<Object>` for the `list` variable.

Be aware that any local variable declared with `var` adopts the specific type of
the initializer expression. If you want to reassign a different value later the
type might not match. In this example the local variable should better have the
type `java.util.List` but with the `var` statement the type `java.util.ArrayList` is
assumed:

{{< sandbox version="java17" mainclass="TooSpecificType" >}}
{{< sandboxsource "TooSpecificType.java" >}}
import java.util.ArrayList;
import java.util.List;

public class TooSpecificType {

    public static void main(String[] args) {
        var list = new ArrayList<String>();
        // will not compile
        list = List.of("one", "two", "three");
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

## New Use Cases for Local Variables

The `var` keyword infers the variable type from the initialization expression.
This opens access to types which can not be declared with explicit type
declarations.

### Anonymous Classes

The first example is anonymous inner classes. Using the `var` keyword the
compiler uses the actual anonymous type and it is possible to access the API
of the anonymous class:

{{< sandbox version="java17" mainclass="AnonymousType" >}}
{{< sandboxsource "AnonymousType.java" >}}
public class AnonymousType {

    public static void main(String[] args) {
        var foo = new Object() {
            String field = "Hello";
        };

        System.out.println(foo.field);
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

### Intersection Types

With the `var` statement it is possible to declare a local variable that has the
type of multiple mix-in interfaces. This is not possible with local variables
using explicit type declarations.

{{< sandbox version="java17" mainclass="IntersectionType" >}}
{{< sandboxsource "IntersectionType.java" >}}
public class IntersectionType {

    public static void main(String[] args) {
        var guest = (Welcome & Goodbye) () -> "World";

        System.out.println(guest.welcome());
        System.out.println(guest.goodbye());
    }

}
{{< /sandboxsource >}}
{{< sandboxsource "Welcome.java" >}}
import java.util.function.Supplier;

public interface Welcome extends Supplier<String> {

    default String welcome() {
        return String.format("Hello %s", get());
    }

}
{{< /sandboxsource >}}
{{< sandboxsource "Goodbye.java" >}}
import java.util.function.Supplier;

public interface Goodbye extends Supplier<String> {

    default String goodbye() {
        return String.format("Goodbye %s", get());
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

Intersection types may also be implicitly determined by the compiler, for
example when the compiler needs to find the common type of different objects
supplied to a parameterized function with multiple parameters. This can result
in quite unexpected inferred types:

{{< sandbox version="java17" mainclass="FunnyIntersectionType" >}}
{{< sandboxsource "FunnyIntersectionType.java" >}}
import java.util.List;

public class FunnyIntersectionType {

    public static void main(String[] args) {
        var list = List.of(3.5, "42");

        // will not compile, note compiler error:
        String first = list.get(0);
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

## References

* [JEP 286: Local-Variable Type Inference, OpenJDK](http://openjdk.java.net/jeps/286)
* [JEP 323: Local-Variable Syntax for Lambda Parameters, OpenJDK](http://openjdk.java.net/jeps/323)
* [Local Variable Type Inference: Frequently Asked Questions, OpenJDK](https://openjdk.java.net/projects/amber/LVTIFAQ.html)
