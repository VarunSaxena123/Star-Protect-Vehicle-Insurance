package com.starprotect.dto;

public class LoginResponse {
    private boolean success;
    private String role;
    private String userId;
    private String name;
    private String message;

    public LoginResponse(boolean success, String role, String userId, String name, String message) {
        this.success = success;
        this.role = role;
        this.userId = userId;
        this.name = name;
        this.message = message;
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getRole() { return role; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getMessage() { return message; }
}