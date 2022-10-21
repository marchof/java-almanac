---
title: Comparators
---

With the `java.util.Comparator` interface you can define an ordering for
 object instances of a given type. Unlike the `java.lang.Comparable` interface
 which is implemented by the sorted elements itself, `Comparator`s are
 *external* and depending on the use case different orderings can be defined.

Since [Java 8](/jdk/8/)

{{< sandbox version="java17" mainclass="Comparators" >}}
{{< sandboxsource "Comparators.java" >}}

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Comparators {

	// There are pre-defined comparators in the Java API:
	static Comparator<String> c1 = String.CASE_INSENSITIVE_ORDER;

	// The natural ordering can be directly obtained with method references
	static Comparator<String> c2 = String::compareTo;

	// There is a factory method to compare on attributes
	static Comparator<String> c3 = Comparator.comparingInt(String::length);

	// Comparators can be reversed and chained
	static Comparator<String> c4 = Comparator //
			.comparingInt(String::length) //
			.reversed() //
			.thenComparing(String::compareToIgnoreCase);

	// Comparators can be made null safe:
	static Comparator<String> c5 = Comparator.nullsFirst(c1);

	public static void main(String[] args) {
		var input = List.of("apple", "Cherry", "Fig", "Pear", "Mango");

		// Sort the list with the different criteria:
		System.out.println(input.stream().sorted(c1).toList());
		System.out.println(input.stream().sorted(c2).toList());
		System.out.println(input.stream().sorted(c3).toList());
		System.out.println(input.stream().sorted(c4).toList());

		// Sort a list with nulls:
		var inputWithNulls = Arrays.asList("c", null, null, "a", "B");
		System.out.println(inputWithNulls.stream().sorted(c5).toList());

		// Find the minimal element in respect to a Comparator:
		System.out.println(Collections.min(input, c4));
	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/util/Comparators.java)
