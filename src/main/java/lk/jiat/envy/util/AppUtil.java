package lk.jiat.envy.util;

import java.security.SecureRandom;

public class AppUtil {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generateCode() {
        int randomNumber = SECURE_RANDOM.nextInt(1_000_000);

        return String.format("%06d", randomNumber);
    }
}
