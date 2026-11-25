package com.example.malotiprime.model;

public final class DemoCredentials {
    public static final String EMAIL = "user1@gmail.com";
    public static final String PASSWORD = "123admin";

    private DemoCredentials() {
    }

    public static boolean authenticate(String email, String password) {
        return email != null
                && EMAIL.equalsIgnoreCase(email.trim())
                && PASSWORD.equals(password);
    }
}
