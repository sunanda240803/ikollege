
package com.iitm.hosteldine.constant;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class AutoGenerateString {

	public static final char[] SECURE_NUMBERS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	public static final char[] HEX_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9' };

	public static final char[] SECURE_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
			'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9' };

	public static final char[] SECURE_ALPHA_SPECIAL_CHARS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
			'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	private static final char[] symbols = new char[SECURE_CHARS.length];

	static {
		for (int idx = 0; idx < SECURE_CHARS.length; ++idx) {
			symbols[idx] = (char) (SECURE_CHARS[idx]);
		}

	}

	private final Random random = new Random();

	private char[] buf;

	public String nextString(int length) {
		if (length < 1)
			throw new IllegalArgumentException("length < 1: " + length);
		buf = new char[length];
		for (int idx = 0; idx < buf.length; ++idx)

		{
			buf[idx] = symbols[random.nextInt(symbols.length)];

		}

		return new String(buf);

	}

}
