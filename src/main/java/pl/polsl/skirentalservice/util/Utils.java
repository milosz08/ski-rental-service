package pl.polsl.skirentalservice.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    public static String generateHash(String preCoded) {
        return BCrypt.withDefaults().hashToString(10, preCoded.toCharArray());
    }
}
