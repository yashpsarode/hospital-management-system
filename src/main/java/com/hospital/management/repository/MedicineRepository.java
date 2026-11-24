package com.hospital.management.repository;

import com.hospital.management.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findByActiveTrue();

    List<Medicine> findByCategory(String category);

    List<Medicine> findByNameContainingIgnoreCase(String name);

    List<Medicine> findByStockQuantityLessThanEqual(Integer quantity);

    List<Medicine> findByStockQuantityLessThanEqualAndActiveTrue(Integer quantity);

    Optional<Medicine> findByNameAndBatchNumber(String name, String batchNumber);
}
