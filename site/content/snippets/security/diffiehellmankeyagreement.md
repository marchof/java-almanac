---
title: Diffie-Hellman Key Agreement
---

With key agreement algorithms two parties can agree on a shared secret by
 exchanging information over public channels only.

Since [Java 1.4](/jdk/1.4/)

{{< sandbox version="java17" mainclass="DiffieHellmanKeyAgreement" >}}
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

		void printSecret() {
			byte[] secret = agreement.generateSecret();
			System.out.println(HexFormat.of().formatHex(secret));
		}

	}

	public static void main(String[] args) throws Exception {
		var alice = new Party();
		var bob = new Party();

		// Alice and Bob exchange their public keys
		alice.retrieveOtherPublicKey(bob.getPublicKey());
		bob.retrieveOtherPublicKey(alice.getPublicKey());

		// Now both parties can calculate a shared secret:
		alice.printSecret();
		bob.printSecret();
	}

}

{{< /sandboxsource >}}
{{< /sandbox >}}

This [snippet at GitHub](https://github.com/marchof/io.javaalmanac.snippets/tree/master/src/main/java/io/javaalmanac/snippets/security/DiffieHellmanKeyAgreement.java)
