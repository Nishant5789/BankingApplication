package utils;

public class ValidationUtils {
    // Validate username format
    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9._-]{5,15}$");
    }

    // Validate password format
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    // Validate security answer
    public static boolean isValidSecurityAnswer(String answer) {
        return answer != null && answer.trim().length() > 0;
    }

}
