package com.nexoraa.skilllink;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHashTest {
    @Test
    void bcryptHashMatchesOriginalButNotADifferentPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String raw = "UnitTestOnly-" + System.nanoTime();
        String hash = encoder.encode(raw);

        assertNotEquals(raw, hash);
        assertTrue(encoder.matches(raw, hash));
        assertFalse(encoder.matches(raw + "-changed", hash));
    }
}
