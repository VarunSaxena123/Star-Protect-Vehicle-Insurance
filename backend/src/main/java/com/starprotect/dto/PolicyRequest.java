package com.starprotect.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PolicyRequest {
    private String policyId;
    private String vehicleNo;
    private String vehicleType;
    private String customerName;
    private String engineNo;
    private String chassisNo;
    private String phoneNo;
    private BigDecimal premiumAmount;
    private String insuranceType;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String underwriterId;
    private String status;
    private Integer vehicleAge;
    private Integer tenureYears;

    // Getters and Setters
    public String getPolicyId() { return policyId; }
    public void setPolicyId(String policyId) { this.policyId = policyId; }
    
    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }
    
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getEngineNo() { return engineNo; }
    public void setEngineNo(String engineNo) { this.engineNo = engineNo; }
    
    public String getChassisNo() { return chassisNo; }
    public void setChassisNo(String chassisNo) { this.chassisNo = chassisNo; }
    
    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }
    
    public BigDecimal getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(BigDecimal premiumAmount) { this.premiumAmount = premiumAmount; }
    
    public String getInsuranceType() { return insuranceType; }
    public void setInsuranceType(String insuranceType) { this.insuranceType = insuranceType; }
    
    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }
    
    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }
    
    public String getUnderwriterId() { return underwriterId; }
    public void setUnderwriterId(String underwriterId) { this.underwriterId = underwriterId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getVehicleAge() { return vehicleAge; }
    public void setVehicleAge(Integer vehicleAge) { this.vehicleAge = vehicleAge; }
    
    public Integer getTenureYears() { return tenureYears; }
    public void setTenureYears(Integer tenureYears) { this.tenureYears = tenureYears; }
}