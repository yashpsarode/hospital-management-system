package com.hospital.management.service;

import com.hospital.management.dto.PatientDTO;
import com.hospital.management.entity.Patient;
import com.hospital.management.enums.Gender;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
        return mapToDTO(patient);
    }

    public PatientDTO createPatient(PatientDTO dto) {
        Patient patient = mapToEntity(dto);
        patient.setActive(true);
        Patient saved = patientRepository.save(patient);
        return mapToDTO(saved);
    }

    public PatientDTO updatePatient(Long id, PatientDTO dto) {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setDateOfBirth(dto.getDateOfBirth());
        existing.setGender(Gender.valueOf(dto.getGender()));
        existing.setAddress(dto.getAddress());
        existing.setBloodGroup(dto.getBloodGroup());
        existing.setEmergencyContact(dto.getEmergencyContact());
        existing.setMedicalHistory(dto.getMedicalHistory());

        Patient updated = patientRepository.save(existing);
        return mapToDTO(updated);
    }

    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
        patient.setActive(false);
        patientRepository.save(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> searchPatients(String query) {
        return patientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PatientDTO mapToDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setEmail(patient.getEmail());
        dto.setPhone(patient.getPhone());
        dto.setDateOfBirth(patient.getDateOfBirth());
        dto.setGender(patient.getGender() != null ? patient.getGender().name() : null);
        dto.setAddress(patient.getAddress());
        dto.setBloodGroup(patient.getBloodGroup());
        dto.setEmergencyContact(patient.getEmergencyContact());
        dto.setMedicalHistory(patient.getMedicalHistory());
        return dto;
    }

    private Patient mapToEntity(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender() != null ? Gender.valueOf(dto.getGender()) : null);
        patient.setAddress(dto.getAddress());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setEmergencyContact(dto.getEmergencyContact());
        patient.setMedicalHistory(dto.getMedicalHistory());
        return patient;
    }
}
