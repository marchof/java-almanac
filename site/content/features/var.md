---
title: var Keyword (JEP 286)
jep: 286
jdkversion: 10
type: "sandbox"
---

The `var` keyword simplifies the declaration of local variables. By using this
keyword the variable type is inferred from the initialization expression. This
allows more concise variable declarations without usually redundant type
declarations.

{{< sandbox version="java11" mainclass="VarKeyword" >}}
{{< sandboxsource "VarKeyword.java" >}}
import java.util.Arrays;

public class VarKeyword {

    public static void main(String[] args) {
        var world = "World";
        var receiver = Arrays.asList(world, "Galaxy", "Universe");

        receiver.forEach(System.out::println);
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

## Limitations

The usage of the `var` keyword is limited to local variable declarations with
initializers. It can *not* be used for other declarations such as:

* Uninitialized local variables
* Local variables initialized with `null`
* Fields
* Method parameters or return types
* `catch` clauses

Be aware that any local variable declared with `var` adopts the specific type of
the initializer expression. If you want to reassign a different value later the
type might not match. In this example the local variable should better have the
type `java.util.List` but with the `var` statement the type `java.util.ArrayList` is
assumed:

{{< sandbox version="java11" mainclass="TooSpecificType" >}}
{{< sandboxsource "TooSpecificType.java" >}}
import java.util.ArrayList;
import java.util.Arrays;

public class TooSpecificType {

    public static void main(String[] args) {
        var list = new ArrayList();
        // will not compile
        list = Arrays.asList("one", "two", "three");
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

## New Use Cases for Local Variables

The `var` keyword infers the variable type from the initialization expression.
This opens access to types which can not be declared with explicit type
declarations.

### Anonymous Inner Classes

The first example is anonymous inner classes. Using the `var` keyword the
compiler uses the actual anonymous type and it is possible to access the API
of the anonymous class:

{{< sandbox version="java11" mainclass="AnonymousType" >}}
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

{{< sandbox version="java11" mainclass="IntersectionType" >}}
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

## References

* [JEP 286: Local-Variable Type Inference, OpenJDK](http://openjdk.java.net/jeps/286)
* [Local Variable Type Inference: Frequently Asked Questions, OpenJDK](https://openjdk.java.net/projects/amber/LVTIFAQ.html)
