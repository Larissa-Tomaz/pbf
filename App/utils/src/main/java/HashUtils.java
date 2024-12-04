package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtils {

    public static String hashPassword(String password) {   
        String hashedPassword = hashData(password);  
        return hashedPassword;
    }

    public static boolean checkPassword(String password, String storedHash) {
        String hashedInput = hashData(password);
        return hashedInput.equals(storedHash);
    }

    public static String hashData(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(data.getBytes());

            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing data", e);
        }
    }
}
