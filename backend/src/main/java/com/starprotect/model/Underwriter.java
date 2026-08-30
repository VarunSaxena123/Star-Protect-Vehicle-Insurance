package com.starprotect.model;

import java.time.LocalDate;

public class Underwriter {
    private String underwriterId;
    private String name;
    private LocalDate dob;
    private LocalDate joiningDate;
    private String password;
    private String createdDate;

    // Constructors
    public Underwriter() {}

    public Underwriter(String underwriterId, String name, LocalDate dob, LocalDate joiningDate, String password) {
        this.underwriterId = underwriterId;
        this.name = name;
        this.dob = dob;
        this.joiningDate = joiningDate;
        this.password = password;
    }

    // Getters and Setters
    public String getUnderwriterId() { return underwriterId; }
    public void setUnderwriterId(String underwriterId) { this.underwriterId = underwriterId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    
    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}