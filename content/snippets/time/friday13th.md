---
title: Friday 13th
---

To find all Friday 13th we iterate over a range of YearMonth and check
whether the LocalDate with day 13 is a Friday.

Since [Java 8](/jdk/8/)

{{< sandbox version="java25" mainclass="Friday13th" >}}
{{< sandboxsource "Friday13th.java" >}}

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.stream.Stream;

public class Friday13th {

	public static void main(String[] args) {

		YearMonth start = YearMonth.of(2000, 1);
		YearMonth end = YearMonth.of(2030, 12);

		Stream.iterate(start, m -> m.isBefore(end), m -> m.plusMonths(1)) //
				.map(m -> m.atDay(13)) //
				.filter(Friday13th::isFriday) //
				.map("%ta %<s"::formatted) //
				.forEach(System.out::println);
	}

	static boolean isFriday(LocalDate date) {
		return DayOfWeek.FRIDAY.equals(date.getDayOfWeek());
	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/time/Friday13th.java)
