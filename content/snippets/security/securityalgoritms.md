---
title: JDK Security Algorithms
---

The JDK comes with a long list of cryptographic algorithms for different
[`Service`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/Provider.Service.html) types. This snippet lists all service
types together with the algorithms implemented in the current JDK.

Since [Java 1.1](/jdk/1.1/)

{{< sandbox version="java25" mainclass="SecurityAlgoritms" >}}
{{< sandboxsource "SecurityAlgoritms.java" >}}

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toCollection;

import java.security.Provider.Service;
import java.security.Security;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

public class SecurityAlgoritms {

	static Stream<Service> getAllSecurityServices() {
		return Arrays.stream(Security.getProviders()).flatMap(p -> p.getServices().stream());
	}

	public static void main(String[] args) {

		var grouped = getAllSecurityServices().collect(
				// Group algorithms by type
				groupingBy(Service::getType,
						// Sort types and algorithms using tree collections
						TreeMap::new, mapping(Service::getAlgorithm, toCollection(TreeSet::new))));

		grouped.forEach((type, algorithms) -> {
			System.out.println(type);
			algorithms.forEach(a -> System.out.println("* " + a));
		});

	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/security/SecurityAlgoritms.java)
