---
title: What time is it?
type: sandbox
---
SBB Developer Day 2021
## LocalTime, Duration
{{< sandbox version="java17" mainclass="ExampleLocalTime" >}}
{{< sandboxsource "ExampleLocalTime.java" >}}
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class ExampleLocalTime {

	public static void main(String[] args) {
		LocalTime t = LocalTime.of(13, 30);
		System.out.println(t);

		System.out.println(t.plus(5, ChronoUnit.HOURS));

		System.out.println(t.plus(11, ChronoUnit.HOURS));

		System.out.println(Duration.between(LocalTime.of(8, 15), t));
	}

}
{{< /sandboxsource >}}
{{< /sandbox >}}
## LocalDate, LocalDateTime
{{< sandbox version="java17" mainclass="ExampleLocalDate" >}}
{{< sandboxsource "ExampleLocalDate.java" >}}
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class ExampleLocalDate {

	public static void main(String[] args) {
		LocalDate d = LocalDate.of(2021, 9, 21);
		System.out.println(d);

		System.out.println(d.getDayOfWeek());

		System.out.println(d.plus(6, ChronoUnit.MONTHS));

		System.out.println(d.atTime(LocalTime.of(14, 10)));

		System.out.println(LocalDateTime.of(2021, 9, 21, 14, 10));
	}

}
{{< /sandboxsource >}}
{{< /sandbox >}}
## Duration vs. Period
{{< sandbox version="java17" mainclass="ExamplePeriod" >}}
{{< sandboxsource "ExamplePeriod.java" >}}
import java.time.LocalDate;
import java.time.Period;

public class ExamplePeriod {

	public static void main(String[] args) {
		LocalDate from = LocalDate.of(2021, 9, 21);
		LocalDate to = LocalDate.of(2021, 12, 31);

		System.out.println(Period.between(from, to));
	}

}
{{< /sandboxsource >}}
{{< /sandbox >}}
## Temporal Adjusters
{{< sandbox version="java17" mainclass="ExampleTemporalAdjusters" >}}
{{< sandboxsource "ExampleTemporalAdjusters.java" >}}
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.SUNDAY;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class ExampleTemporalAdjusters {

	public static void main(String[] args) {
		var today = LocalDate.of(2021, 9, 21);
		System.out.println(today.with(TemporalAdjusters.next(FRIDAY)));
		System.out.println(today.with(TemporalAdjusters.lastInMonth(SUNDAY)));
		System.out.println(today.with(TemporalAdjusters.lastDayOfMonth()));
	}

}
{{< /sandboxsource >}}
{{< /sandbox >}}
## Calendar Systems
{{< sandbox version="java17" mainclass="ExampleCalendarSystems" >}}
{{< sandboxsource "ExampleCalendarSystems.java" >}}
import java.time.LocalDate;
import java.time.chrono.Chronology;

public class ExampleCalendarSystems {

	public static void main(String[] args) {
		var today = LocalDate.of(2021, 9, 21);

		Chronology.getAvailableChronologies().stream() //
				.map(c -> c.date(today)) //
				.forEach(System.out::println);
	}

}
{{< /sandboxsource >}}
{{< /sandbox >}}
## Time Zones in Java
{{< sandbox version="java17" mainclass="ExampleTimeZones" >}}
{{< sandboxsource "ExampleTimeZones.java" >}}
import java.time.ZoneId;

public class ExampleTimeZones {

	public static void main(String[] args) {
		System.out.println(ZoneId.getAvailableZoneIds());
	}

}
{{< /sandboxsource >}}
{{< /sandbox >}}
## Time Zones Rules
{{< sandbox version="java17" mainclass="ExampleTimeZoneRules" >}}
{{< sandboxsource "ExampleTimeZoneRules.java" >}}
import java.time.Instant;
import java.time.ZoneId;

public class ExampleTimeZoneRules {

