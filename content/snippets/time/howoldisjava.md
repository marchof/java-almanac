---
title: Time Period
---

A date-based amount of time can be represented with the
[`Period`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/time/Period.html) type. With this data type a time span is expressed
as a number of years, months and days.

Since [Java 8](/jdk/8/)

{{< sandbox version="java25" mainclass="HowOldIsJava" >}}
{{< sandboxsource "HowOldIsJava.java" >}}

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class HowOldIsJava {

	static final LocalDate BIRTHDAY_OF_JAVA = LocalDate.of(1995, 5, 23);

	static final ZoneId TIMEZONE_OF_BIRTH = ZoneId.of("America/Los_Angeles");

	void main() {

		var today = LocalDate.now(TIMEZONE_OF_BIRTH);
		var age = Period.between(BIRTHDAY_OF_JAVA, today);
		System.out.println("As of today Java is %s old".formatted(fmt(age)));

		var nextAnivesary = BIRTHDAY_OF_JAVA.plus(Period.ofYears(age.getYears() + 1));
		var tillAnivesary = Period.between(today, nextAnivesary);
		System.out.println("Java's next birthday is in %s".formatted(fmt(tillAnivesary)));

	}

	String fmt(Period p) {
		// Unfortunately there is no built-in formatter for Period objects
		return "%d years, %d months and %d days".formatted(p.getYears(), p.getMonths(), p.getDays());
	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/time/HowOldIsJava.java)
