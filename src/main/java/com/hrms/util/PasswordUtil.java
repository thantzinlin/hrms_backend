package com.hrms.util;

import java.security.SecureRandom;

public class PasswordUtil {

    // Define the characters to be used in the password
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_+=<>?";

    private static final String ALL_CHARACTERS = UPPER + LOWER + DIGITS + SPECIAL;

    private static final int PASSWORD_LENGTH = 8;

    public static String generateRandomPassword() {
        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        // Ensure that at least one character from each group is included
        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // Fill the rest of the password length with random characters
        for (int i = password.length(); i < PASSWORD_LENGTH; i++) {
            password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }

        // Shuffle the password to ensure randomness
        String result = password.toString();
        result = shuffleString(result);

        return result;
    }

    // Method to shuffle the password string (optional for extra randomness)
    private static String shuffleString(String input) {
        char[] array = input.toCharArray();
        SecureRandom random = new SecureRandom();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return new String(array);
    }

    // public static void main(String[] args) {
    // String randomPassword = generateRandomPassword();
    // System.out.println("Generated Password: " + randomPassword);
    // }
}
