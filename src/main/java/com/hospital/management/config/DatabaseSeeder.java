package com.hospital.management.config;

import com.hospital.management.entity.*;
import com.hospital.management.enums.*;
import com.hospital.management.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final MedicineRepository medicineRepository;
    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting Hospital Management System database initialization...");

        // 1. Seed Roles
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                roleRepository.save(Role.builder().name(roleName).build());
                log.info("Seeded role: {}", roleName);
            }
        }

        // 2. Seed Default Users if none exist
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow();
            Role doctorRole = roleRepository.findByName(RoleName.ROLE_DOCTOR).orElseThrow();
            Role pharmacistRole = roleRepository.findByName(RoleName.ROLE_PHARMACIST).orElseThrow();
            Role receptionistRole = roleRepository.findByName(RoleName.ROLE_RECEPTIONIST).orElseThrow();

            // Admin
            User admin = User.builder()
                    .username("admin")
                    .email("admin@hospital.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Hospital Administrator")
                    .enabled(true)
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin);

            // Doctor User
            User docUser = User.builder()
                    .username("dr.john")
                    .email("dr.john@hospital.com")
                    .password(passwordEncoder.encode("doctor123"))
                    .fullName("Dr. John Smith")
                    .enabled(true)
                    .roles(Set.of(doctorRole))
                    .build();
            userRepository.save(docUser);

            // Pharmacist User
            User pharmUser = User.builder()
                    .username("pharmacist")
                    .email("pharmacy@hospital.com")
                    .password(passwordEncoder.encode("pharmacy123"))
                    .fullName("Alex Rivera")
                    .enabled(true)
                    .roles(Set.of(pharmacistRole))
                    .build();
            userRepository.save(pharmUser);

            // Receptionist User
            User recepUser = User.builder()
                    .username("receptionist")
                    .email("reception@hospital.com")
                    .password(passwordEncoder.encode("reception123"))
                    .fullName("Sarah Connor")
                    .enabled(true)
                    .roles(Set.of(receptionistRole))
                    .build();
            userRepository.save(recepUser);

            // 3. Seed Doctor profile
            Doctor doctor = Doctor.builder()
                    .firstName("John")
                    .lastName("Smith")
                    .email("dr.john@hospital.com")
                    .phone("+1-555-0100")
                    .specialization("Cardiology")
                    .qualification("MD, FACC")
                    .licenseNumber("MED-LIC-2024-001")
                    .department("Cardiovascular Center")
                    .consultationFee(150.00)
                    .available(true)
                    .user(docUser)
                    .build();
            doctorRepository.save(doctor);

            Doctor doctor2 = Doctor.builder()
                    .firstName("Emily")
                    .lastName("Wong")
                    .email("dr.wong@hospital.com")
                    .phone("+1-555-0101")
                    .specialization("Pediatrics")
                    .qualification("MBBS, MD (Pediatrics)")
                    .licenseNumber("MED-LIC-2024-002")
                    .department("Child Health")
                    .consultationFee(120.00)
                    .available(true)
                    .build();
            doctorRepository.save(doctor2);

            // 4. Seed Patients
            Patient patient1 = Patient.builder()
                    .firstName("Robert")
                    .lastName("Miller")
                    .email("robert.miller@gmail.com")
                    .phone("+1-555-0201")
                    .dateOfBirth(LocalDate.of(1985, 5, 12))
                    .gender(Gender.MALE)
                    .address("123 Maple Street, New York, NY")
                    .bloodGroup("O+")
                    .emergencyContact("Jane Miller")
                    .emergencyContactPhone("+1-555-0202")
                    .medicalHistory("Hypertension diagnosed 2021. Penicillin allergy.")
                    .active(true)
                    .build();
            patientRepository.save(patient1);

            Patient patient2 = Patient.builder()
                    .firstName("Sophia")
                    .lastName("Davis")
                    .email("sophia.davis@gmail.com")
                    .phone("+1-555-0203")
                    .dateOfBirth(LocalDate.of(1992, 11, 28))
                    .gender(Gender.FEMALE)
                    .address("456 Oak Avenue, Brooklyn, NY")
                    .bloodGroup("A+")
                    .emergencyContact("Mark Davis")
                    .emergencyContactPhone("+1-555-0204")
                    .medicalHistory("Mild asthma. No known drug allergies.")
                    .active(true)
                    .build();
            patientRepository.save(patient2);

            // 5. Seed Pharmacy Inventory
            List<Medicine> medicines = Arrays.asList(
                    Medicine.builder()
                            .name("Amoxicillin 500mg")
                            .genericName("Amoxicillin")
                            .manufacturer("Pfizer Inc.")
                            .category("Antibiotics")
                            .price(18.50)
                            .stockQuantity(120)
                            .reorderLevel(25)
                            .unit("Capsules")
                            .expiryDate(LocalDate.now().plusMonths(18))
                            .batchNumber("AMX-2024-B1")
                            .active(true)
                            .build(),
                    Medicine.builder()
                            .name("Lisinopril 10mg")
                            .genericName("Lisinopril")
                            .manufacturer("Novartis")
                            .category("Antihypertensive")
                            .price(12.75)
                            .stockQuantity(85)
                            .reorderLevel(20)
                            .unit("Tablets")
                            .expiryDate(LocalDate.now().plusMonths(24))
                            .batchNumber("LIS-2024-A3")
                            .active(true)
                            .build(),
                    Medicine.builder()
                            .name("Paracetamol 650mg")
                            .genericName("Acetaminophen")
                            .manufacturer("GSK")
                            .category("Analgesics / Antipyretic")
                            .price(6.00)
                            .stockQuantity(250)
                            .reorderLevel(30)
                            .unit("Tablets")
                            .expiryDate(LocalDate.now().plusMonths(30))
                            .batchNumber("PCM-2024-C9")
                            .active(true)
                            .build(),
                    Medicine.builder()
                            .name("Metformin 500mg")
                            .genericName("Metformin HCl")
                            .manufacturer("Merck")
                            .category("Antidiabetic")
                            .price(15.20)
                            .stockQuantity(8) // Low stock! (threshold <= 15)
                            .reorderLevel(15)
                            .unit("Tablets")
                            .expiryDate(LocalDate.now().plusMonths(12))
                            .batchNumber("MET-2023-F2")
                            .active(true)
                            .build()
            );
            medicineRepository.saveAll(medicines);

            // 6. Seed Appointment
            Appointment appointment = Appointment.builder()
                    .patient(patient1)
                    .doctor(doctor)
                    .appointmentDate(LocalDate.now().plusDays(1))
                    .appointmentTime(LocalTime.of(10, 30))
                    .reason("Routine cardiology checkup and ECG evaluation")
                    .notes("Patient reported occasional mild chest tightness after exercise.")
                    .status(AppointmentStatus.SCHEDULED)
                    .build();
            appointmentRepository.save(appointment);

            // 7. Seed Initial Bill
            BillItem item1 = BillItem.builder()
                    .description("Doctor Consultation Fee - Cardiology")
                    .quantity(1)
                    .unitPrice(BigDecimal.valueOf(150.00))
                    .amount(BigDecimal.valueOf(150.00))
                    .build();

            BillItem item2 = BillItem.builder()
                    .description("Electrocardiogram (ECG) Diagnostic")
                    .quantity(1)
                    .unitPrice(BigDecimal.valueOf(75.00))
                    .amount(BigDecimal.valueOf(75.00))
                    .build();

            Bill bill = Bill.builder()
                    .patient(patient1)
                    .appointment(appointment)
                    .totalAmount(BigDecimal.valueOf(225.00))
                    .paidAmount(BigDecimal.valueOf(225.00))
                    .discount(BigDecimal.ZERO)
                    .status(BillStatus.PAID)
                    .paymentMethod("CREDIT_CARD")
                    .notes("Paid in full at front desk.")
                    .items(Arrays.asList(item1, item2))
                    .build();

            item1.setBill(bill);
            item2.setBill(bill);
            billRepository.save(bill);

            log.info("Hospital Management System successfully seeded with demo users, doctors, patients, inventory, appointment, and billing records.");
        }
    }
}
