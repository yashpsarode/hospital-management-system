package com.hospital.management.controller;

import com.hospital.management.dto.PatientDTO;
import com.hospital.management.service.PatientService;

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
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "Endpoints for managing patients")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','PHARMACIST')")
    @Operation(summary = "Get all patients", description = "Retrieve a list of all patients")
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','PHARMACIST')")
    @Operation(summary = "Get patient by ID", description = "Retrieve a specific patient by their ID")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        PatientDTO patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR')")
    @Operation(summary = "Create a new patient", description = "Register a new patient in the system")
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody PatientDTO patientDTO) {
        PatientDTO createdPatient = patientService.createPatient(patientDTO);
        return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR')")
    @Operation(summary = "Update a patient", description = "Update an existing patient's information")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientDTO patientDTO) {
        PatientDTO updatedPatient = patientService.updatePatient(id, patientDTO);
        return ResponseEntity.ok(updatedPatient);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR')")
    @Operation(summary = "Delete a patient", description = "Soft delete a patient from the system")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR','NURSE','PHARMACIST')")
    @Operation(summary = "Search patients", description = "Search patients by name, email, or phone number")
    public ResponseEntity<List<PatientDTO>> searchPatients(@RequestParam String query) {
        List<PatientDTO> patients = patientService.searchPatients(query);
        return ResponseEntity.ok(patients);
    }
}
