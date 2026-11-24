package com.hospital.management.entity;

import com.hospital.management.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;

    private String bloodGroup;

    private String emergencyContact;

    private String emergencyContactPhone;

    @Column(columnDefinition = "TEXT")
    private String medicalHistory;

    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    private LocalDateTime registrationDate;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
