package com.hospital.management.service;

import com.hospital.management.dto.MedicineDTO;
import com.hospital.management.entity.Medicine;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PharmacyService {

    private final MedicineRepository medicineRepository;

    @Transactional(readOnly = true)
    public List<MedicineDTO> getAllMedicines() {
        return medicineRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MedicineDTO getMedicineById(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", id));
        return mapToDTO(medicine);
    }

    public MedicineDTO addMedicine(MedicineDTO dto) {
        Medicine medicine = mapToEntity(dto);
        medicine.setActive(true);
        Medicine saved = medicineRepository.save(medicine);
        return mapToDTO(saved);
    }

    public MedicineDTO updateMedicine(Long id, MedicineDTO dto) {
        Medicine existing = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", id));

        existing.setName(dto.getName());
        existing.setGenericName(dto.getGenericName());
        existing.setManufacturer(dto.getManufacturer());
        existing.setCategory(dto.getCategory());
        existing.setPrice(dto.getPrice());
        existing.setStockQuantity(dto.getStockQuantity());
        if (dto.getReorderLevel() != null) {
            existing.setReorderLevel(dto.getReorderLevel());
        }
        existing.setUnit(dto.getUnit());
        existing.setExpiryDate(dto.getExpiryDate());
        existing.setBatchNumber(dto.getBatchNumber());

        Medicine updated = medicineRepository.save(existing);
        return mapToDTO(updated);
    }

    public void deleteMedicine(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", id));
        medicine.setActive(false);
        medicineRepository.save(medicine);
    }

    public MedicineDTO updateStock(Long id, Integer quantity) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", id));
        medicine.setStockQuantity(quantity);
        Medicine updated = medicineRepository.save(medicine);
        return mapToDTO(updated);
    }

    @Transactional(readOnly = true)
    public List<MedicineDTO> getLowStockMedicines() {
        return medicineRepository.findByStockQuantityLessThanEqualAndActiveTrue(10).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MedicineDTO> searchMedicines(String query) {
        return medicineRepository.findByNameContainingIgnoreCase(query).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private MedicineDTO mapToDTO(Medicine medicine) {
        return MedicineDTO.builder()
                .id(medicine.getId())
                .name(medicine.getName())
                .genericName(medicine.getGenericName())
                .manufacturer(medicine.getManufacturer())
                .category(medicine.getCategory())
                .price(medicine.getPrice())
                .stockQuantity(medicine.getStockQuantity())
                .reorderLevel(medicine.getReorderLevel())
                .unit(medicine.getUnit())
                .expiryDate(medicine.getExpiryDate())
                .batchNumber(medicine.getBatchNumber())
                .build();
    }

    private Medicine mapToEntity(MedicineDTO dto) {
        return Medicine.builder()
                .name(dto.getName())
                .genericName(dto.getGenericName())
                .manufacturer(dto.getManufacturer())
                .category(dto.getCategory())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .reorderLevel(dto.getReorderLevel() != null ? dto.getReorderLevel() : 10)
                .unit(dto.getUnit())
                .expiryDate(dto.getExpiryDate())
                .batchNumber(dto.getBatchNumber())
                .active(true)
                .build();
    }
}
