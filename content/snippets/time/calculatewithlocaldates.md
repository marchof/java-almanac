---
title: Calculate With Local Dates
---

Local dates can be used for various calendar calculations of based on
granularity days.

Since [Java 8](/jdk/8/)

{{< sandbox version="java25" mainclass="CalculateWithLocalDates" >}}
{{< sandboxsource "CalculateWithLocalDates.java" >}}

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class CalculateWithLocalDates {

	void main() {
		LocalDate arrival = LocalDate.of(2021, 12, 28);
		LocalDate departure = LocalDate.of(2022, 1, 2);

		IO.println("Nights: " + arrival.until(departure, ChronoUnit.DAYS));
		arrival.datesUntil(departure).map("Dinner: %ta %<s"::formatted).forEach(IO::println);

		departure = departure.plus(3, ChronoUnit.DAYS);
		IO.println("Stay 3 more days: %ta %<s".formatted(departure));

		departure = departure.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
		IO.println("Stay to next sunday: %ta %<s".formatted(departure));
	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/time/CalculateWithLocalDates.java)
