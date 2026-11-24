package com.hospital.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicineDTO {

    private Long id;

    @NotBlank
    private String name;

    private String genericName;

    private String manufacturer;

    private String category;

    @NotNull
    private Double price;

    @NotNull
    private Integer stockQuantity;

    private Integer reorderLevel;

    private String unit;

    private LocalDate expiryDate;

    private String batchNumber;
}
