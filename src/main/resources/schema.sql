-- ============================================================
-- Hospital Management System - Complete Database Schema (MySQL)
-- ============================================================

CREATE DATABASE IF NOT EXISTS hospital_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hospital_db;

-- 1. Roles Table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- 2. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6),
    updated_at DATETIME(6)
) ENGINE=InnoDB;

-- 3. User Roles Mapping Table (RBAC)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Patients Table
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(10),
    address VARCHAR(255),
    blood_group VARCHAR(5),
    emergency_contact VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    medical_history TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    registration_date DATETIME(6),
    updated_at DATETIME(6),
    INDEX idx_patient_name (first_name, last_name),
    INDEX idx_patient_email (email)
) ENGINE=InnoDB;

-- 5. Doctors Table
CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    specialization VARCHAR(100) NOT NULL,
    qualification VARCHAR(100),
    license_number VARCHAR(50) NOT NULL UNIQUE,
    department VARCHAR(100),
    consultation_fee DOUBLE PRECISION DEFAULT 0.0,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    user_id BIGINT UNIQUE,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_doctor_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL,
    INDEX idx_doctor_specialization (specialization),
    INDEX idx_doctor_department (department)
) ENGINE=InnoDB;

-- 6. Appointments Table
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason TEXT,
    notes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE,
    CONSTRAINT fk_appt_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id) ON DELETE CASCADE,
    INDEX idx_appt_date (appointment_date),
    INDEX idx_appt_status (status)
) ENGINE=InnoDB;

-- 7. Medicines / Pharmacy Inventory Table
CREATE TABLE IF NOT EXISTS medicines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    generic_name VARCHAR(100),
    manufacturer VARCHAR(100),
    category VARCHAR(50),
    price DOUBLE PRECISION NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    reorder_level INT NOT NULL DEFAULT 10,
    unit VARCHAR(30),
    expiry_date DATE,
    batch_number VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    INDEX idx_med_name (name),
    INDEX idx_med_category (category),
    INDEX idx_med_stock (stock_quantity)
) ENGINE=InnoDB;

-- 8. Prescriptions Table
CREATE TABLE IF NOT EXISTS prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    diagnosis TEXT,
    notes TEXT,
    prescription_date DATE,
    created_at DATETIME(6),
    CONSTRAINT fk_presc_patient FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE,
    CONSTRAINT fk_presc_doctor FOREIGN KEY (doctor_id) REFERENCES doctors (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 9. Prescription Items Table
CREATE TABLE IF NOT EXISTS prescription_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    medicine_id BIGINT,
    medicine_name VARCHAR(100) NOT NULL,
    dosage VARCHAR(50),
    frequency VARCHAR(50),
    duration_days INT,
    instructions VARCHAR(255),
    CONSTRAINT fk_item_prescription FOREIGN KEY (prescription_id) REFERENCES prescriptions (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_medicine FOREIGN KEY (medicine_id) REFERENCES medicines (id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 10. Bills Table
CREATE TABLE IF NOT EXISTS bills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    appointment_id BIGINT,
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    paid_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    discount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    notes VARCHAR(255),
    billing_date DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_bill_patient FOREIGN KEY (patient_id) REFERENCES patients (id) ON DELETE CASCADE,
    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (id) ON DELETE SET NULL,
    INDEX idx_bill_status (status)
) ENGINE=InnoDB;

-- 11. Bill Items Table
CREATE TABLE IF NOT EXISTS bill_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id BIGINT NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_item_bill FOREIGN KEY (bill_id) REFERENCES bills (id) ON DELETE CASCADE
) ENGINE=InnoDB;
