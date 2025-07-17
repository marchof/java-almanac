package io.javaalmanac.data.output;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

class MD5 {

	private byte[] digest;

	MD5(String content) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(content.getBytes(StandardCharsets.UTF_8));
			this.digest = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	String hexInQuotes() {
		return '"' + hex() + '"';
	}

	String hex() {
		StringBuilder str = new StringBuilder();
		for (byte b : digest) {
			str.append(Character.forDigit((b >> 4) & 0x0F, 16));
			str.append(Character.forDigit((b & 0x0F), 16));
		}
		return str.toString();
	}

	String base64() {
		return Base64.getEncoder().encodeToString(digest);
	}

}
