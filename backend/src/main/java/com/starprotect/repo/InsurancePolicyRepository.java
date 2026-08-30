package com.starprotect.repository;

import com.starprotect.model.InsurancePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class InsurancePolicyRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<InsurancePolicy> policyRowMapper = new RowMapper<InsurancePolicy>() {
        @Override
        public InsurancePolicy mapRow(ResultSet rs, int rowNum) throws SQLException {
            InsurancePolicy policy = new InsurancePolicy();
            policy.setPolicyId(rs.getString("policy_id"));
            policy.setVehicleNo(rs.getString("vehicle_no"));
            policy.setVehicleType(rs.getString("vehicle_type"));
            policy.setCustomerName(rs.getString("customer_name"));
            policy.setEngineNo(rs.getString("engine_no"));
            policy.setChassisNo(rs.getString("chassis_no"));
            policy.setPhoneNo(rs.getString("phone_no"));
            policy.setPremiumAmount(rs.getBigDecimal("premium_amount"));
            policy.setInsuranceType(rs.getString("insurance_type"));
            policy.setFromDate(rs.getDate("from_date").toLocalDate());
            policy.setToDate(rs.getDate("to_date").toLocalDate());
            policy.setUnderwriterId(rs.getString("underwriter_id"));
            policy.setStatus(rs.getString("status"));
            policy.setVehicleAge(rs.getInt("vehicle_age"));
            policy.setCreatedDate(rs.getString("created_date"));
            policy.setRenewedFrom(rs.getString("renewed_from"));
            return policy;
        }
    };

    // Get next Policy ID
    public String getNextPolicyId() {
        String sql = "SELECT MAX(policy_id) FROM insurance_policy";
        String maxId = jdbcTemplate.queryForObject(sql, String.class);
        if (maxId == null) {
            return "POL101";
        }
        int num = Integer.parseInt(maxId.substring(3));
        return String.format("POL%03d", num + 1);
    }

    // Insert Policy
    public int save(InsurancePolicy policy) {
        String sql = "INSERT INTO insurance_policy (policy_id, vehicle_no, vehicle_type, customer_name, " +
                     "engine_no, chassis_no, phone_no, premium_amount, insurance_type, from_date, to_date, " +
                     "underwriter_id, status, vehicle_age) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
            policy.getPolicyId(),
            policy.getVehicleNo(),
            policy.getVehicleType(),
            policy.getCustomerName(),
            policy.getEngineNo(),
            policy.getChassisNo(),
            policy.getPhoneNo(),
            policy.getPremiumAmount(),
            policy.getInsuranceType(),
            policy.getFromDate(),
            policy.getToDate(),
            policy.getUnderwriterId(),
            policy.getStatus(),
            policy.getVehicleAge()
        );
    }

    // Find by ID
    public InsurancePolicy findById(String policyId) {
        String sql = "SELECT * FROM insurance_policy WHERE policy_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, policyRowMapper, policyId);
        } catch (Exception e) {
            return null;
        }
    }

    // Find by ID and Underwriter ID (for ownership check)
    public InsurancePolicy findByIdAndUnderwriterId(String policyId, String underwriterId) {
        String sql = "SELECT * FROM insurance_policy WHERE policy_id = ? AND underwriter_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, policyRowMapper, policyId, underwriterId);
        } catch (Exception e) {
            return null;
        }
    }

    // Find all policies by underwriter ID
    public List<InsurancePolicy> findByUnderwriterId(String underwriterId) {
        String sql = "SELECT * FROM insurance_policy WHERE underwriter_id = ? ORDER BY created_date DESC";
        return jdbcTemplate.query(sql, policyRowMapper, underwriterId);
    }

    // Find all policies (for admin)
    public List<InsurancePolicy> findAll() {
        String sql = "SELECT * FROM insurance_policy ORDER BY created_date DESC";
        return jdbcTemplate.query(sql, policyRowMapper);
    }

    // Find pending policies for approval
    public List<InsurancePolicy> findPendingPolicies() {
        String sql = "SELECT * FROM insurance_policy WHERE status = 'pending' ORDER BY created_date ASC";
        return jdbcTemplate.query(sql, policyRowMapper);
    }

    // Update policy status (approve/reject)
    public int updateStatus(String policyId, String status) {
        String sql = "UPDATE insurance_policy SET status = ? WHERE policy_id = ?";
        return jdbcTemplate.update(sql, status, policyId);
    }

    // Update policy type and premium
    public int updatePolicyType(String policyId, String insuranceType, java.math.BigDecimal premiumAmount) {
        String sql = "UPDATE insurance_policy SET insurance_type = ?, premium_amount = ?, status = 'pending' WHERE policy_id = ?";
        return jdbcTemplate.update(sql, insuranceType, premiumAmount, policyId);
    }

    // Renew policy (update dates and premium)
    public int renewPolicy(String policyId, LocalDate fromDate, LocalDate toDate, 
                           java.math.BigDecimal premiumAmount, String renewedFrom) {
        String sql = "UPDATE insurance_policy SET from_date = ?, to_date = ?, premium_amount = ?, " +
                     "status = 'pending', renewed_from = ? WHERE policy_id = ?";
        return jdbcTemplate.update(sql, fromDate, toDate, premiumAmount, renewedFrom, policyId);
    }

    // Close policy
    public int closePolicy(String policyId) {
        String sql = "UPDATE insurance_policy SET status = 'closed' WHERE policy_id = ?";
        return jdbcTemplate.update(sql, policyId);
    }

    // Find expired policies (current date > to_date)
    public List<InsurancePolicy> findExpiredPolicies() {
        String sql = "SELECT * FROM insurance_policy WHERE to_date < CURRENT_DATE AND status = 'approved'";
        return jdbcTemplate.query(sql, policyRowMapper);
    }

    // Get policies by underwriter with details for admin view
    public List<InsurancePolicy> findPoliciesWithUnderwriterDetails(String underwriterId) {
        String sql = "SELECT ip.* FROM insurance_policy ip " +
                     "WHERE ip.underwriter_id = ? ORDER BY ip.from_date DESC";
        return jdbcTemplate.query(sql, policyRowMapper, underwriterId);
    }
}