package com.hospital.management.controller;

import com.hospital.management.dto.DoctorDTO;
import com.hospital.management.service.DoctorService;
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
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Management", description = "Endpoints for managing doctors and doctor schedules")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    @Operation(summary = "Get all doctors", description = "Retrieve a list of all registered doctors")
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/available")
    @Operation(summary = "Get available doctors", description = "Retrieve doctors who are currently available")
    public ResponseEntity<List<DoctorDTO>> getAvailableDoctors() {
        return ResponseEntity.ok(doctorService.getAvailableDoctors());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get doctor by ID", description = "Retrieve details of a specific doctor")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/specialization/{specialization}")
    @Operation(summary = "Get doctors by specialization", description = "Filter doctors by their medical specialization")
    public ResponseEntity<List<DoctorDTO>> getDoctorsBySpecialization(@PathVariable String specialization) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(specialization));
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Get doctors by department", description = "Filter doctors by hospital department")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(doctorService.getDoctorsByDepartment(department));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new doctor", description = "Add a new doctor to the hospital system (Admin only)")
    public ResponseEntity<DoctorDTO> createDoctor(@Valid @RequestBody DoctorDTO doctorDTO) {
        DoctorDTO created = doctorService.createDoctor(doctorDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Update doctor details", description = "Update information of an existing doctor")
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorDTO doctorDTO) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, doctorDTO));
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Toggle doctor availability", description = "Update available status for appointments")
    public ResponseEntity<Void> setAvailability(@PathVariable Long id, @RequestParam boolean available) {
        doctorService.setAvailability(id, available);
        return ResponseEntity.noContent().build();
    }
}
