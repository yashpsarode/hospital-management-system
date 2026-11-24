package com.hospital.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {

    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String email;

    private String phone;

    @NotBlank
    private String specialization;

    private String qualification;

    @NotBlank
    private String licenseNumber;

    private String department;

    private Double consultationFee;

    private boolean available;

    private Long userId;
}
