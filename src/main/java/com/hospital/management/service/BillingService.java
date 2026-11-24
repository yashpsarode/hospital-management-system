package com.hospital.management.service;

import com.hospital.management.dto.BillDTO;
import com.hospital.management.dto.BillItemDTO;
import com.hospital.management.entity.Appointment;
import com.hospital.management.entity.Bill;
import com.hospital.management.entity.BillItem;
import com.hospital.management.entity.Patient;
import com.hospital.management.enums.BillStatus;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.repository.BillRepository;
import com.hospital.management.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BillingService {

    private final BillRepository billRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional(readOnly = true)
    public List<BillDTO> getAllBills() {
        return billRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BillDTO getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", id));
        return mapToDTO(bill);
    }

    public BillDTO createBill(BillDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", dto.getPatientId()));

        Bill bill = mapToEntity(dto);
        bill.setPatient(patient);

        if (dto.getAppointmentId() != null) {
            Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", dto.getAppointmentId()));
            bill.setAppointment(appointment);
        }

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<BillItem> items = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (BillItemDTO itemDTO : dto.getItems()) {
                BillItem item = new BillItem();
                item.setBill(bill);
                item.setDescription(itemDTO.getDescription());
                item.setQuantity(itemDTO.getQuantity());
                item.setUnitPrice(itemDTO.getUnitPrice());

                BigDecimal itemTotal = itemDTO.getUnitPrice()
                        .multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
                item.setAmount(itemTotal);

                totalAmount = totalAmount.add(itemTotal);
                items.add(item);
            }

            bill.setItems(items);
            bill.setTotalAmount(totalAmount);
        }

        if (bill.getPaidAmount() == null) {
            bill.setPaidAmount(BigDecimal.ZERO);
        }

        bill.setStatus(BillStatus.PENDING);

        Bill saved = billRepository.save(bill);
        return mapToDTO(saved);
    }

    public BillDTO updatePayment(Long id, BigDecimal amount, String paymentMethod) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", id));

        BigDecimal currentPaid = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newPaidAmount = currentPaid.add(amount);
        bill.setPaidAmount(newPaidAmount);
        bill.setPaymentMethod(paymentMethod);

        if (bill.getTotalAmount() != null && newPaidAmount.compareTo(bill.getTotalAmount()) >= 0) {
            bill.setStatus(BillStatus.PAID);
        } else {
            bill.setStatus(BillStatus.PARTIALLY_PAID);
        }

        Bill updated = billRepository.save(bill);
        return mapToDTO(updated);
    }

    @Transactional(readOnly = true)
    public List<BillDTO> getBillsByPatient(Long patientId) {
        return billRepository.findByPatientId(patientId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BillDTO> getBillsByStatus(String status) {
        BillStatus billStatus = BillStatus.valueOf(status.toUpperCase());
        return billRepository.findByStatus(billStatus).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private BillDTO mapToDTO(Bill bill) {
        BillDTO dto = new BillDTO();
        dto.setId(bill.getId());
        dto.setPatientId(bill.getPatient().getId());
        dto.setPatientName(bill.getPatient().getFirstName() + " " + bill.getPatient().getLastName());

        if (bill.getAppointment() != null) {
            dto.setAppointmentId(bill.getAppointment().getId());
        }

        dto.setTotalAmount(bill.getTotalAmount());
        dto.setPaidAmount(bill.getPaidAmount());
        dto.setDiscount(bill.getDiscount());
        dto.setStatus(bill.getStatus() != null ? bill.getStatus().name() : null);
        dto.setPaymentMethod(bill.getPaymentMethod());
        dto.setNotes(bill.getNotes());
        dto.setBillingDate(bill.getBillingDate());

        if (bill.getItems() != null) {
            List<BillItemDTO> itemDTOs = bill.getItems().stream()
                    .map(this::mapItemToDTO)
                    .collect(Collectors.toList());
            dto.setItems(itemDTOs);
        }

        return dto;
    }

    private BillItemDTO mapItemToDTO(BillItem item) {
        BillItemDTO dto = new BillItemDTO();
        dto.setId(item.getId());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setAmount(item.getAmount());
        return dto;
    }

    private Bill mapToEntity(BillDTO dto) {
        Bill bill = new Bill();
        bill.setPaymentMethod(dto.getPaymentMethod());
        bill.setNotes(dto.getNotes());
        bill.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO);
        return bill;
    }
}
