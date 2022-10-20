---
title: Daylight Saving Time
type: sandbox
---

The Java time API uses the [tz
 database](https://en.wikipedia.org/wiki/Tz_database) to handle DST rules for
 each time zone. While the typical use case is to convert instants to local
 times the API also allows to query the database and e.g. list all DST
 transitions for a specific time zone.

Since [Java 8](/jdk/8/)

{{< sandbox version="java17" mainclass="DaylightSavingTime" >}}
{{< sandboxsource "DaylightSavingTime.java" >}}

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;

public class DaylightSavingTime {

	public static void main(String... args) {
		ZoneId zone = ZoneId.of("Europe/Zurich");
		Instant from = LocalDate.of(2020, 1, 1).atStartOfDay().atZone(zone).toInstant();
		Instant to = LocalDate.of(2030, 1, 1).atStartOfDay().atZone(zone).toInstant();

		System.out.printf("DST transitions in %s between %s and %s:\n", zone, from, to);

		ZoneRules rules = zone.getRules();
		ZoneOffsetTransition t = rules.nextTransition(from);
		while (t.getInstant().isBefore(to)) {
			System.out.println(t);
			t = rules.nextTransition(t.getInstant());
		}
	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/time/DaylightSavingTime.java)
