---
title: Copy Files and IO Streams
---

The [`Files`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/file/Files.html) class offers a couple of convenience methods
to copy data between files, [`InputStream`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/io/InputStream.html)s and
[`OutputStream`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/io/OutputStream.html)s. Also streams can be directly transferred with
[`InputStream.transferTo()`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/io/InputStream.html#transferTo(java.io.OutputStream)). Many Java
idioms with temporary buffers shuffling data chunks in `while` loops are
obsolete nowadays.

Since [Java 9](/jdk/9/)

{{< sandbox version="java25" mainclass="CopyFiles" >}}
{{< sandboxsource "CopyFiles.java" >}}

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HexFormat;

public class CopyFiles {

	static final HexFormat HEX = HexFormat.of();

	static final byte[] TEST_CONTENT = HEX.parseHex("cafebabe");

	public static void main(String... args) throws IOException {

		var srcfile = Files.createTempFile("CopyFiles", ".txt");
		var dstfile = Files.createTempFile("CopyFiles", ".txt");
		Files.write(srcfile, TEST_CONTENT);

		// Directly copy files
		Files.copy(srcfile, dstfile, StandardCopyOption.REPLACE_EXISTING);

		System.out.println(HEX.formatHex(Files.readAllBytes(dstfile)));

		// Copy data from InputStream to a file:
		var in = new ByteArrayInputStream(TEST_CONTENT);
		Files.copy(in, dstfile, StandardCopyOption.REPLACE_EXISTING);

		System.out.println(HEX.formatHex(Files.readAllBytes(dstfile)));

		// Copy the file content to an OutputStream:
		var out = new ByteArrayOutputStream();
		Files.copy(srcfile, out);

		System.out.println(HEX.formatHex(out.toByteArray()));

		// Copy content from an InputStream to an OutputStream:
		in = new ByteArrayInputStream(TEST_CONTENT);
		out = new ByteArrayOutputStream();
		in.transferTo(out);

		System.out.println(HEX.formatHex(out.toByteArray()));

	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/io/CopyFiles.java)
