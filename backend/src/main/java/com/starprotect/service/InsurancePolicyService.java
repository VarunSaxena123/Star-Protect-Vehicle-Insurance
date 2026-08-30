package com.starprotect.service;

import com.starprotect.model.InsurancePolicy;
import com.starprotect.repository.InsurancePolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class InsurancePolicyService {

    @Autowired
    private InsurancePolicyRepository policyRepository;

    // Calculate premium based on vehicle type, insurance type, and vehicle age
    public BigDecimal calculatePremium(String vehicleType, String insuranceType, int vehicleAge) {
        BigDecimal basePremium = vehicleType.equals("4-wheeler") ? new BigDecimal("6500") : new BigDecimal("2500");
        BigDecimal addOns = BigDecimal.ZERO;
        
        if (insuranceType.equals("Full Insurance")) {
            addOns = addOns.add(basePremium.multiply(new BigDecimal("0.4")));
        } else {
            addOns = addOns.add(basePremium.multiply(new BigDecimal("0.2")));
        }
        
        if (vehicleAge > 5) {
            addOns = addOns.add(new BigDecimal("500"));
        }
        if (vehicleAge > 10) {
            addOns = addOns.add(new BigDecimal("800"));
        }
        
        return basePremium.add(addOns);
    }

    // Calculate To Date as From Date + selected years
    public LocalDate calculateToDate(LocalDate fromDate, int years) {
        return fromDate.plusYears(years);
    }

    // Create new policy (submitted for admin approval)
    public InsurancePolicy createPolicy(InsurancePolicy policy) {
        String policyId = policyRepository.getNextPolicyId();
        policy.setPolicyId(policyId);
        policy.setStatus("pending");
        policyRepository.save(policy);
        return policy;
    }

    // Get policy by ID
    public InsurancePolicy getPolicyById(String policyId) {
        return policyRepository.findById(policyId);
    }

    // Get policy by ID and Underwriter ID (with ownership check)
    public InsurancePolicy getPolicyByIdAndUnderwriter(String policyId, String underwriterId) {
        return policyRepository.findByIdAndUnderwriterId(policyId, underwriterId);
    }

    // Get all policies for an underwriter
    public List<InsurancePolicy> getPoliciesByUnderwriter(String underwriterId) {
        return policyRepository.findByUnderwriterId(underwriterId);
    }

    // Get all policies (admin)
    public List<InsurancePolicy> getAllPolicies() {
        return policyRepository.findAll();
    }

    // Get pending policies for admin approval
    public List<InsurancePolicy> getPendingPolicies() {
        return policyRepository.findPendingPolicies();
    }

    // Approve policy
    public boolean approvePolicy(String policyId) {
        int result = policyRepository.updateStatus(policyId, "approved");
        return result > 0;
    }

    // Reject policy
    public boolean rejectPolicy(String policyId) {
        int result = policyRepository.updateStatus(policyId, "rejected");
        return result > 0;
    }

    // Change policy type (Full Insurance to Third Party only) - Requires Admin Approval
    public String changePolicyType(String policyId, String underwriterId) {
        InsurancePolicy policy = policyRepository.findByIdAndUnderwriterId(policyId, underwriterId);
        
        if (policy == null) {
            return "No such policy exists, please try again.";
        }
        
        if (!policy.getUnderwriterId().equals(underwriterId)) {
            return "You don't have permission to modify this policy.";
        }
        
        if (policy.getInsuranceType().equals("Third Party")) {
            return "There's no provision to update the policy type from Third Party to Full Insurance.";
        }
        
        // Update to Third Party with pending status for admin approval
        BigDecimal newPremium = calculatePremium(policy.getVehicleType(), "Third Party", policy.getVehicleAge());
        policyRepository.updatePolicyType(policyId, "Third Party", newPremium);
        return "SUCCESS:Policy type update requested to Third Party! New Premium: ₹" + newPremium + " (Pending Admin Approval)";
    }

    // Renew policy - Requires Admin Approval
    public String renewPolicy(String policyId, String underwriterId, BigDecimal newPremium, int years) {
        InsurancePolicy policy = policyRepository.findByIdAndUnderwriterId(policyId, underwriterId);
        
        if (policy == null) {
            return "No such policy exists, please try again.";
        }
        
        if (!policy.getUnderwriterId().equals(underwriterId)) {
            return "You don't have permission to renew this policy.";
        }
        
        LocalDate currentDate = LocalDate.now();
        LocalDate newFromDate;
        
        if (currentDate.isAfter(policy.getToDate())) {
            // Policy is expired, renewal from today
            newFromDate = currentDate;
        } else {
            // Policy not expired, renewal from next day after to_date
            newFromDate = policy.getToDate().plusDays(1);
        }
        
        LocalDate newToDate = calculateToDate(newFromDate, years);
        
        // Store the original policy ID in renewed_from field
        policyRepository.renewPolicy(policyId, newFromDate, newToDate, newPremium, policyId);
        return "SUCCESS:Renewal requested successfully! New dates: " + newFromDate + " to " + newToDate + " (Pending Admin Approval)";
    }

    // Close policy
    public String closePolicy(String policyId, String underwriterId) {
        InsurancePolicy policy = policyRepository.findByIdAndUnderwriterId(policyId, underwriterId);
        
        if (policy == null) {
            return "No such policy exists.";
        }
        
        if (!policy.getUnderwriterId().equals(underwriterId)) {
            return "You don't have permission to close this policy.";
        }
        
        if (policy.getStatus().equals("closed")) {
            return "This policy is already closed.";
        }
        
        policyRepository.closePolicy(policyId);
        return "SUCCESS:Policy has been closed successfully.";
    }

    // Get expired policies
    public List<InsurancePolicy> getExpiredPolicies() {
        return policyRepository.findExpiredPolicies();
    }

    // Get policies by underwriter with details (for admin view)
    public List<InsurancePolicy> getPoliciesByUnderwriterWithDetails(String underwriterId) {
        return policyRepository.findPoliciesWithUnderwriterDetails(underwriterId);
    }
}