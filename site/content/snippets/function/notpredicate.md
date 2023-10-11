---
title: Not Predicate
---

With the static method
 [`Predicate.not()`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/function/Predicate.html#not(java.util.function.Predicate)) we can
 invert conditions. This is particular useful in combination with method
 references.

Since [Java 11](/jdk/11/)

{{< sandbox version="java21" mainclass="NotPredicate" >}}
{{< sandboxsource "NotPredicate.java" >}}

import static java.util.function.Predicate.not;

import java.util.List;

public class NotPredicate {

	public static void main(String... args) {

		List.of("", "venus", "   ", "mars", " ", "earth") //
				.stream() //
				.filter(not(String::isBlank)) //
				.forEach(System.out::println);

	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/function/NotPredicate.java)
