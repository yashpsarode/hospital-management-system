package com.hospital.management.controller;

import com.hospital.management.dto.PrescriptionDTO;
import com.hospital.management.service.PrescriptionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@Tag(name = "Prescription Management", description = "Endpoints for managing prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping
    @Operation(summary = "Get all prescriptions", description = "Retrieve a list of all prescriptions")
    public ResponseEntity<List<PrescriptionDTO>> getAllPrescriptions() {
        List<PrescriptionDTO> prescriptions = prescriptionService.getAllPrescriptions();
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prescription by ID", description = "Retrieve a specific prescription by its ID")
    public ResponseEntity<PrescriptionDTO> getPrescriptionById(@PathVariable Long id) {
        PrescriptionDTO prescription = prescriptionService.getPrescriptionById(id);
        return ResponseEntity.ok(prescription);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @Operation(summary = "Create a new prescription", description = "Create a new prescription for a patient")
    public ResponseEntity<PrescriptionDTO> createPrescription(@Valid @RequestBody PrescriptionDTO prescriptionDTO) {
        PrescriptionDTO createdPrescription = prescriptionService.createPrescription(prescriptionDTO);
        return new ResponseEntity<>(createdPrescription, HttpStatus.CREATED);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get prescriptions by patient", description = "Retrieve all prescriptions for a specific patient")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByPatient(@PathVariable Long patientId) {
        List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get prescriptions by doctor", description = "Retrieve all prescriptions issued by a specific doctor")
    public ResponseEntity<List<PrescriptionDTO>> getPrescriptionsByDoctor(@PathVariable Long doctorId) {
        List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByDoctor(doctorId);
        return ResponseEntity.ok(prescriptions);
    }
}
