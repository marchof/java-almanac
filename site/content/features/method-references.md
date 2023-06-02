---
title: Method References (JSR 335)
JSR: 335
jdkversion: 8
author: "Marc R. Hoffmann"
---

In the Java language a *method reference* allows converting a specific method to an instance of a functional interface. When the reference is evaluated the method is not yet called. The created functional interface instance can be used to invoke the method at a later point in time. Example:

{{< sandbox version="java11" mainclass="ReferenceExample" >}}
{{< sandboxsource "ReferenceExample.java" >}}
import java.util.function.Consumer;

class ReferenceExample {

    public static void main(String[] args) {
        // Just get the reference
        Consumer<String> printer = System.out::println;
    
        // Now call the method
        printer.accept("Hello...");
        printer.accept("...Reference!");
    }
} 
{{< /sandboxsource >}}
{{< /sandbox >}}

The formal parameters of the method defined by the functional interface has to match the signature of the method reference.

With method references functional interfaces can be created without using the lambda syntax. As this results in a more concise notation it is considered a good practice to prefer method references over lambdas. For example a boolean property can be directly used as a predicate:

    var activeUsers = allUsers.stream().filter(User::isActive).collect(toList);

## Syntax

Java uses two colons `::` to denote a method reference: 

    <Type or Instance>::<method identifier>

Other languages like JavaScript do not have a specific syntax for method references. Methods are simply slots of objects like fields and can be referenced with the regular dot without parenthesis to trigger a call:

    // method object in JavaScript
    runmethod = worker.run
    runmethod()

As the Java language allows fields and methods with the same name this would result in ambiguities. Therefore a new distinct syntax using two colons `::` was required.

## References to static methods

The simplest case for method references are static methods. In this case the signature of the static method must be the same as the signature of the abstract method defined by the functional interface:

{{< sandbox version="java11" mainclass="StaticMethodReference" >}}
{{< sandboxsource "StaticMethodReference.java" >}}
import java.util.function.ToIntFunction;

public class StaticMethodReference {

    public static void main(String[] args) {
    
        // Reference to a static method
        ToIntFunction<String> parser = Integer::parseInt;
        
        System.out.println(parser.applyAsInt("42"));
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}


## References to unbound instance methods

If you create a reference to an instance method the situation becomes more tricky. In the next example we make a direct reference to the method of a certain type. So the method is *unbound* which means that at the point in time when we call the method an instance to call the method on must be provided.

When we for example want to call `String.length()` we need to provide a `String` instance. The compatible functional interface needs to define the method references' type as the first parameter:

{{< sandbox version="java11" mainclass="InstanceMethodReference" >}}
{{< sandboxsource "InstanceMethodReference.java" >}}
import java.util.function.ToIntFunction;

public class InstanceMethodReference {

