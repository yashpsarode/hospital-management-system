-- ============================================================
-- Hospital Management System - Initial Seed Data (MySQL)
-- ============================================================

USE hospital_db;

-- 1. Insert Roles
INSERT IGNORE INTO roles (id, name) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_DOCTOR'),
(3, 'ROLE_NURSE'),
(4, 'ROLE_PHARMACIST'),
(5, 'ROLE_RECEPTIONIST');

-- 2. Insert Default Users (Passwords encrypted with BCrypt: 'admin123', 'doctor123', etc.)
-- BCrypt for admin123: $2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2
INSERT IGNORE INTO users (id, username, email, password, full_name, enabled, created_at, updated_at) VALUES
(1, 'admin', 'admin@hospital.com', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'System Administrator', TRUE, NOW(), NOW()),
(2, 'dr.john', 'dr.john@hospital.com', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'Dr. John Smith', TRUE, NOW(), NOW()),
(3, 'dr.wong', 'dr.wong@hospital.com', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'Dr. Emily Wong', TRUE, NOW(), NOW()),
(4, 'pharmacist', 'pharmacy@hospital.com', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'Alex Rivera', TRUE, NOW(), NOW()),
(5, 'receptionist', 'reception@hospital.com', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'Sarah Connor', TRUE, NOW(), NOW());

-- 3. Assign User Roles
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin -> ROLE_ADMIN
(2, 2), -- dr.john -> ROLE_DOCTOR
(3, 2), -- dr.wong -> ROLE_DOCTOR
(4, 4), -- pharmacist -> ROLE_PHARMACIST
(5, 5); -- receptionist -> ROLE_RECEPTIONIST

-- 4. Insert Doctors
INSERT IGNORE INTO doctors (id, first_name, last_name, email, phone, specialization, qualification, license_number, department, consultation_fee, available, user_id, created_at, updated_at) VALUES
(1, 'John', 'Smith', 'dr.john@hospital.com', '+1-555-0100', 'Cardiology', 'MD, FACC', 'MED-LIC-2024-001', 'Cardiovascular Center', 150.00, TRUE, 2, NOW(), NOW()),
(2, 'Emily', 'Wong', 'dr.wong@hospital.com', '+1-555-0101', 'Pediatrics', 'MBBS, MD', 'MED-LIC-2024-002', 'Child Health Dept', 120.00, TRUE, 3, NOW(), NOW()),
(3, 'Marcus', 'Vance', 'dr.vance@hospital.com', '+1-555-0102', 'Neurology', 'MD, PhD', 'MED-LIC-2024-003', 'Neurology Clinic', 180.00, TRUE, NULL, NOW(), NOW());

-- 5. Insert Patients
INSERT IGNORE INTO patients (id, first_name, last_name, email, phone, date_of_birth, gender, address, blood_group, emergency_contact, emergency_contact_phone, medical_history, active, registration_date, updated_at) VALUES
(1, 'Robert', 'Miller', 'robert.miller@gmail.com', '+1-555-0201', '1985-05-12', 'MALE', '123 Maple Street, New York, NY', 'O+', 'Jane Miller', '+1-555-0202', 'Hypertension diagnosed 2021. Allergy to Penicillin.', TRUE, NOW(), NOW()),
(2, 'Sophia', 'Davis', 'sophia.davis@gmail.com', '+1-555-0203', '1992-11-28', 'FEMALE', '456 Oak Avenue, Brooklyn, NY', 'A+', 'Mark Davis', '+1-555-0204', 'Asthma. Uses salbutamol inhaler as needed.', TRUE, NOW(), NOW()),
(3, 'David', 'Clark', 'david.clark@yahoo.com', '+1-555-0205', '1978-03-15', 'MALE', '789 Pine Rd, Queens, NY', 'B+', 'Lisa Clark', '+1-555-0206', 'Type 2 Diabetes. On Metformin.', TRUE, NOW(), NOW());

