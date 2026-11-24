package com.hospital.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDTO {

    private Long id;

    @NotNull
    private Long patientId;

    @NotNull
    private Long doctorId;

    @NotNull
    private LocalDate appointmentDate;

    @NotNull
    private LocalTime appointmentTime;

    private String reason;

    private String notes;

    private String status;

    private String patientName;

    private String doctorName;
}
