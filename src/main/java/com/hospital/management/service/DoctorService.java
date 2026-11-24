package com.hospital.management.service;

import com.hospital.management.dto.DoctorDTO;
import com.hospital.management.entity.Doctor;
import com.hospital.management.entity.User;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DoctorDTO> getAvailableDoctors() {
        return doctorRepository.findByAvailableTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DoctorDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
        return mapToDTO(doctor);
    }

    @Transactional(readOnly = true)
    public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DoctorDTO> getDoctorsByDepartment(String department) {
        return doctorRepository.findByDepartment(department).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DoctorDTO createDoctor(DoctorDTO dto) {
        Doctor doctor = mapToEntity(dto);
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.getUserId()));
            doctor.setUser(user);
        }
        Doctor saved = doctorRepository.save(doctor);
        return mapToDTO(saved);
    }

    public DoctorDTO updateDoctor(Long id, DoctorDTO dto) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setSpecialization(dto.getSpecialization());
        existing.setQualification(dto.getQualification());
        existing.setLicenseNumber(dto.getLicenseNumber());
        existing.setDepartment(dto.getDepartment());
        existing.setConsultationFee(dto.getConsultationFee());
        existing.setAvailable(dto.isAvailable());

        Doctor updated = doctorRepository.save(existing);
        return mapToDTO(updated);
    }

    public void setAvailability(Long id, boolean available) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
        doctor.setAvailable(available);
        doctorRepository.save(doctor);
    }

    private DoctorDTO mapToDTO(Doctor doctor) {
        return DoctorDTO.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .specialization(doctor.getSpecialization())
                .qualification(doctor.getQualification())
                .licenseNumber(doctor.getLicenseNumber())
                .department(doctor.getDepartment())
                .consultationFee(doctor.getConsultationFee())
                .available(doctor.isAvailable())
                .userId(doctor.getUser() != null ? doctor.getUser().getId() : null)
                .build();
    }

    private Doctor mapToEntity(DoctorDTO dto) {
        return Doctor.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .specialization(dto.getSpecialization())
                .qualification(dto.getQualification())
                .licenseNumber(dto.getLicenseNumber())
                .department(dto.getDepartment())
                .consultationFee(dto.getConsultationFee())
                .available(dto.isAvailable())
                .build();
    }
}
