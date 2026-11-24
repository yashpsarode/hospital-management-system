package com.hospital.management.service;

import com.hospital.management.dto.AppointmentDTO;
import com.hospital.management.entity.Appointment;
import com.hospital.management.entity.Doctor;
import com.hospital.management.entity.Patient;
import com.hospital.management.enums.AppointmentStatus;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AppointmentDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        return mapToDTO(appointment);
    }

    public AppointmentDTO createAppointment(AppointmentDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", dto.getPatientId()));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", dto.getDoctorId()));

        Appointment appointment = mapToEntity(dto);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment saved = appointmentRepository.save(appointment);
        return mapToDTO(saved);
    }

    public AppointmentDTO updateAppointment(Long id, AppointmentDTO dto) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        if (dto.getPatientId() != null) {
            Patient patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", dto.getPatientId()));
            existing.setPatient(patient);
        }

        if (dto.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", dto.getDoctorId()));
            existing.setDoctor(doctor);
        }

        existing.setAppointmentDate(dto.getAppointmentDate());
        existing.setAppointmentTime(dto.getAppointmentTime());
        existing.setReason(dto.getReason());
        existing.setNotes(dto.getNotes());

        if (dto.getStatus() != null) {
            existing.setStatus(AppointmentStatus.valueOf(dto.getStatus()));
        }

        Appointment updated = appointmentRepository.save(existing);
        return mapToDTO(updated);
    }

    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private AppointmentDTO mapToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setPatientId(appointment.getPatient().getId());
        dto.setPatientName(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setDoctorName(appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setStatus(appointment.getStatus() != null ? appointment.getStatus().name() : null);
        dto.setReason(appointment.getReason());
        dto.setNotes(appointment.getNotes());
        return dto;
    }

    private Appointment mapToEntity(AppointmentDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setAppointmentTime(dto.getAppointmentTime());
        appointment.setReason(dto.getReason());
        appointment.setNotes(dto.getNotes());

        if (dto.getStatus() != null) {
            appointment.setStatus(AppointmentStatus.valueOf(dto.getStatus()));
        }

        return appointment;
    }
}
