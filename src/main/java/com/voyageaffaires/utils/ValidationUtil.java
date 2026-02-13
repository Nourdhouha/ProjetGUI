package com.voyageaffaires.utils;

import java.util.regex.Pattern;

/**
 * Utility class for input validation.
 */
public class ValidationUtil {
    
    // Email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Phone regex pattern (Tunisian format)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{8}$|^\\+216[0-9]{8}$"
    );
    
    /**
     * Validates if a string is not null and not empty.
     * 
     * @param value The string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Validates email format.
     * 
     * @param email The email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validates phone number format.
     * 
     * @param phone The phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Validates if a string has minimum length.
     * 
     * @param value The string to validate
     * @param minLength Minimum required length
     * @return true if valid, false otherwise
     */
    public static boolean hasMinLength(String value, int minLength) {
        return isNotEmpty(value) && value.trim().length() >= minLength;
    }
    
    /**
     * Validates if a number is positive.
     * 
     * @param value The number to validate
     * @return true if positive, false otherwise
     */
    public static boolean isPositive(double value) {
        return value > 0;
    }
    
    /**
     * Validates if a number is within a range.
     * 
     * @param value The number to validate
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return true if within range, false otherwise
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
}
