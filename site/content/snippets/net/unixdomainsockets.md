---
title: Unix Domain Sockets
type: sandbox
---

Since Java 16 finally Unix domain local interprocess communication is
 supported.

Since [Java 16](/jdk/16)

{{< sandbox version="java17" mainclass="UnixDomainSockets" >}}
{{< sandboxsource "UnixDomainSockets.java" >}}

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class UnixDomainSockets {

	public static void main(String... args) throws IOException {

		// Allocate a file name
		Path socketfile = Files.createTempFile("UnixDomainSockets", ".socket");
		Files.delete(socketfile);

		// Creqte a server server socket
		ServerSocketChannel server = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
		server.bind(UnixDomainSocketAddress.of(socketfile));

		// Connect client and send a message
		SocketChannel client = SocketChannel.open(StandardProtocolFamily.UNIX);
		client.connect(UnixDomainSocketAddress.of(socketfile));
		Writer writer = Channels.newWriter(client, StandardCharsets.UTF_8);
		writer.write("Hello Socket!\n");
		writer.close();

		// Accept the server connection and read the message from it
		Reader reader = Channels.newReader(server.accept(), StandardCharsets.UTF_8);
		PrintWriter out = new PrintWriter(System.out, true);
		reader.transferTo(out);
		out.flush();

		// Cleanup
		server.close();
		Files.delete(socketfile);

	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/net/UnixDomainSockets.java)
