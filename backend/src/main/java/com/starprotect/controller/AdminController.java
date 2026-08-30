package com.starprotect.controller;

import com.starprotect.model.InsurancePolicy;
import com.starprotect.model.Underwriter;
import com.starprotect.service.InsurancePolicyService;
import com.starprotect.service.UnderwriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UnderwriterService underwriterService;

    @Autowired
    private InsurancePolicyService policyService;

    // ========== Underwriter Management ==========

    @PostMapping("/underwriter/register")
    public ResponseEntity<Map<String, String>> registerUnderwriter(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String dob = request.get("dob");
        String joiningDate = request.get("joiningDate");
        String password = request.get("password");
        
        String result = underwriterService.registerUnderwriter(
            name, LocalDate.parse(dob), LocalDate.parse(joiningDate), password
        );
        
        Map<String, String> response = new HashMap<>();
        if (result.startsWith("SUCCESS")) {
            response.put("status", "success");
            response.put("message", "Underwriter registered successfully!");
            response.put("underwriterId", result.substring(8));
        } else {
            response.put("status", "error");
            response.put("message", result);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/underwriter/search/{id}")
    public ResponseEntity<Map<String, Object>> searchUnderwriter(@PathVariable String id) {
        Underwriter uw = underwriterService.searchUnderwriter(id);
        Map<String, Object> response = new HashMap<>();
        
        if (uw != null) {
            response.put("found", true);
            response.put("underwriterId", uw.getUnderwriterId());
            response.put("name", uw.getName());
            response.put("dob", uw.getDob());
            response.put("age", underwriterService.calculateAge(uw.getDob()));
            response.put("joiningDate", uw.getJoiningDate());
        } else {
            response.put("found", false);
            response.put("message", "No Such Underwriter Exist with the Given Id.");
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/underwriter/update-password")
    public ResponseEntity<Map<String, String>> updatePassword(@RequestBody Map<String, String> request) {
        String underwriterId = request.get("underwriterId");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");
        
        String result = underwriterService.updatePassword(underwriterId, newPassword, confirmPassword);
        
        Map<String, String> response = new HashMap<>();
        if (result.startsWith("SUCCESS")) {
            response.put("status", "success");
            response.put("message", result.substring(8));
        } else {
            response.put("status", "error");
            response.put("message", result);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/underwriter/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteUnderwriter(@PathVariable String id) {
        String result = underwriterService.deleteUnderwriter(id);
        
        Map<String, String> response = new HashMap<>();
        if (result.startsWith("SUCCESS")) {
            response.put("status", "success");
            response.put("message", result.substring(8));
        } else {
            response.put("status", "error");
            response.put("message", result);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/underwriter/all")
    public ResponseEntity<List<Underwriter>> getAllUnderwriters() {
        return ResponseEntity.ok(underwriterService.getAllUnderwriters());
    }

    // ========== Policy Management ==========

    @GetMapping("/policies/all")
    public ResponseEntity<List<InsurancePolicy>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @GetMapping("/policies/pending")
    public ResponseEntity<List<InsurancePolicy>> getPendingPolicies() {
        return ResponseEntity.ok(policyService.getPendingPolicies());
    }

    @PutMapping("/policies/approve/{policyId}")
    public ResponseEntity<Map<String, String>> approvePolicy(@PathVariable String policyId) {
        boolean success = policyService.approvePolicy(policyId);
        Map<String, String> response = new HashMap<>();
        
        if (success) {
            response.put("status", "success");
            response.put("message", "Policy " + policyId + " has been approved and is now Active.");
        } else {
            response.put("status", "error");
            response.put("message", "Policy not found!");
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/policies/reject/{policyId}")
    public ResponseEntity<Map<String, String>> rejectPolicy(@PathVariable String policyId) {
        boolean success = policyService.rejectPolicy(policyId);
        Map<String, String> response = new HashMap<>();
        
        if (success) {
            response.put("status", "success");
            response.put("message", "Policy " + policyId + " has been rejected.");
        } else {
            response.put("status", "error");
            response.put("message", "Policy not found!");
        }
        return ResponseEntity.ok(response);
    }

    // Dashboard stats
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUnderwriters", underwriterService.getAllUnderwriters().size());
        stats.put("totalPolicies", policyService.getAllPolicies().size());
        stats.put("activePolicies", policyService.getAllPolicies().stream()
            .filter(p -> p.getStatus().equals("approved") || p.getStatus().equals("Active"))
            .count());
        stats.put("pendingPolicies", policyService.getPendingPolicies().size());
        stats.put("expiredPolicies", policyService.getExpiredPolicies().size());
        
        return ResponseEntity.ok(stats);
    }
}