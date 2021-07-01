---
title: Read and Write Binary Files
type: sandbox
---

The [`Files`](https://download.java.net/java/early_access/jdk17/docs/api/java.base/java/nio/file/Files.html) class offers a couple of convenience methods
 to read and write binary content from/to files.

Since [Java 11](/jdk/11)

{{< sandbox version="java17" mainclass="ReadWriteBinaryFiles" >}}
{{< sandboxsource "ReadWriteBinaryFiles.java" >}}

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HexFormat;

public class ReadWriteBinaryFiles {

	static final HexFormat HEX = HexFormat.of();

	static final byte[] TEST_CONTENT = HEX.parseHex("cafebabe");

	public static void main(String... args) throws IOException {

		var file = Files.createTempFile("ReadWriteBinaryFiles", ".txt");

		// Write out a raw byte array to a file
		Files.write(file, TEST_CONTENT);

		// Read the entire content as one byte array:
		var content = Files.readAllBytes(file);
		System.out.println(HEX.formatHex(content));

		// Append binary content with an OutputStream:
		try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.APPEND)) {
			out.write(TEST_CONTENT);
		}

		// Read binary content from an InputStream:
		try (InputStream in = Files.newInputStream(file)) {
			content = in.readAllBytes();
			System.out.println(HEX.formatHex(content));
		}

		Files.delete(file);

	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/io/ReadWriteBinaryFiles.java)
