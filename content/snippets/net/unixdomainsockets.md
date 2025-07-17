---
title: Unix Domain Sockets
---

Since Java 16 finally Unix domain local interprocess communication is
 supported.

Since [Java 16](/jdk/16/)

{{< sandbox version="java21" mainclass="UnixDomainSockets" >}}
{{< sandboxsource "UnixDomainSockets.java" >}}

import static java.net.StandardProtocolFamily.UNIX;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;

public class UnixDomainSockets {

	public static void main(String... args) throws IOException {

		// Create a server socket on a temporary file name
		var server = ServerSocketChannel.open(UNIX);
		server.bind(null);
		var address = (UnixDomainSocketAddress) server.getLocalAddress();
		System.out.println("Listening on " + address);

		// Connect client and send a message
		try (var client = SocketChannel.open(UNIX)) {
			client.connect(address);
			client.write(ByteBuffer.wrap("Hello Socket!".getBytes(UTF_8)));
		}

		// Accept connection and read the message from it
		try (var input = Channels.newInputStream(server.accept())) {
			input.transferTo(System.out);
		}

		// Cleanup
		server.close();
		Files.delete(address.getPath());

	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/net/UnixDomainSockets.java)
