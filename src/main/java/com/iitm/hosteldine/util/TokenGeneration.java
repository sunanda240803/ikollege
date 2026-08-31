package com.iitm.hosteldine.util;

import java.security.SecureRandom;
import java.util.stream.Collectors;

public class TokenGeneration {

	private static final String SECURE_CHARS =
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private static final SecureRandom RANDOM = new SecureRandom();
	private final int length;

	public TokenGeneration(int length) {
		if (length < 1) {
			throw new IllegalArgumentException("Length must be at least 1");
		}
		this.length = length;
	}

	public String nextString() {
		return RANDOM.ints(length, 0, SECURE_CHARS.length())
				.mapToObj(SECURE_CHARS::charAt)
				.map(String::valueOf)
				.collect(Collectors.joining());
	}

	public String nextNumericString() {
		return RANDOM.ints(length, 0, 10) // 0 to 9
				.mapToObj(Integer::toString)
				.collect(Collectors.joining());
	}

	public static void main(String[] args) {
		var tokenGen = new TokenGeneration(9);
		var token = System.currentTimeMillis() + tokenGen.nextString();
		System.out.println(token);
	}
}
