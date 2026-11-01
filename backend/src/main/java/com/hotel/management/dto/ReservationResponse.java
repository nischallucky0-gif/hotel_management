package com.hotel.management.dto;

import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationResponse {
    private Long id;
    private String bookingReference;
    private Long guestId;
    private String guestName;
    private String guestPhone;
    private String guestEmail;
    private Long roomId;
    private String roomNumber;
    private String roomType;
    private BigDecimal roomPricePerNight;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private long numberOfNights;
    private Integer numberOfGuests;
    private BigDecimal totalAmount;
    private ReservationStatus status;
    private String notes;
    private Long billId;
    private String billStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReservationResponse() {
    }

    public static ReservationResponse fromEntity(Reservation reservation) {
        ReservationResponse resp = new ReservationResponse();
        resp.setId(reservation.getId());
        resp.setBookingReference(reservation.getBookingReference());
        if (reservation.getGuest() != null) {
            resp.setGuestId(reservation.getGuest().getId());
            resp.setGuestName(reservation.getGuest().getFullName());
            resp.setGuestPhone(reservation.getGuest().getPhoneNumber());
            resp.setGuestEmail(reservation.getGuest().getEmail());
        }
        if (reservation.getRoom() != null) {
            resp.setRoomId(reservation.getRoom().getId());
            resp.setRoomNumber(reservation.getRoom().getRoomNumber());
            resp.setRoomType(reservation.getRoom().getType().name());
            resp.setRoomPricePerNight(reservation.getRoom().getPricePerNight());
        }
        resp.setCheckInDate(reservation.getCheckInDate());
        resp.setCheckOutDate(reservation.getCheckOutDate());
        if (reservation.getCheckInDate() != null && reservation.getCheckOutDate() != null) {
            resp.setNumberOfNights(java.time.temporal.ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate()));
        }
        resp.setNumberOfGuests(reservation.getNumberOfGuests());
        resp.setTotalAmount(reservation.getTotalAmount());
        resp.setStatus(reservation.getStatus());
        resp.setNotes(reservation.getNotes());
        resp.setCreatedAt(reservation.getCreatedAt());
        resp.setUpdatedAt(reservation.getUpdatedAt());
        return resp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
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

