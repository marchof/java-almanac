---
title: Message Authentication Code
type: sandbox
---

A message authentication code is calculated based with a private key. Only
 owners of the corresponding private key can calculate and verify the
 authentication code. Because the private key cannot be derived from the
 generated hash this is a secure method to e.g. sign API calls which are
 transmitted over unsafe transport channels. For example verification links
 send via email.

Since [Java 1.4](/jdk/1.4)

{{< sandbox version="java17" mainclass="MessageAuthenticationCode" >}}
{{< sandboxsource "MessageAuthenticationCode.java" >}}

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

public class MessageAuthenticationCode {

	static void addSignature(Mac mac, String msg) {
		mac.reset();
		byte[] signature = mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
		System.out.printf("%s&hmac=%s%n", msg, HexFormat.of().formatHex(signature));
	}

	public static void main(String[] args) throws Exception {
		SecretKey key = KeyGenerator.getInstance("HmacSHA3-256").generateKey();
		Mac mac = Mac.getInstance("HmacSHA3-256");
		mac.init(key);

		// Same content will create the same signature
		addSignature(mac, "confirm=alice@example.com&date=20210917");
		addSignature(mac, "confirm=alice@example.com&date=20210917");

		// Different content will result in a different signature
		addSignature(mac, "confirm=marvin@example.com&date=20210917");
	}
}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/security/MessageAuthenticationCode.java)
