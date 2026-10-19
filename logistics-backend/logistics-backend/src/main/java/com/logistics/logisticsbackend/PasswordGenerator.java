package com.logistics.logisticsbackend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = System.getenv("LOGISTICS_TEMP_PASSWORD");

        String encodedPassword = encoder.encode(password);

        System.out.println("BCrypt Password Hash:");
        System.out.println(encodedPassword);

        System.out.println("Password Match: "
                + encoder.matches(password, encodedPassword));
    }
}