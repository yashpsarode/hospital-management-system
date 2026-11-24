package com.hospital.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillDTO {

    private Long id;

    @NotNull
    private Long patientId;

    private Long appointmentId;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal discount;

    private String status;

    private String paymentMethod;

    private String notes;

    private String patientName;

    private LocalDateTime billingDate;

    private List<BillItemDTO> items;
}
