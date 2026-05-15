package io.javaalmanac.data.output;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;

class MD5 {

	private static final HexFormat HEX_FORMAT = HexFormat.of();

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
		return HEX_FORMAT.formatHex(digest);
	}

	String base64() {
		return Base64.getEncoder().encodeToString(digest);
	}

}
