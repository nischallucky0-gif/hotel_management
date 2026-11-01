package com.hotel.management.dto;

import com.hotel.management.entity.Bill;
import com.hotel.management.entity.BillStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BillResponse {
    private Long id;
    private String billNumber;
    private Long reservationId;
    private String bookingReference;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String roomNumber;
    private String roomType;
    private BigDecimal roomPricePerNight;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private long numberOfNights;
    private BigDecimal roomCharges;
    private BigDecimal additionalChargesTotal;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private BillStatus status;
    private List<AdditionalChargeResponse> additionalCharges = new ArrayList<>();
    private List<PaymentResponse> payments = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BillResponse() {
    }

    public static BillResponse fromEntity(Bill bill) {
        BillResponse resp = new BillResponse();
        resp.setId(bill.getId());
        resp.setBillNumber(bill.getBillNumber());
        if (bill.getReservation() != null) {
            resp.setReservationId(bill.getReservation().getId());
            resp.setBookingReference(bill.getReservation().getBookingReference());
            if (bill.getReservation().getGuest() != null) {
                resp.setGuestName(bill.getReservation().getGuest().getFullName());
                resp.setGuestEmail(bill.getReservation().getGuest().getEmail());
                resp.setGuestPhone(bill.getReservation().getGuest().getPhoneNumber());
            }
            if (bill.getReservation().getRoom() != null) {
                resp.setRoomNumber(bill.getReservation().getRoom().getRoomNumber());
                resp.setRoomType(bill.getReservation().getRoom().getType().name());
                resp.setRoomPricePerNight(bill.getReservation().getRoom().getPricePerNight());
            }
            resp.setCheckInDate(bill.getReservation().getCheckInDate());
            resp.setCheckOutDate(bill.getReservation().getCheckOutDate());
            if (bill.getReservation().getCheckInDate() != null && bill.getReservation().getCheckOutDate() != null) {
                resp.setNumberOfNights(ChronoUnit.DAYS.between(bill.getReservation().getCheckInDate(), bill.getReservation().getCheckOutDate()));
            }
        }
        resp.setRoomCharges(bill.getRoomCharges());
        resp.setAdditionalChargesTotal(bill.getAdditionalChargesTotal());
        resp.setTotalAmount(bill.getTotalAmount());
        resp.setPaidAmount(bill.getPaidAmount());
        resp.setBalanceAmount(bill.getBalanceAmount());
        resp.setStatus(bill.getStatus());

        if (bill.getAdditionalCharges() != null) {
            bill.getAdditionalCharges().forEach(ac -> resp.getAdditionalCharges().add(AdditionalChargeResponse.fromEntity(ac)));
        }
        if (bill.getPayments() != null) {
            bill.getPayments().forEach(p -> resp.getPayments().add(PaymentResponse.fromEntity(p)));
        }

        resp.setCreatedAt(bill.getCreatedAt());
        resp.setUpdatedAt(bill.getUpdatedAt());
        return resp;
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

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public BigDecimal getRoomPricePerNight() {
        return roomPricePerNight;
    }

    public void setRoomPricePerNight(BigDecimal roomPricePerNight) {
        this.roomPricePerNight = roomPricePerNight;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public long getNumberOfNights() {
        return numberOfNights;
    }

    public void setNumberOfNights(long numberOfNights) {
        this.numberOfNights = numberOfNights;
    }

    public BigDecimal getRoomCharges() {
        return roomCharges;
    }

    public void setRoomCharges(BigDecimal roomCharges) {
        this.roomCharges = roomCharges;
    }

    public BigDecimal getAdditionalChargesTotal() {
        return additionalChargesTotal;
    }

    public void setAdditionalChargesTotal(BigDecimal additionalChargesTotal) {
        this.additionalChargesTotal = additionalChargesTotal;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public List<AdditionalChargeResponse> getAdditionalCharges() {
        return additionalCharges;
    }

    public void setAdditionalCharges(List<AdditionalChargeResponse> additionalCharges) {
        this.additionalCharges = additionalCharges;
    }

    public List<PaymentResponse> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentResponse> payments) {
        this.payments = payments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

