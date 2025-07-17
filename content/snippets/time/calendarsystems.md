---
title: Calendar Systems
---

There are different calendar systems in the world. Let's write the Java 17
 release date in the systems known to Java.

Since [Java 8](/jdk/8/)

{{< sandbox version="java21" mainclass="CalendarSystems" >}}
{{< sandboxsource "CalendarSystems.java" >}}

import java.time.LocalDate;
import java.time.chrono.Chronology;

public class CalendarSystems {

	public static void main(String[] args) {

		LocalDate java17release = LocalDate.of(2021, 9, 14);

		Chronology.getAvailableChronologies().stream() //
				.sorted() //
				.map(c -> c.date(java17release)) //
				.forEach(System.out::println);

	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/time/CalendarSystems.java)