	public static void main(String[] args) {
		ZoneId zone = ZoneId.of("Europe/Zurich");
		System.out.println(zone);
		
		Instant now = Instant.now();
		System.out.println(zone.getRules().getOffset(now));
		System.out.println(zone.getRules().getDaylightSavings(now));
	}

}
{{< /sandboxsource >}}
{{< /sandbox >}}
## ZonedDateTime
{{< sandbox version="java17" mainclass="ExampleZonedDateTime" >}}
{{< sandboxsource "ExampleZonedDateTime.java" >}}
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ExampleZonedDateTime {
	
	public static void main(String[] args) {
		var localdatetime = LocalDateTime.of(2021, 10, 31, 2, 30);
		System.out.println(localdatetime);
		
		ZoneId zone = ZoneId.of("Europe/Zurich");
		ZonedDateTime zoneddatetime = localdatetime.atZone(zone);
		System.out.println(zoneddatetime);

		zoneddatetime = zoneddatetime.withLaterOffsetAtOverlap();
		System.out.println(zoneddatetime);
	}

}
{{< /sandboxsource >}}
{{< /sandbox >}}
## Antarctica
{{< sandbox version="java17" mainclass="ExampleAntarctica" >}}
{{< sandboxsource "ExampleAntarctica.java" >}}
import java.time.Instant;
import java.time.ZoneId;

public class ExampleAntarctica {

	public static void main(String[] args) {
		var now = Instant.now();

		ZoneId.getAvailableZoneIds().stream() //
				.filter(s -> s.startsWith("Antarctica")) //
				.map(ZoneId::of) //
				.map(now::atZone) //
				.sorted() //
				.map(z -> z.getOffset() + " " + z.getZone()) //
				.forEach(System.out::println);
	}

}
{{< /sandboxsource >}}
{{< /sandbox >}}
## Time Zone Offset Range
{{< sandbox version="java17" mainclass="ExampleOffsetRange" >}}
{{< sandboxsource "ExampleOffsetRange.java" >}}
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

public class ExampleOffsetRange {

	public static void main(String[] args) {
		var now = Instant.now();

		var offsets = ZoneId.getAvailableZoneIds().stream() //
				.map(ZoneId::of) //
				.map(now::atZone) //
				.map(ZonedDateTime::getOffset).toList();
	
		System.out.println(Collections.min(offsets));
		System.out.println(Collections.max(offsets));
	}

}
{{< /sandboxsource >}}
{{< /sandbox >}}
## Samoa
{{< sandbox version="java17" mainclass="ExampleSamoa" >}}
{{< sandboxsource "ExampleSamoa.java" >}}
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

public class ExampleSamoa {

	public static void main(String[] args) {
		var tz = ZoneId.of("Pacific/Apia");
		var start = ZonedDateTime.of(LocalDateTime.of(2011, 12, 29, 20, 0), tz);
		var end = ZonedDateTime.of(LocalDateTime.of(2011, 12, 31, 4, 0), tz);

		Stream.iterate(start, t -> t.plus(Duration.ofHours(1))) //
				.takeWhile(end::isAfter) //
				.forEach(System.out::println);
		
		System.out.println(Duration.between(start, end));
		System.out.println(Period.between(start.toLocalDate(), end.toLocalDate()));
	}

}
{{< /sandboxsource >}}
{{< /sandbox >}}
## Büsingen
{{< sandbox version="java17" mainclass="ExampleBuesingen" >}}
{{< sandboxsource "ExampleBuesingen.java" >}}
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;

public class ExampleBuesingen {
	
	public static void main(String[] args) {
		var zones = List.of("Europe/Berlin", "Europe/Busingen", "Europe/Zurich");

		for (var year : new int[] { 1979, 1980, 1981 }) {
			var date = LocalDate.of(year, Month.AUGUST, 1).atStartOfDay();
			System.out.println(date);
			for (var zone : zones) {
				var offset = ZoneId.of(zone).getRules().getOffset(date);
				System.out.printf("  %s %s%n", offset, zone);
			}
		}
	}

}
{{< /sandboxsource >}}
{{< /sandbox >}}
## Instant
{{< sandbox version="java17" mainclass="ExampleInstant" >}}
{{< sandboxsource "ExampleInstant.java" >}}
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ExampleInstant {

	public static void main(String[] args) {
		System.out.println(Instant.MIN);
		System.out.println(Instant.MAX);
		
		var now = Instant.now();
		System.out.println(now);
		
		System.out.println(now.plus(1, ChronoUnit.NANOS));
	}

}
{{< /sandboxsource >}}
{{< /sandbox >}}
## Time Left
{{< sandbox version="java17" mainclass="ExampleTimeLeft" >}}
{{< sandboxsource "ExampleTimeLeft.java" >}}
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;

public class ExampleTimeLeft {
	
	public static void main(String[] args) {
		var start = ZonedDateTime.parse("2021-09-21T14:00+02:00");
		
		System.out.println(Duration.between(start.toInstant(), Instant.now()));
	}

}
{{< /sandboxsource >}}
{{< /sandbox >}}
