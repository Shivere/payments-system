package com.example.mamlaka.security.tokenization;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenizationService {

    // Simulate a secure vault by using a Map to store tokens -> encrypted data.
    private static final Map<String, String> tokenVault = new HashMap<>();

    public static String tokenize(String sensitiveData) throws Exception {
        String encryptedData = EncryptionUtil.encrypt(sensitiveData);  // Encrypt the data
        String token = UUID.randomUUID().toString();  // Generate a random token
        tokenVault.put(token, encryptedData);  // Store token -> encrypted data
        return token;
    }

    public static String detokenize(String token) throws Exception {
        String encryptedData = tokenVault.get(token);
        if (encryptedData == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        return EncryptionUtil.decrypt(encryptedData);  // Decrypt and return sensitive data
    }
}
