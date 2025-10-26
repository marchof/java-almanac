---
title: Calendar Systems
---

There are different calendar systems in the world. Let's write the Java 17
release date in the systems known to Java.

Since [Java 8](/jdk/8/)

{{< sandbox version="java25" mainclass="CalendarSystems" >}}
{{< sandboxsource "CalendarSystems.java" >}}

import java.time.LocalDate;
import java.time.chrono.Chronology;

public class CalendarSystems {

	void main() {

		LocalDate java25release = LocalDate.of(2025, 9, 16);

		Chronology.getAvailableChronologies().stream() //
				.sorted() //
				.map(c -> c.date(java25release)) //
				.forEach(IO::println);

	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/time/CalendarSystems.java)
