package lk.jiat.envy.util;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenUtil {
    public static String generateToken() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
