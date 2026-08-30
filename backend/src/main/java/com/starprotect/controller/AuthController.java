package com.starprotect.controller;

import com.starprotect.dto.LoginRequest;
import com.starprotect.dto.LoginResponse;
import com.starprotect.model.Underwriter;
import com.starprotect.service.UnderwriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UnderwriterService underwriterService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String userId = loginRequest.getUserId();
        String password = loginRequest.getPassword();
        String role = loginRequest.getRole();

        if (role.equals("admin")) {
            if (userId.equals("admin") && password.equals("admin123")) {
                return ResponseEntity.ok(new LoginResponse(true, "admin", "admin", "Administrator", "Login successful"));
            } else {
                return ResponseEntity.ok(new LoginResponse(false, null, null, null, "Invalid admin credentials!"));
            }
        } else if (role.equals("underwriter")) {
            Underwriter underwriter = underwriterService.validateLogin(userId, password);
            if (underwriter != null) {
                return ResponseEntity.ok(new LoginResponse(true, "underwriter", underwriter.getUnderwriterId(), 
                                        underwriter.getName(), "Login successful"));
            } else {
                return ResponseEntity.ok(new LoginResponse(false, null, null, null, "Invalid underwriter credentials!"));
            }
        }
        
        return ResponseEntity.ok(new LoginResponse(false, null, null, null, "Invalid role!"));
    }
}