-- 6. Insert Pharmacy Medicines Inventory
INSERT IGNORE INTO medicines (id, name, generic_name, manufacturer, category, price, stock_quantity, reorder_level, unit, expiry_date, batch_number, active, created_at, updated_at) VALUES
(1, 'Amoxicillin 500mg', 'Amoxicillin', 'Pfizer Inc.', 'Antibiotics', 18.50, 120, 25, 'Capsules', '2027-06-30', 'AMX-2024-B1', TRUE, NOW(), NOW()),
(2, 'Lisinopril 10mg', 'Lisinopril', 'Novartis', 'Antihypertensive', 12.75, 85, 20, 'Tablets', '2027-12-31', 'LIS-2024-A3', TRUE, NOW(), NOW()),
(3, 'Paracetamol 650mg', 'Acetaminophen', 'GSK', 'Analgesics', 6.00, 250, 30, 'Tablets', '2028-03-31', 'PCM-2024-C9', TRUE, NOW(), NOW()),
(4, 'Metformin 500mg', 'Metformin HCl', 'Merck', 'Antidiabetic', 15.20, 8, 20, 'Tablets', '2026-11-30', 'MET-2023-F2', TRUE, NOW(), NOW()),
(5, 'Atorvastatin 20mg', 'Atorvastatin Calcium', 'Pfizer', 'Lipid-lowering', 22.00, 95, 20, 'Tablets', '2027-08-31', 'ATV-2024-D4', TRUE, NOW(), NOW()),
(6, 'Omeprazole 20mg', 'Omeprazole', 'AstraZeneca', 'Antacid / PPI', 14.50, 6, 15, 'Capsules', '2026-12-31', 'OMP-2023-E7', TRUE, NOW(), NOW());

-- 7. Insert Appointments
INSERT IGNORE INTO appointments (id, patient_id, doctor_id, appointment_date, appointment_time, reason, notes, status, created_at, updated_at) VALUES
(1, 1, 1, CURDATE() + INTERVAL 1 DAY, '10:30:00', 'Routine cardiology consultation and ECG', 'Patient requested morning slot', 'SCHEDULED', NOW(), NOW()),
(2, 2, 2, CURDATE() + INTERVAL 2 DAY, '14:00:00', 'Pediatric vaccination and wellness review', 'Parent accompanying child', 'SCHEDULED', NOW(), NOW()),
(3, 3, 1, CURDATE() - INTERVAL 3 DAY, '11:00:00', 'Follow up on blood pressure meds', 'Blood pressure controlled at 125/82', 'COMPLETED', NOW(), NOW());

-- 8. Insert Prescriptions
INSERT IGNORE INTO prescriptions (id, patient_id, doctor_id, diagnosis, notes, prescription_date, created_at) VALUES
(1, 3, 1, 'Essential Hypertension and Hyperlipidemia', 'Review after 60 days. Continue daily exercise.', CURDATE() - INTERVAL 3 DAY, NOW());

-- 9. Insert Prescription Items
INSERT IGNORE INTO prescription_items (id, prescription_id, medicine_id, medicine_name, dosage, frequency, duration_days, instructions) VALUES
(1, 1, 2, 'Lisinopril 10mg', '10mg', 'Once daily in the morning', 30, 'Take with a glass of water after food'),
(2, 1, 5, 'Atorvastatin 20mg', '20mg', 'Once daily at bedtime', 30, 'Avoid grapefruit juice while on this medication');

-- 10. Insert Bills
INSERT IGNORE INTO bills (id, patient_id, appointment_id, total_amount, paid_amount, discount, status, payment_method, notes, billing_date, updated_at) VALUES
(1, 3, 3, 186.75, 186.75, 0.00, 'PAID', 'CREDIT_CARD', 'Paid consultation fee + pharmacy prescription', NOW() - INTERVAL 3 DAY, NOW()),
(2, 1, 1, 150.00, 0.00, 0.00, 'PENDING', NULL, 'Appointment scheduled; invoice generated', NOW(), NOW());

-- 11. Insert Bill Items
INSERT IGNORE INTO bill_items (id, bill_id, description, quantity, unit_price, amount) VALUES
(1, 1, 'Cardiology Consultation (Dr. John Smith)', 1, 150.00, 150.00),
(2, 1, 'Lisinopril 10mg (30 Tablets)', 1, 12.75, 12.75),
(3, 1, 'Atorvastatin 20mg (30 Tablets)', 1, 22.00, 22.00),
(4, 2, 'Cardiology Consultation Advance Booking', 1, 150.00, 150.00);
