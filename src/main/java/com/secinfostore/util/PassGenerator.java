package com.secinfostore.util;

import java.security.SecureRandom;

public class PassGenerator {
    public static String generatePassword(String Characters, int length) {
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(Characters.length());
            char randomChar = Characters.charAt(randomIndex);
            password.append(randomChar);
        }
        return password.toString();
    }
}
