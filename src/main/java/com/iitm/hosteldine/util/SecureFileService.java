package com.iitm.hosteldine.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecureFileService {
    private final KeyManagementService keyManagementService;
    private SecretKey secretKey;

    // Encrypt the byte array
    public byte[] encryptFile(byte[] fileBytes) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        return cipher.doFinal(fileBytes);
    }

    // Decrypt the byte array
    public byte[] decryptFile(byte[] encryptedBytes) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
        return cipher.doFinal(encryptedBytes);
    }

    // Save the encrypted file
    public boolean saveEncryptedFile(byte[] fileBytes, String filePath) throws Exception {
        byte[] encryptedBytes = encryptFile(fileBytes);
        return saveFile(encryptedBytes, filePath + ".e");
    }

    public boolean saveFile(byte[] fileBytes, String filePath) throws Exception {
        boolean fileSaved = false;
        File file = new File(filePath);
        boolean fileExists = file.exists();
        if (!fileExists) {
            if (!file.getParentFile().exists()) {
                fileExists = file.getParentFile().mkdirs();
            } else fileExists = true;
            if (fileExists) {
                fileExists = file.createNewFile();
            }
        }
        StringBuilder logStr = new StringBuilder("File created: ").append(fileExists)
                .append(" | File size: ").append(fileBytes.length);
        if (fileExists) {
            Files.write(Paths.get(filePath), fileBytes);
            file = new File(filePath);
            logStr.append(" | File created size: ").append(file.length());
            fileSaved = file.length() == fileBytes.length;
        }
        logStr.append(" | File Saved").append(fileSaved);
        log.info(logStr.toString());
        return fileSaved;
    }

    // Delete the encrypted file
    public boolean deleteEncryptedFile(String filePath) {
        return deleteFile(filePath + ".e");
    }

    public boolean deleteFile(String filePath) {
        boolean fileDeleted = true;
        File file = new File(filePath);
        boolean fileExists = file.exists();
        if (fileExists) {
            if (file.isFile()) {
                fileDeleted = file.delete();
            }
        }
        return fileDeleted;
    }

    // Read and decrypt the file
    public byte[] readEncryptedFile(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
        	filePath += ".e";
        }
		if (!file.exists()) {
			return null;
		}
        byte[] fileBytes = readFile(filePath);
        if (filePath.endsWith(".e")) {
            return decryptFile(fileBytes);
        } else {
            return fileBytes;
        }
    }

    public byte[] readFile(String filePath) throws Exception {
        return Files.readAllBytes(Paths.get(filePath));
    }

    public SecretKey getSecretKey() {
        if (secretKey == null) {
            try {
                secretKey = keyManagementService.loadKey();
            } catch (IOException e) {
                try {
                    keyManagementService.generateAndStoreKey();
                    secretKey = keyManagementService.loadKey();
                } catch (NoSuchAlgorithmException | IOException ignore) {
                }
            }
        }
        return secretKey;
    }
}
