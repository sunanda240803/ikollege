package com.iitm.hosteldine.util;

import com.iitm.hosteldine.service.SimsConfigDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class KeyManagementService {
    private final SimsConfigDataService simsConfigDataService;

    // Generate and store the AES key
    public void generateAndStoreKey() throws NoSuchAlgorithmException, IOException {
        // Generate an AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // You can use 192 or 256 bits as well
        SecretKey secretKey = keyGen.generateKey();

        // Store the key in a file
        try (FileOutputStream fos = new FileOutputStream(getKeyFilePath())) {
            fos.write(secretKey.getEncoded());
        }

        System.out.println("Key generated and stored successfully at: " + getKeyFilePath());
    }

    private String getKeyFilePath() {
        return simsConfigDataService.getSimConfigValue("ENCRYPTION_KEY_FILE_PATH");
    }

    // Load the AES key from the file
    public SecretKey loadKey() throws IOException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(getKeyFilePath()));
        return new SecretKeySpec(keyBytes, "AES");
    }
}
