package com.hotel.management.controller;

import com.hotel.management.dto.AdditionalChargeRequest;
import com.hotel.management.dto.BillResponse;
import com.hotel.management.dto.PaymentRequest;
import com.hotel.management.entity.BillStatus;
import com.hotel.management.service.BillingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/api/bills")
    public ResponseEntity<List<BillResponse>> getAllBills(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) BillStatus status) {
        return ResponseEntity.ok(billingService.getAllBills(query, status));
    }

    @GetMapping("/api/bills/{id}")
    public ResponseEntity<BillResponse> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getBillById(id));
    }

    @GetMapping("/api/bills/by-reservation/{reservationId}")
    public ResponseEntity<BillResponse> getBillByReservationId(@PathVariable Long reservationId) {
        return ResponseEntity.ok(billingService.getBillByReservationId(reservationId));
    }

    @PostMapping("/api/reservations/{reservationId}/bill")
    public ResponseEntity<BillResponse> getOrCreateBillForReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(billingService.getOrCreateBillForReservation(reservationId));
    }

    @PostMapping("/api/bills/{id}/charges")
    public ResponseEntity<BillResponse> addCharge(@PathVariable Long id, @Valid @RequestBody AdditionalChargeRequest request) {
        return ResponseEntity.ok(billingService.addAdditionalCharge(id, request));
    }

    @PostMapping("/api/bills/{id}/payments")
    public ResponseEntity<BillResponse> recordPayment(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(billingService.recordPayment(id, request));
    }
}

