package com.hospital.management.controller;

import com.hospital.management.dto.AppointmentDTO;
import com.hospital.management.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Management", description = "Endpoints for managing appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    @Operation(summary = "Get all appointments", description = "Retrieve a list of all appointments")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", description = "Retrieve a specific appointment by its ID")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long id) {
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','RECEPTIONIST')")
    @Operation(summary = "Create a new appointment", description = "Schedule a new appointment")
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO) {
        AppointmentDTO createdAppointment = appointmentService.createAppointment(appointmentDTO);
        return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','RECEPTIONIST')")
    @Operation(summary = "Update an appointment", description = "Update an existing appointment's details")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentDTO appointmentDTO) {
        AppointmentDTO updatedAppointment = appointmentService.updateAppointment(id, appointmentDTO);
        return ResponseEntity.ok(updatedAppointment);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','RECEPTIONIST')")
    @Operation(summary = "Cancel an appointment", description = "Cancel an existing appointment")
    public ResponseEntity<AppointmentDTO> cancelAppointment(@PathVariable Long id) {
        AppointmentDTO cancelledAppointment = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(cancelledAppointment);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get appointments by patient", description = "Retrieve all appointments for a specific patient")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByPatient(@PathVariable Long patientId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get appointments by doctor", description = "Retrieve all appointments for a specific doctor")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/date")
    @Operation(summary = "Get appointments by date", description = "Retrieve all appointments for a specific date")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(appointments);
    }
}
