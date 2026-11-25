package com.example.malotiprime.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoCredentialsTest {
    @Test
    void acceptsTheConfiguredDemoCredentials() {
        assertTrue(DemoCredentials.authenticate("user1@gmail.com", "123admin"));
        assertTrue(DemoCredentials.authenticate(" USER1@GMAIL.COM ", "123admin"));
    }

    @Test
    void rejectsIncorrectCredentials() {
        assertFalse(DemoCredentials.authenticate("user1@gmail.com", "wrong"));
        assertFalse(DemoCredentials.authenticate("other@gmail.com", "123admin"));
        assertFalse(DemoCredentials.authenticate(null, "123admin"));
        assertFalse(DemoCredentials.authenticate("user1@gmail.com", null));
    }
}
