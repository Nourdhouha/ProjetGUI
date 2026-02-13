package com.voyageaffaires.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for password hashing and verification using BCrypt.
 */
public class PasswordUtil {
    
    // BCrypt work factor (log rounds)
    private static final int WORK_FACTOR = 10;
    
    /**
     * Hashes a plain text password using BCrypt.
     * 
     * @param plainPassword The plain text password
     * @return Hashed password
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }
    
    /**
     * Verifies a plain text password against a hashed password.
     * 
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The hashed password to compare against
     * @return true if passwords match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validates password strength.
     * Password must be at least 6 characters long.
     * 
     * @param password The password to validate
     * @return true if password is strong enough, false otherwise
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // Add more complex validation rules as needed
        return true;
    }
}
