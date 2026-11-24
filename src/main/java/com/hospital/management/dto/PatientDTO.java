package com.hospital.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {

    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String email;

    private String phone;

    private LocalDate dateOfBirth;

    private String gender;

    private String address;

    private String bloodGroup;

    private String emergencyContact;

    private String emergencyContactPhone;

    private String medicalHistory;
}
