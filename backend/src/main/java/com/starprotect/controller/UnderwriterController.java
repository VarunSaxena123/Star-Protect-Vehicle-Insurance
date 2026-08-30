package com.starprotect.controller;

import com.starprotect.dto.PolicyRequest;
import com.starprotect.model.InsurancePolicy;
import com.starprotect.service.InsurancePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/underwriter")
@CrossOrigin(origins = "*")
public class UnderwriterController {

    @Autowired
    private InsurancePolicyService policyService;

    // Create new policy
    @PostMapping("/policy/create")
    public ResponseEntity<Map<String, Object>> createPolicy(@RequestBody PolicyRequest request) {
        InsurancePolicy policy = new InsurancePolicy();
        policy.setVehicleNo(request.getVehicleNo());
        policy.setVehicleType(request.getVehicleType());
        policy.setCustomerName(request.getCustomerName());
        policy.setEngineNo(request.getEngineNo());
        policy.setChassisNo(request.getChassisNo());
        policy.setPhoneNo(request.getPhoneNo());
        policy.setVehicleAge(request.getVehicleAge() != null ? request.getVehicleAge() : 0);
        policy.setInsuranceType(request.getInsuranceType());
        policy.setFromDate(request.getFromDate());
        policy.setUnderwriterId(request.getUnderwriterId());
        
        int tenureYears = request.getTenureYears() != null ? request.getTenureYears() : 1;
        LocalDate toDate = policyService.calculateToDate(policy.getFromDate(), tenureYears);
        policy.setToDate(toDate);
        
        // Calculate premium
        BigDecimal premium = policyService.calculatePremium(
            policy.getVehicleType(), policy.getInsuranceType(), policy.getVehicleAge()
        );
        policy.setPremiumAmount(premium);
        
        InsurancePolicy savedPolicy = policyService.createPolicy(policy);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Policy " + savedPolicy.getPolicyId() + " submitted for admin approval!");
        response.put("policy", savedPolicy);
        
        return ResponseEntity.ok(response);
    }

    // Get all policies for logged-in underwriter
    @GetMapping("/policies/{underwriterId}")
    public ResponseEntity<List<InsurancePolicy>> getMyPolicies(@PathVariable String underwriterId) {
        return ResponseEntity.ok(policyService.getPoliciesByUnderwriter(underwriterId));
    }

    // Get single policy by ID (with ownership check)
    @GetMapping("/policy/{policyId}/{underwriterId}")
    public ResponseEntity<Map<String, Object>> getPolicyForUnderwriter(@PathVariable String policyId, @PathVariable String underwriterId) {
        InsurancePolicy policy = policyService.getPolicyByIdAndUnderwriter(policyId, underwriterId);
        Map<String, Object> response = new HashMap<>();
        
        if (policy != null) {
            response.put("found", true);
            response.put("policy", policy);
            response.put("isExpired", LocalDate.now().isAfter(policy.getToDate()));
        } else {
            response.put("found", false);
            response.put("message", "No such policy exists, please try again.");
        }
        return ResponseEntity.ok(response);
    }

    // Get single policy by ID (without ownership check - for viewing only)
    @GetMapping("/policy/{policyId}")
    public ResponseEntity<Map<String, Object>> getPolicy(@PathVariable String policyId) {
        InsurancePolicy policy = policyService.getPolicyById(policyId);
        Map<String, Object> response = new HashMap<>();
        
        if (policy != null) {
            response.put("found", true);
            response.put("policy", policy);
            response.put("isExpired", LocalDate.now().isAfter(policy.getToDate()));
        } else {
            response.put("found", false);
            response.put("message", "No such policy exists, please try again.");
        }
        return ResponseEntity.ok(response);
    }

    // Renew policy
    @PutMapping("/policy/renew")
    public ResponseEntity<Map<String, String>> renewPolicy(@RequestBody Map<String, String> request) {
        String policyId = request.get("policyId");
        String underwriterId = request.get("underwriterId");
        BigDecimal newPremium = new BigDecimal(request.get("newPremium"));
        int years = Integer.parseInt(request.getOrDefault("years", "1"));
        
        String result = policyService.renewPolicy(policyId, underwriterId, newPremium, years);
        
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

    // Change policy type
    @PutMapping("/policy/change-type")
    public ResponseEntity<Map<String, String>> changePolicyType(@RequestBody Map<String, String> request) {
        String policyId = request.get("policyId");
        String underwriterId = request.get("underwriterId");
        
        String result = policyService.changePolicyType(policyId, underwriterId);
        
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

    // Close policy
    @PutMapping("/policy/close")
    public ResponseEntity<Map<String, String>> closePolicy(@RequestBody Map<String, String> request) {
        String policyId = request.get("policyId");
        String underwriterId = request.get("underwriterId");
        
        String result = policyService.closePolicy(policyId, underwriterId);
        
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

    // Calculate premium preview
    @PostMapping("/calculate-premium")
    public ResponseEntity<Map<String, Object>> calculatePremium(@RequestBody Map<String, String> request) {
        String vehicleType = request.get("vehicleType");
        String insuranceType = request.get("insuranceType");
        int vehicleAge = Integer.parseInt(request.getOrDefault("vehicleAge", "0"));
        
        BigDecimal premium = policyService.calculatePremium(vehicleType, insuranceType, vehicleAge);
        
        Map<String, Object> response = new HashMap<>();
        response.put("premiumAmount", premium);
        response.put("formattedPremium", "₹" + premium.toPlainString());
        
        return ResponseEntity.ok(response);
    }

    // Calculate To Date based on from date and years
    @PostMapping("/calculate-to-date")
    public ResponseEntity<Map<String, Object>> calculateToDate(@RequestBody Map<String, String> request) {
        LocalDate fromDate = LocalDate.parse(request.get("fromDate"));
        int years = Integer.parseInt(request.get("years"));
        
        LocalDate toDate = policyService.calculateToDate(fromDate, years);
        
        Map<String, Object> response = new HashMap<>();
        response.put("toDate", toDate.toString());
        
        return ResponseEntity.ok(response);
    }

    // Get dashboard stats for underwriter
    @GetMapping("/dashboard/stats/{underwriterId}")
    public ResponseEntity<Map<String, Object>> getUnderwriterStats(@PathVariable String underwriterId) {
        List<InsurancePolicy> policies = policyService.getPoliciesByUnderwriter(underwriterId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPolicies", policies.size());
        stats.put("totalPremium", policies.stream()
            .map(InsurancePolicy::getPremiumAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        stats.put("activePolicies", policies.stream()
            .filter(p -> p.getStatus().equals("approved") || p.getStatus().equals("Active"))
            .count());
        stats.put("pendingPolicies", policies.stream()
            .filter(p -> p.getStatus().equals("pending"))
            .count());
        
        return ResponseEntity.ok(stats);
    }

    // Get expired policies for underwriter
    @GetMapping("/expired-policies/{underwriterId}")
    public ResponseEntity<List<InsurancePolicy>> getExpiredPolicies(@PathVariable String underwriterId) {
        List<InsurancePolicy> allPolicies = policyService.getPoliciesByUnderwriter(underwriterId);
        List<InsurancePolicy> expired = allPolicies.stream()
            .filter(p -> LocalDate.now().isAfter(p.getToDate()) && 
                   (p.getStatus().equals("approved") || p.getStatus().equals("Active")))
            .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(expired);
    }
}