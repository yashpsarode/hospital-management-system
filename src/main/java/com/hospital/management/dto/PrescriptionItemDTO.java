package com.hospital.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionItemDTO {

    private Long id;

    private String medicineName;

    private Long medicineId;

    private String dosage;

    private String frequency;

    private Integer durationDays;

    private String instructions;
}
