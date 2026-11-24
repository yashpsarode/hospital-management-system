package com.hospital.management.controller;

import com.hospital.management.dto.BillDTO;
import com.hospital.management.service.BillingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@Tag(name = "Billing Management", description = "Endpoints for managing bills and payments")
public class BillingController {

    private final BillingService billingService;

    @GetMapping
    @Operation(summary = "Get all bills", description = "Retrieve a list of all bills")
    public ResponseEntity<List<BillDTO>> getAllBills() {
        List<BillDTO> bills = billingService.getAllBills();
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bill by ID", description = "Retrieve a specific bill by its ID")
    public ResponseEntity<BillDTO> getBillById(@PathVariable Long id) {
        BillDTO bill = billingService.getBillById(id);
        return ResponseEntity.ok(bill);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Operation(summary = "Create a new bill", description = "Generate a new bill for a patient")
    public ResponseEntity<BillDTO> createBill(@Valid @RequestBody BillDTO billDTO) {
        BillDTO createdBill = billingService.createBill(billDTO);
        return new ResponseEntity<>(createdBill, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/payment")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Operation(summary = "Update payment", description = "Record a payment against an existing bill")
    public ResponseEntity<BillDTO> updatePayment(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam String paymentMethod) {
        BillDTO updatedBill = billingService.updatePayment(id, amount, paymentMethod);
        return ResponseEntity.ok(updatedBill);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get bills by patient", description = "Retrieve all bills for a specific patient")
    public ResponseEntity<List<BillDTO>> getBillsByPatient(@PathVariable Long patientId) {
        List<BillDTO> bills = billingService.getBillsByPatient(patientId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get bills by status", description = "Retrieve all bills with a specific payment status")
    public ResponseEntity<List<BillDTO>> getBillsByStatus(@PathVariable String status) {
        List<BillDTO> bills = billingService.getBillsByStatus(status);
        return ResponseEntity.ok(bills);
    }
}
