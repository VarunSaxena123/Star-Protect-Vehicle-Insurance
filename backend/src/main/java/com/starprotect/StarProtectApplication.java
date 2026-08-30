package com.starprotect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StarProtectApplication {
    public static void main(String[] args) {
        SpringApplication.run(StarProtectApplication.class, args);
        System.out.println("=== Star Protect Vehicle Insurance Backend Started ===");
        System.out.println("H2 Console: http://localhost:8080/api/h2-console");
        System.out.println("JDBC URL: jdbc:h2:mem:starprotectdb");
    }
}