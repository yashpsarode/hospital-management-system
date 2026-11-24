package com.hospital.management.entity;

import com.hospital.management.enums.BillStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalAmount;

    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BillStatus status = BillStatus.PENDING;

    private String paymentMethod;

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = true)
    private Appointment appointment;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BillItem> items = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime billingDate;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
