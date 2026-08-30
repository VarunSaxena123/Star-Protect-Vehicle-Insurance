-- Drop tables if they exist (for clean startup)
DROP TABLE IF EXISTS insurance_policy;
DROP TABLE IF EXISTS underwriter;

-- Create Underwriter Table with auto-generated created_date
CREATE TABLE underwriter (
    underwriter_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    dob DATE NOT NULL,
    joining_date DATE NOT NULL,
    password VARCHAR(50) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Insurance Policy Table with all required columns
CREATE TABLE insurance_policy (
    policy_id VARCHAR(10) PRIMARY KEY,
    vehicle_no VARCHAR(10) NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL,
    customer_name VARCHAR(50) NOT NULL,
    engine_no VARCHAR(30) NOT NULL,
    chassis_no VARCHAR(30) NOT NULL,
    phone_no VARCHAR(10) NOT NULL,
    premium_amount DECIMAL(10,2) NOT NULL,
    insurance_type VARCHAR(20) NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    underwriter_id VARCHAR(10) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    vehicle_age INT DEFAULT 0,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    renewed_from VARCHAR(10) DEFAULT 'admin',
    FOREIGN KEY (underwriter_id) REFERENCES underwriter(underwriter_id) ON DELETE CASCADE
);

-- Insert Sample Underwriters (at least 2 per requirement)
INSERT INTO underwriter (underwriter_id, name, dob, joining_date, password) VALUES 
('UW101', 'Chandna', '2002-04-12', CURRENT_DATE, 'Pass@123'),
('UW102', 'Neha Anna', '2003-09-22', CURRENT_DATE, 'Pass@123'),
('UW103', 'Prabhjot', '2002-03-30', CURRENT_DATE, 'Pass@123');

-- Insert Sample Insurance Policies (at least 2 per underwriter)
-- For UW101 - 3 policies
INSERT INTO insurance_policy (policy_id, vehicle_no, vehicle_type, customer_name, engine_no, chassis_no, 
    phone_no, premium_amount, insurance_type, from_date, to_date, underwriter_id, status, vehicle_age, renewed_from) VALUES 
('POL101', 'KA01AB1234', '4-wheeler', 'Rahul Mehta', 'ENG001', 'CHS001', '9876543210', 9100, 'Full Insurance', CURRENT_DATE, DATEADD(YEAR, 1, CURRENT_DATE), 'UW101', 'approved', 2, 'admin'),
('POL102', 'KA01CD5678', '2-wheeler', 'Priya Sharma', 'ENG002', 'CHS002', '9876543211', 3500, 'Third Party', CURRENT_DATE, DATEADD(YEAR, 1, CURRENT_DATE), 'UW101', 'approved', 1, 'admin'),
('POL103', 'KA01EF9012', '4-wheeler', 'Amit Singh', 'ENG003', 'CHS003', '9876543212', 9100, 'Full Insurance', CURRENT_DATE, DATEADD(YEAR, 1, CURRENT_DATE), 'UW101', 'pending', 3, 'admin');

-- For UW102 - 2 policies
INSERT INTO insurance_policy (policy_id, vehicle_no, vehicle_type, customer_name, engine_no, chassis_no, 
    phone_no, premium_amount, insurance_type, from_date, to_date, underwriter_id, status, vehicle_age, renewed_from) VALUES 
('POL104', 'KA02GH3456', '2-wheeler', 'Sneha Reddy', 'ENG004', 'CHS004', '9876543213', 3500, 'Third Party', CURRENT_DATE, DATEADD(YEAR, 1, CURRENT_DATE), 'UW102', 'approved', 1, 'admin'),
('POL105', 'KA02IJ7890', '4-wheeler', 'Vikram Patil', 'ENG005', 'CHS005', '9876543214', 9100, 'Full Insurance', CURRENT_DATE, DATEADD(YEAR, 1, CURRENT_DATE), 'UW102', 'approved', 2, 'admin');

-- For UW103 - 2 policies (with one pending)
INSERT INTO insurance_policy (policy_id, vehicle_no, vehicle_type, customer_name, engine_no, chassis_no, 
    phone_no, premium_amount, insurance_type, from_date, to_date, underwriter_id, status, vehicle_age, renewed_from) VALUES 
('POL106', 'KA03KL1234', '4-wheeler', 'Deepak Joshi', 'ENG006', 'CHS006', '9876543215', 9100, 'Full Insurance', CURRENT_DATE, DATEADD(YEAR, 1, CURRENT_DATE), 'UW103', 'approved', 1, 'admin'),
('POL107', 'KA03MN5678', '2-wheeler', 'Kavita Nair', 'ENG007', 'CHS007', '9876543216', 3500, 'Third Party', CURRENT_DATE, DATEADD(YEAR, 1, CURRENT_DATE), 'UW103', 'pending', 1, 'admin');