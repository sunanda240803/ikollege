package com.iitm.hosteldine.util;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;


@Slf4j
public class MCrypt {


    static char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private final IvParameterSpec ivSpec;
    private final SecretKeySpec keySpec;
    private Cipher cipher;

    private static MCrypt mCrypt;

    public static MCrypt getInstance() {
        if (mCrypt == null) {
            mCrypt = new MCrypt();
        }
        return mCrypt;
    }

    public MCrypt() {
//      String iv = "hosteldinea98765";
//      String secretKey = "56789ahosteldine";
		String iv = "fedcba9876543210";
		String secretKey = "0123456789abcdef";
        ivSpec = new IvParameterSpec(iv.getBytes());

        keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");

        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException", e);
        } catch (NoSuchPaddingException e) {
            log.error("NoSuchPaddingException", e);
        }
    }

    public byte[] encrypt(String text) throws Exception {
        if (Strings.isEmpty(text))
            throw new Exception("Empty string");

        byte[] encrypted;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            encrypted = cipher.doFinal(padString(text).getBytes());
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encrypted;
    }

    public byte[] decrypt(String code) throws Exception {
        if (Strings.isEmpty(code))
            throw new Exception("Empty string");

        byte[] decrypted;

        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            decrypted = cipher.doFinal(hexToBytes(code));
            // Remove trailing zeroes
            if (decrypted.length > 0) {
                int trim = 0;
                for (int i = decrypted.length - 1; i >= 0; i--)
                    if (decrypted[i] == 0)
                        trim++;

                if (trim > 0) {
                    byte[] newArray = new byte[decrypted.length - trim];
                    System.arraycopy(decrypted, 0, newArray, 0,
                            decrypted.length - trim);
                    decrypted = newArray;
                }
            }
        } catch (Exception e) {
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }

    public static String bytesToHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }

    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(
                        str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }

    private static String padString(String source) {
        char paddingChar = 0;
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        source = source + String.valueOf(paddingChar).repeat(padLength);

        return source;
    }

    public String decryptToString(String text) throws Exception {
        String decryptedText;
        try {
            if (Strings.isNotEmpty(text)) {
                decryptedText = new String(MCrypt.getInstance().decrypt(text));
            } else {
                decryptedText = "";
            }
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
        return decryptedText;
    }

    public boolean decryptToBoolean(String text, String matchStr)
            throws Exception {
        String decryptedText;
        try {
            decryptedText = new String(MCrypt.getInstance().decrypt(text));
            return decryptedText.equalsIgnoreCase(matchStr);
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
    }

    public Double decryptToDouble(String text) throws Exception {
        String decryptedText;
        double decryptedValue;
        try {
            decryptedText = new String(MCrypt.getInstance().decrypt(text));
            decryptedValue = Double.parseDouble(decryptedText);
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
        return decryptedValue;
    }

    public Long decryptToLong(String text) throws Exception {
        String decryptedText;
        long decryptedValue;
        try {
            decryptedText = new String(MCrypt.getInstance().decrypt(text));
            decryptedValue = Long.parseLong(decryptedText);
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
        return decryptedValue;
    }

    public Short decryptToShort(String text) throws Exception {
        String decryptedText;
        short decryptedValue = 0;
        try {
            if (Strings.isNotEmpty(text)) {
                decryptedText = new String(MCrypt.getInstance().decrypt(text));
                decryptedValue = Short.parseShort(decryptedText);
            }
        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
        return decryptedValue;
    }

    public Integer decryptToInt(String text) throws Exception {
        String decryptedText;
        int decryptedValue = 0;
        try {
            if (Strings.isNotEmpty(text)) {
                decryptedText = new String(MCrypt.getInstance().decrypt(text));
                decryptedValue = Integer.parseInt(decryptedText);
            }

        } catch (Exception exception) {
            throw new Exception(exception.getMessage());
        }
        return decryptedValue;
    }

    public String encryptToText(String text) throws Exception {
        String encryptedText = null;
        if (Strings.isNotEmpty(text)) {
            encryptedText = MCrypt.bytesToHex(MCrypt.getInstance().encrypt(text));
        }
        return encryptedText;
    }

    public static void main(String[] arg) throws Exception {
        System.out.println(MCrypt.getInstance().encryptToText("123451"));
        System.out.println(MCrypt.getInstance().encryptToText("123452"));
        System.out.println(MCrypt.getInstance().encryptToText("123453"));
        System.out.println(MCrypt.getInstance().encryptToText("123454"));
        System.out.println(MCrypt.getInstance().encryptToText("123455"));
        System.out.println(MCrypt.getInstance().encryptToText("123456"));
        System.out.println(MCrypt.getInstance().encryptToText("123457"));
        System.out.println(MCrypt.getInstance().encryptToText("123458"));
        System.out.println(MCrypt.getInstance().encryptToText("123459"));
        System.out.println(MCrypt.getInstance().encryptToText("123450"));
        System.out.println(MCrypt.getInstance().decryptToString("f3f7bf58189c9a7a66ba82e2639e344b1fb98290a7727652f8c46afb6a2f6770224f35bb907e76608c4651f17d1f7c14"));
    }
}

