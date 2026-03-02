package util;

import java.util.regex.Pattern;


public class ValidationUtil {

    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    //check if email is in right format
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Check if password is strong.
     * Rules:
     * - At least 8 characters
     * - At least one uppercase letter
     * - At least one number
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;

        boolean hasUpper = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isDigit(c)) hasDigit = true;
        }

        return hasUpper && hasDigit;
    }

    //text field not empty
    public static boolean isNotEmpty(String text) {
        return text != null && !text.isBlank();
    }

    //star rating is properly given
    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }
}