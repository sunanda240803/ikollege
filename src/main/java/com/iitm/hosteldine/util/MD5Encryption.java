package com.iitm.hosteldine.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author Jegan
 */
public class MD5Encryption {

	/**
	 *
	 * @param nonEncrStr
	 * @return	
	 */
	public static String md5Encrypt(String nonEncrStr) {
		// sanity check...
		if (nonEncrStr == null) {
			return "-1";
		}

		if (nonEncrStr.length() == 0) {
			return "-1";
		}

		// start encryption process...
		byte[] bStr = nonEncrStr.getBytes();
		MessageDigest algorithm = null;
		try {
			// algorithm = MessageDigest.getInstance ("SHA-1");
			// Alternate... but we not using
			algorithm = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
			return "-1";
		}

		// reset encryption algorithm
		algorithm.reset();
		algorithm.update(bStr);

		byte digest[] = algorithm.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			hexString.append(hexDigit(digest[i]));
		}

		// return the excryption string...
		return hexString.toString();
	}

	/**
	 *
	 * @param x
	 * @return
	 */
	static private String hexDigit(byte x) {
		StringBuffer sb = new StringBuffer();
		char c;
		// First nibble
		c = (char) ((x >> 4) & 0xf);
		if (c > 9) {
			c = (char) ((c - 10) + 'a');
		} else {
			c = (char) (c + '0');
		}

		sb.append(c);

		// Second nibble
		c = (char) (x & 0xf);
		if (c > 9) {
			c = (char) ((c - 10) + 'a');
		} else {
			c = (char) (c + '0');
		}
		sb.append(c);

		return sb.toString();
	}

	public static void main(String[] ar) {
		//default live password : OMenDrOA -> 3d9d865bb20960d6f1acef2ede35ca21
		//default dev password: hosteldine@123 -> 3820affb8b5f5f73f649eecb13e60db7
		//default dev password: ohm@123 -> 3b1e9305d6688bc0c3d20111698a8390
		//default dev password: hosteloffice@123 -> 6b35b63998ea9389df2eba29898ac3e5
		String encryptedpassword = md5Encrypt("ohm@123");
		System.out.println(encryptedpassword);

	}
}
