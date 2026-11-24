package com.hospital.management.repository;

import com.hospital.management.entity.Appointment;
import com.hospital.management.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByAppointmentDate(LocalDate date);

    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);

    List<Appointment> findByStatus(AppointmentStatus status);
}
