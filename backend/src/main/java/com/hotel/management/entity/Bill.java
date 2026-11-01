package com.hotel.management.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bills", indexes = {
    @Index(name = "idx_bill_number", columnList = "billNumber", unique = true),
    @Index(name = "idx_bill_status", columnList = "status"),
    @Index(name = "idx_bill_reservation", columnList = "reservation_id")
})
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String billNumber;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal roomCharges = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal additionalChargesTotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillStatus status = BillStatus.UNPAID;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdditionalCharge> additionalCharges = new ArrayList<>();

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Bill() {
    }

    public Bill(String billNumber, Reservation reservation, BigDecimal roomCharges) {
        this.billNumber = billNumber;
        this.reservation = reservation;
        this.roomCharges = roomCharges != null ? roomCharges : BigDecimal.ZERO;
        this.additionalChargesTotal = BigDecimal.ZERO;
        this.paidAmount = BigDecimal.ZERO;
        recalculateTotals();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void recalculateTotals() {
        BigDecimal addChargesSum = BigDecimal.ZERO;
        if (additionalCharges != null) {
            for (AdditionalCharge ac : additionalCharges) {
                if (ac.getAmount() != null) {
                    addChargesSum = addChargesSum.add(ac.getAmount());
                }
            }
        }
        this.additionalChargesTotal = addChargesSum;

        BigDecimal rCharges = this.roomCharges != null ? this.roomCharges : BigDecimal.ZERO;
        this.totalAmount = rCharges.add(this.additionalChargesTotal);

        BigDecimal paymentsSum = BigDecimal.ZERO;
        if (payments != null) {
            for (Payment p : payments) {
                if (p.getAmount() != null) {
                    paymentsSum = paymentsSum.add(p.getAmount());
                }
            }
        }
        this.paidAmount = paymentsSum;
        this.balanceAmount = this.totalAmount.subtract(this.paidAmount);

        if (this.paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            this.status = BillStatus.UNPAID;
        } else if (this.paidAmount.compareTo(this.totalAmount) >= 0) {
            this.status = BillStatus.PAID;
            this.balanceAmount = BigDecimal.ZERO;
        } else {
            this.status = BillStatus.PARTIALLY_PAID;
        }
    }

    public void addAdditionalCharge(AdditionalCharge charge) {
        additionalCharges.add(charge);
        charge.setBill(this);
        recalculateTotals();
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setBill(this);
        recalculateTotals();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public BigDecimal getRoomCharges() {
        return roomCharges;
    }

    public void setRoomCharges(BigDecimal roomCharges) {
        this.roomCharges = roomCharges;
        recalculateTotals();
    }

    public BigDecimal getAdditionalChargesTotal() {
        return additionalChargesTotal;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public List<AdditionalCharge> getAdditionalCharges() {
        return additionalCharges;
    }

    public void setAdditionalCharges(List<AdditionalCharge> additionalCharges) {
        this.additionalCharges = additionalCharges;
        recalculateTotals();
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
        recalculateTotals();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
