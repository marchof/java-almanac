---
title: Collection Constants
type: sandbox
---

The collection interfaces [`Set`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Set.html), [`List`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/List.html) and [`Map`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Map.html) have several static `of(...)`
 methods to create constant instances of the respective collection types.
 Collections created this way do not allow `null` elements and any
 mutation method throws an [`UnsupportedOperationException`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/UnsupportedOperationException.html).
 Also as these methods are designed to declare constants duplicate set entries
 or map keys are considered as an error.

Since [Java 9](/jdk/9/)

{{< sandbox version="java17" mainclass="CollectionConstants" >}}
{{< sandboxsource "CollectionConstants.java" >}}

import java.awt.Color;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionConstants {

	static final Set<Color> BAVARIAN_COLORS = Set.of(Color.WHITE, Color.BLUE);

	static final List<DayOfWeek> LONG_WEEKEND = List.of(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

	static final Map<String, Integer> PEAK_ELEVATIONS = Map.of( //
			"Mauna Key", 4205, //
			"Matterhorn", 4478, //
			"Makalu", 8485);

	public static void main(String... args) {

		System.out.println("The elevation of Matterhorn is " + PEAK_ELEVATIONS.get("Matterhorn"));

		// Invalid usages:

		try {
			BAVARIAN_COLORS.add(Color.BLACK);
		} catch (UnsupportedOperationException e) {
			System.out.println("Constant collections cannot be modified");
		}

		try {
			List.of("null", null);
		} catch (NullPointerException e) {
			System.out.println("null is not allowed in constant collections");
		}

		try {
			Set.of("same", "same");
		} catch (IllegalArgumentException e) {
			System.out.println("Duplicate entries are not allowed for constant sets");
		}

		try {
			Map.of("key", 1, "key", 2);
		} catch (IllegalArgumentException e) {
			System.out.println("Duplicate keys are not allowed for constant maps");
		}

	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/util/CollectionConstants.java)
