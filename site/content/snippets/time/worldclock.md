---
title: World Clock
---

Print the current time in all time zones known to the JDK (from the [tz
 database](https://en.wikipedia.org/wiki/Tz_database)) together with their
 current UTC offset and the DST status.

Since [Java 8](/jdk/8/)

{{< sandbox version="java17" mainclass="WorldClock" >}}
{{< sandboxsource "WorldClock.java" >}}

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class WorldClock {

	static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("E, yyyy-MM-dd HH:mm:ss  xxx", Locale.US);

	static String format(ZonedDateTime t) {
		String dst = t.getZone().getRules().isDaylightSavings(t.toInstant()) ? "DST" : "";
		return "%-32s %s  %s".formatted(t.getZone(), t.format(FORMATTER), dst);
	}

	public static void main(String... args) {
		Instant now = Instant.now();

		ZoneId.getAvailableZoneIds().stream() //
				.map(ZoneId::of) //
				.map((ZoneId z) -> ZonedDateTime.ofInstant(now, z)) //
				.sorted() //
				.map(WorldClock::format)
				.forEach(System.out::println);
	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/time/WorldClock.java)
