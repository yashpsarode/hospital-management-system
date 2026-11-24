package com.hospital.management.controller;

import com.hospital.management.dto.MedicineDTO;
import com.hospital.management.service.PharmacyService;

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
@RequestMapping("/api/pharmacy")
@RequiredArgsConstructor
@Tag(name = "Pharmacy Management", description = "Endpoints for managing medicines and pharmacy inventory")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    @GetMapping
    @Operation(summary = "Get all medicines", description = "Retrieve a list of all medicines in the pharmacy")
    public ResponseEntity<List<MedicineDTO>> getAllMedicines() {
        List<MedicineDTO> medicines = pharmacyService.getAllMedicines();
        return ResponseEntity.ok(medicines);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medicine by ID", description = "Retrieve a specific medicine by its ID")
    public ResponseEntity<MedicineDTO> getMedicineById(@PathVariable Long id) {
        MedicineDTO medicine = pharmacyService.getMedicineById(id);
        return ResponseEntity.ok(medicine);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    @Operation(summary = "Add a new medicine", description = "Add a new medicine to the pharmacy inventory")
    public ResponseEntity<MedicineDTO> addMedicine(@Valid @RequestBody MedicineDTO medicineDTO) {
        MedicineDTO createdMedicine = pharmacyService.addMedicine(medicineDTO);
        return new ResponseEntity<>(createdMedicine, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    @Operation(summary = "Update a medicine", description = "Update an existing medicine's information")
    public ResponseEntity<MedicineDTO> updateMedicine(@PathVariable Long id, @Valid @RequestBody MedicineDTO medicineDTO) {
        MedicineDTO updatedMedicine = pharmacyService.updateMedicine(id, medicineDTO);
        return ResponseEntity.ok(updatedMedicine);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    @Operation(summary = "Delete a medicine", description = "Soft delete a medicine from the pharmacy inventory")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        pharmacyService.deleteMedicine(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    @Operation(summary = "Update medicine stock", description = "Update the stock quantity of a specific medicine")
    public ResponseEntity<MedicineDTO> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        MedicineDTO updatedMedicine = pharmacyService.updateStock(id, quantity);
        return ResponseEntity.ok(updatedMedicine);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock medicines", description = "Retrieve all medicines that are low in stock")
    public ResponseEntity<List<MedicineDTO>> getLowStockMedicines() {
        List<MedicineDTO> medicines = pharmacyService.getLowStockMedicines();
        return ResponseEntity.ok(medicines);
    }

    @GetMapping("/search")
    @Operation(summary = "Search medicines", description = "Search medicines by name or generic name")
    public ResponseEntity<List<MedicineDTO>> searchMedicines(@RequestParam String query) {
        List<MedicineDTO> medicines = pharmacyService.searchMedicines(query);
        return ResponseEntity.ok(medicines);
    }
}
