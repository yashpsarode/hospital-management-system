package com.hospital.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillItemDTO {

    private Long id;

    private String description;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal amount;
}
