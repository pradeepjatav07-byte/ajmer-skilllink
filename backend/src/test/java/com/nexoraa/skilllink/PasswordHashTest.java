package com.nexoraa.skilllink;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHashTest {
    @Test void documentedDemoPasswordMatchesTheSeededBcryptHash() {
        String hash = "$2b$10$8Z73/jCwjxHCb5VNIfC.r.kvYOakM3ch3yB4MajQyVrnSnUpbSzKW";
        assertTrue(new BCryptPasswordEncoder().matches("SkillLink@123", hash));
    }
}
