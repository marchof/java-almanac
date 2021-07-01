---
title: Convert Between Time Zones
type: sandbox
---

With time zones a given timestamp can be represented in different local dates
 and times.

Since [Java 8](/jdk/8)

{{< sandbox version="java17" mainclass="ConvertBetweenTimeZones" >}}
{{< sandboxsource "ConvertBetweenTimeZones.java" >}}

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

public class ConvertBetweenTimeZones {

	public static void main(String... args) {
		ZonedDateTime lunarEclipse = ZonedDateTime.of(LocalDate.of(2022, 11, 8), LocalTime.of(10, 59), ZoneOffset.UTC);
		List<String> places = List.of( //
				"Asia/Tokyo", "Asia/Kolkata", "Europe/Paris", "America/Fortaleza", "Pacific/Honolulu");

		for (String place : places) {
			System.out.println("Lunar Eclipse at " + lunarEclipse.withZoneSameInstant(ZoneId.of(place)));
		}
	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/time/ConvertBetweenTimeZones.java)
