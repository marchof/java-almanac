---
title: Diffie-Hellman Key Agreement
---

With key agreement algorithms two parties can agree on a shared secret by
exchanging information over public channels only.

Since [Java 1.4](/jdk/1.4/)

{{< sandbox version="java25" mainclass="DiffieHellmanKeyAgreement" >}}
{{< sandboxsource "DiffieHellmanKeyAgreement.java" >}}

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.HexFormat;

import javax.crypto.KeyAgreement;

public class DiffieHellmanKeyAgreement {

	static class Party {

		private KeyPair key;
		private KeyAgreement agreement;

		Party() throws Exception {
			key = KeyPairGenerator.getInstance("DiffieHellman").generateKeyPair();
			agreement = KeyAgreement.getInstance("DiffieHellman");
			agreement.init(key.getPrivate());
		}

		PublicKey getPublicKey() {
			return key.getPublic();
		}

		void retrieveOtherPublicKey(PublicKey key) throws Exception {
			agreement.doPhase(key, true);
		}

		String generateSecret() {
			byte[] secret = agreement.generateSecret();
			return HexFormat.of().formatHex(secret);
		}

	}

	void main() throws Exception {
		var alice = new Party();
		var bob = new Party();

		// Alice and Bob exchange their public keys
		alice.retrieveOtherPublicKey(bob.getPublicKey());
		bob.retrieveOtherPublicKey(alice.getPublicKey());

		// Now both parties can calculate a shared secret:
		IO.println("Alice: " + alice.generateSecret());
		IO.println("Bob:   " + bob.generateSecret());
	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/security/DiffieHellmanKeyAgreement.java)
