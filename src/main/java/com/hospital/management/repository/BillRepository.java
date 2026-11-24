package com.hospital.management.repository;

import com.hospital.management.entity.Bill;
import com.hospital.management.enums.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByPatientId(Long patientId);

    List<Bill> findByStatus(BillStatus status);

    Optional<Bill> findByAppointmentId(Long appointmentId);
}
