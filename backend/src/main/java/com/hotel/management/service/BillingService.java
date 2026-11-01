package com.hotel.management.service;

import com.hotel.management.dto.*;
import com.hotel.management.entity.*;
import com.hotel.management.exception.InvalidBookingException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.AdditionalChargeRepository;
import com.hotel.management.repository.BillRepository;
import com.hotel.management.repository.PaymentRepository;
import com.hotel.management.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BillingService {

    private final BillRepository billRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final AdditionalChargeRepository additionalChargeRepository;

    public BillingService(BillRepository billRepository,
                          ReservationRepository reservationRepository,
                          PaymentRepository paymentRepository,
                          AdditionalChargeRepository additionalChargeRepository) {
        this.billRepository = billRepository;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
        this.additionalChargeRepository = additionalChargeRepository;
    }

    @Transactional(readOnly = true)
    public List<BillResponse> getAllBills(String query, BillStatus status) {
        String trimmedQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        List<Bill> bills = billRepository.searchBills(trimmedQuery, status);
        return bills.stream().map(BillResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BillResponse getBillById(Long id) {
        Bill bill = findEntityById(id);
        return BillResponse.fromEntity(bill);
    }

    @Transactional(readOnly = true)
    public BillResponse getBillByReservationId(Long reservationId) {
        Bill bill = billRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found for reservation ID: " + reservationId));
        return BillResponse.fromEntity(bill);
    }

    public BillResponse getOrCreateBillForReservation(Long reservationId) {
        return billRepository.findByReservationId(reservationId)
                .map(BillResponse::fromEntity)
                .orElseGet(() -> {
                    Reservation reservation = reservationRepository.findById(reservationId)
                            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));
                    String billNumber = "INV-" + (1000 + reservation.getId());
                    Bill bill = new Bill(billNumber, reservation, reservation.getTotalAmount());
                    return BillResponse.fromEntity(billRepository.save(bill));
                });
    }

    public BillResponse addAdditionalCharge(Long billId, AdditionalChargeRequest request) {
        Bill bill = findEntityById(billId);

        AdditionalCharge charge = new AdditionalCharge(
                bill,
                request.getServiceCategory().trim(),
                request.getDescription() != null ? request.getDescription().trim() : null,
                request.getAmount()
        );

        additionalChargeRepository.save(charge);
        bill.addAdditionalCharge(charge);
        Bill updated = billRepository.save(bill);
        return BillResponse.fromEntity(updated);
    }

    public BillResponse recordPayment(Long billId, PaymentRequest request) {
        Bill bill = findEntityById(billId);

        if (bill.getStatus() == BillStatus.PAID) {
            throw new InvalidBookingException("Bill is already fully paid");
        }

        if (request.getAmount().compareTo(bill.getBalanceAmount()) > 0) {
            throw new InvalidBookingException("Payment amount (" + request.getAmount() +
                    ") exceeds remaining balance (" + bill.getBalanceAmount() + ")");
        }

        String txnRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String notes = request.getNotes() != null && !request.getNotes().trim().isEmpty()
                ? request.getNotes().trim() + " (Simulated Hotel Transaction)"
                : "Simulated Hotel Transaction";

        Payment payment = new Payment(
                bill,
                txnRef,
                request.getAmount(),
                request.getPaymentMethod(),
                notes
        );

        paymentRepository.save(payment);
        bill.addPayment(payment);
        Bill updated = billRepository.save(bill);
        return BillResponse.fromEntity(updated);
    }

    public Bill findEntityById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + id));
    }
}