    public static void main(String[] args) {
    
        // Reference to a unbound instance method
        ToIntFunction<String> len = String::length;
        
        System.out.println(len.applyAsInt("Hello Reference!"));
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

This still works if the instance method itself has parameters. In this case the first parameter of the method in the functional interface represents the instance we want to invoke the referenced method on, the remaining parameters will be mapped to the method parameters.

{{< sandbox version="java11" mainclass="InstanceWithParameterMethodReference" >}}
{{< sandboxsource "InstanceWithParameterMethodReference.java" >}}
import java.util.function.BiFunction;

public class InstanceWithParameterMethodReference {

    public static void main(String[] args) {
    
        // Reference to a unbound instance method with a parameter
        BiFunction<String, String, String> concat = String::concat;
        
        System.out.println(concat.apply("Hello ", "Reference!"));
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}


## References to bound instance methods

The method reference syntax `::` cannot only be used with types for instance methods it can also be used on instances. In this case the method is bound to the given instance at the point in time when the functional interface is created.

{{< sandbox version="java11" mainclass="BoundInstanceMethodReference" >}}
{{< sandboxsource "BoundInstanceMethodReference.java" >}}
import java.util.function.Consumer;

public class BoundInstanceMethodReference {

    public static void main(String[] args) {
    
        // Reference to a instance method
        Consumer<Object> printer = System.out::println;
        
        printer.accept("Hello Reference!");
    }

}
{{< /sandboxsource >}}
{{< /sandbox >}}

Note that the instance is bound at the point in time when the method reference is created. In this example the `printer` will still print to the original output even if you change the default output with `System.setOut()`. This is a
subtle difference to the following similar lambda which evaluates the value of `System.out` at every invocation:

    Consumer<String> printer = s -> System.out.println(s);

In the context of a class instance `this` as well as `super` can be used to to reference methods of the current instance: 

{{< sandbox version="java11" mainclass="ThisAndSuperReferences" >}}
{{< sandboxsource "ThisAndSuperReferences.java" >}}
import java.util.function.Supplier;

public class ThisAndSuperReferences {
   
    Supplier<String> thisToString  = this::toString;
    Supplier<String> superToString = super::toString;
    
    @Override
    public String toString() {
        return "This is super!";
    }
    
    public static void main(String[] args) {
        var r = new ThisAndSuperReferences();
        
        System.out.println(r.thisToString.get());
        System.out.println(r.superToString.get());
    }
       
}
{{< /sandboxsource >}}
{{< /sandbox >}}


## References to constructors

Constructors can be referenced by using `new` as an identifier. The `new` method reference works on

* top level classes,
* inner classes,
* local classes and
* array types.

In case of non-static inner classes the enclosing instance is evaluated when the method reference is created. This example demonstrates where `new` references are applicable:

{{< sandbox version="java11" mainclass="ConstructorReferences" >}}
{{< sandboxsource "ConstructorReferences.java" >}}
import java.util.List;
import java.util.ArrayList;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class ConstructorReferences {

    class InnerClass { }

    Supplier<List<String>> top = ArrayList::new;  
    Supplier<InnerClass> inner = InnerClass::new;
    IntFunction<int[]> array = int[]::new;
    Supplier<Object> local;
    
    ConstructorReferences() {
        class LocalClass { }
        local = LocalClass::new;
    }

    public static void main(String[] args) {
        var r = new ConstructorReferences();
        
        System.out.println(r.top.get());
        System.out.println(r.inner.get());
        System.out.println(r.array.apply(16));
        System.out.println(r.local.get());
    }  
       
}
{{< /sandboxsource >}}
{{< /sandbox >}}


## Parameterized types and methods

Method references can come with type parameters. Both for the referenced type and method type parameters can be provided if the underlying APIs declare them:

    // parameterized type
    Supplier<List<String>> listFactory = ArrayList<String>::new;

    // parameterized method
    Supplier<Comparator<Integer>> cmpFactory = Comparator::<Integer>naturalOrder;

## Things that do not work

While at most places references can be used instead of direct method or constructor invocations there are situations where method references will not result in valid Java source code.

For example there is no method reference syntax to create an inner class for a given enclosing outer class instance like with this direct invocation:

    Inner i = outer.new Inner();

If this is required the only alternative is to fall back to lambda syntax:

    Supplier<Inner> = () -> outer.new Inner();

Using a type parameter for the reference type itself is not possible:

    // does not compile
    Supplier<T> creator = T::new;
    
Literals other than string are no valid types for method references. There is no *outoboxing* for primitive literals:

    // does not compile
    ToIntFunction<Integer> cmp42 = 42::compareTo;

The compiler needs to know the functional interface the method reference must be converted to. Therefore method references cannot be used to initialize local variables declared with `var`:

    // does not compile
    var printer = System.out::println;;


## References

* [JSR 335: Lambda Expressions for the Java Programming Language, Java Community Process](https://jcp.org/en/jsr/detail?id=335)
* [JLS, Chapter 15.13, Method Reference Expressions, James Gosling et al.](https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.13)

