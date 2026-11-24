package com.hospital.management.service;

import com.hospital.management.dto.PrescriptionDTO;
import com.hospital.management.dto.PrescriptionItemDTO;
import com.hospital.management.entity.Doctor;
import com.hospital.management.entity.Medicine;
import com.hospital.management.entity.Patient;
import com.hospital.management.entity.Prescription;
import com.hospital.management.entity.PrescriptionItem;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.MedicineRepository;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MedicineRepository medicineRepository;

    @Transactional(readOnly = true)
    public List<PrescriptionDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PrescriptionDTO getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription", "id", id));
        return mapToDTO(prescription);
    }

    public PrescriptionDTO createPrescription(PrescriptionDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", dto.getPatientId()));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", dto.getDoctorId()));

        Prescription prescription = mapToEntity(dto);
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<PrescriptionItem> items = new ArrayList<>();
            for (PrescriptionItemDTO itemDTO : dto.getItems()) {
                PrescriptionItem item = new PrescriptionItem();
                item.setPrescription(prescription);
                item.setMedicineName(itemDTO.getMedicineName());
                item.setDosage(itemDTO.getDosage());
                item.setFrequency(itemDTO.getFrequency());
                item.setDurationDays(itemDTO.getDurationDays());
                item.setInstructions(itemDTO.getInstructions());

                if (itemDTO.getMedicineId() != null) {
                    Medicine medicine = medicineRepository.findById(itemDTO.getMedicineId())
                            .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", itemDTO.getMedicineId()));
                    item.setMedicine(medicine);
                }

                items.add(item);
            }
            prescription.setItems(items);
        }

        Prescription saved = prescriptionRepository.save(prescription);
        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionDTO> getPrescriptionsByPatient(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PrescriptionDTO> getPrescriptionsByDoctor(Long doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PrescriptionDTO mapToDTO(Prescription prescription) {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setId(prescription.getId());
        dto.setPatientId(prescription.getPatient().getId());
        dto.setPatientName(prescription.getPatient().getFirstName() + " " + prescription.getPatient().getLastName());
        dto.setDoctorId(prescription.getDoctor().getId());
        dto.setDoctorName(prescription.getDoctor().getFirstName() + " " + prescription.getDoctor().getLastName());
        dto.setDiagnosis(prescription.getDiagnosis());
        dto.setNotes(prescription.getNotes());
        dto.setPrescriptionDate(prescription.getPrescriptionDate());

        if (prescription.getItems() != null) {
            List<PrescriptionItemDTO> itemDTOs = prescription.getItems().stream()
                    .map(this::mapItemToDTO)
                    .collect(Collectors.toList());
            dto.setItems(itemDTOs);
        }

        return dto;
    }

    private PrescriptionItemDTO mapItemToDTO(PrescriptionItem item) {
        PrescriptionItemDTO dto = new PrescriptionItemDTO();
        dto.setId(item.getId());
        dto.setMedicineName(item.getMedicineName());
        dto.setDosage(item.getDosage());
        dto.setFrequency(item.getFrequency());
        dto.setDurationDays(item.getDurationDays());
        dto.setInstructions(item.getInstructions());

        if (item.getMedicine() != null) {
            dto.setMedicineId(item.getMedicine().getId());
        }

        return dto;
    }

    private Prescription mapToEntity(PrescriptionDTO dto) {
        Prescription prescription = new Prescription();
        prescription.setDiagnosis(dto.getDiagnosis());
        prescription.setNotes(dto.getNotes());
        prescription.setPrescriptionDate(dto.getPrescriptionDate());
        return prescription;
    }
}
