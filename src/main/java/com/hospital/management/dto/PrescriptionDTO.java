package com.hospital.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionDTO {

    private Long id;

    @NotNull
    private Long patientId;

    @NotNull
    private Long doctorId;

    private String diagnosis;

    private String notes;

    private LocalDate prescriptionDate;

    private String patientName;

    private String doctorName;

    private List<PrescriptionItemDTO> items;
}
