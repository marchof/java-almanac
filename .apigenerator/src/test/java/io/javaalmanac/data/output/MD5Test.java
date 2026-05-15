package io.javaalmanac.data.output;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MD5Test {

	@Test
	void should_render_hash_in_hex() {
		var md5 = new MD5("javaalmanac");
		assertEquals("184057a324dda1206428fbf0d146b5c6", md5.hex());
	}

	@Test
	void should_render_hash_in_quoted_hex() {
		var md5 = new MD5("javaalmanac");
		assertEquals("\"184057a324dda1206428fbf0d146b5c6\"", md5.hexInQuotes());
	}

	@Test
	void should_render_hash_in_base64() {
		var md5 = new MD5("javaalmanac");
		assertEquals("GEBXoyTdoSBkKPvw0Ua1xg==", md5.base64());
	}

}
