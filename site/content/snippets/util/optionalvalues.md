---
title: Optional Values
type: sandbox
---

Instead of passing using `null` references optional values can be wrapped in
 a [`Optional`](https://download.java.net/java/early_access/jdk17/docs/api/java.base/java/util/Optional.html) instances. This type is typically used in APIs
 to express the possibility that a method may not return a value, for example
 [`Stream.max()`](https://download.java.net/java/early_access/jdk17/docs/api/java.base/java/util/stream/Stream.html#max(java.util.Comparator)) may not have a
 return value on an empty stream.
 
 The `Optional` type is designed to be used in a functional way: It owns the
 control flows, we simply declare what to do if a value is present or missing.
 If you find yourself writing code like `if (optional.isPresent()) { ... }` or
 you are calling [`Optional.get()`](https://download.java.net/java/early_access/jdk17/docs/api/java.base/java/util/Optional.html#get()) you're most likely on the
 wrong track.

Since [Java 8](/jdk/8)

{{< sandbox version="java17" mainclass="OptionalValues" >}}
{{< sandboxsource "OptionalValues.java" >}}

import java.util.Optional;

public class OptionalValues {

	static void work(Optional<String> task) {

		// Obtain a fixed default
		var myTask1 = task.orElse("Clean the kitchen.");
		System.out.println(myTask1);

		// Calculate the default value if required
		var myTask2 = task.orElseGet(OptionalValues::createTask);
		System.out.println(myTask2);

		// Perform an operation if a value exist:
		task.ifPresent(System.out::println);

		// Perform an operation if a value exist or do something else:
		task.ifPresentOrElse(System.out::println, OptionalValues::idle);

		// The optional value can be mapped (if it exists):
		task.map(String::toUpperCase).ifPresent(System.out::println);

		// The optional value can be filtered (if it exists):
		task.filter(t -> t.contains("coding")).ifPresent(System.out::println);

	}

	static String createTask() {
		return "Find random Task.";
	}

	static void idle() {
		System.out.println("Just lazy today.");
	}

	public static void main(String[] args) {
		System.out.println("Our task today:");
		work(Optional.of("Do some coding."));

		System.out.println("No Task today:");
		work(Optional.empty());
	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/util/OptionalValues.java)
