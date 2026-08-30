package com.starprotect.service;

import com.starprotect.model.Underwriter;
import com.starprotect.repository.UnderwriterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UnderwriterService {

    @Autowired
    private UnderwriterRepository underwriterRepository;

    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$");

    // Calculate age from DOB
    public int calculateAge(LocalDate dob) {
        if (dob == null) return 0;
        return Period.between(dob, LocalDate.now()).getYears();
    }

    // Validate password format
    public boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    // Register new underwriter
    public String registerUnderwriter(String name, LocalDate dob, LocalDate joiningDate, String password) {
        // Validate age (must be 18+)
        if (calculateAge(dob) < 18) {
            return "Underwriter must be at least 18 years old!";
        }
        
        // Validate password
        if (!isValidPassword(password)) {
            return "Password must contain alphanumeric and special character!";
        }
        
        String underwriterId = underwriterRepository.getNextUnderwriterId();
        Underwriter underwriter = new Underwriter(underwriterId, name, dob, joiningDate, password);
        underwriterRepository.save(underwriter);
        
        return "SUCCESS:" + underwriterId;
    }

    // Search underwriter by ID
    public Underwriter searchUnderwriter(String underwriterId) {
        return underwriterRepository.findById(underwriterId);
    }

    // Update password
    public String updatePassword(String underwriterId, String newPassword, String confirmPassword) {
        if (!underwriterRepository.existsById(underwriterId)) {
            return "No Such Underwriter Exist with the Given Id.";
        }
        
        if (!newPassword.equals(confirmPassword)) {
            return "Passwords do not match!";
        }
        
        if (!isValidPassword(newPassword)) {
            return "Password criteria doesn't match, please enter a combination of alphabets, numbers and special characters.";
        }
        
        underwriterRepository.updatePassword(underwriterId, newPassword);
        return "SUCCESS:Password updated for Id: " + underwriterId;
    }

    // Delete underwriter
    public String deleteUnderwriter(String underwriterId) {
        if (!underwriterRepository.existsById(underwriterId)) {
            return "No Such Underwriter Exist with the Given Id.";
        }
        underwriterRepository.deleteById(underwriterId);
        return "SUCCESS:Underwriter with Id: " + underwriterId + " is deleted.";
    }

    // Get all underwriters
    public List<Underwriter> getAllUnderwriters() {
        return underwriterRepository.findAll();
    }

    // Login validation for underwriter
    public Underwriter validateLogin(String userId, String password) {
        return underwriterRepository.findByCredentials(userId, password);
    }
}