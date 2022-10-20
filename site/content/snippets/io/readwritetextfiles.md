---
title: Read and Write Text Files
type: sandbox
---

The [`Files`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/file/Files.html) class offers a couple of convenience methods
 to read and write text content from/to files. When converting between Unicode
 strings and binary files content a encoding is applied. Do not rely on system
 defaults, always supply an encoding for portable behavior.

Since [Java 11](/jdk/11/)

{{< sandbox version="java17" mainclass="ReadWriteTextFiles" >}}
{{< sandboxsource "ReadWriteTextFiles.java" >}}

import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ReadWriteTextFiles {

	static final String TEST_CONTENT = "Line 1\nLine 2\nLine 3\n";

	public static void main(String... args) throws IOException {

		var file = Files.createTempFile("ReadWriteTextFiles", ".txt");

		// Write out a string to a file
		Files.writeString(file, TEST_CONTENT, StandardCharsets.UTF_8);

		// Read the entire content as one string:
		System.out.print(Files.readString(file, StandardCharsets.UTF_8));

		// Read the entire content as a stream of lines:
		Files.lines(file, StandardCharsets.UTF_8).map("> "::concat).forEach(System.out::println);

		// Read the entire content into a list of lines:
		var lines = Files.readAllLines(file, StandardCharsets.UTF_8);
		System.out.println(String.join("/", lines));

		// If we need a Reader instance we can directly open a file as a BufferedReader
		try (var reader = new LineNumberReader(Files.newBufferedReader(file, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.printf("%d: %s%n", reader.getLineNumber(), line);
			}
		}

		Files.delete(file);

	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/io/ReadWriteTextFiles.java)
