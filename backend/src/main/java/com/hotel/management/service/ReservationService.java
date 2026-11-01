package com.hotel.management.service;

import com.hotel.management.dto.ReservationRequest;
import com.hotel.management.dto.ReservationResponse;
import com.hotel.management.entity.*;
import com.hotel.management.exception.InvalidBookingException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.BillRepository;
import com.hotel.management.repository.ReservationRepository;
import com.hotel.management.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final GuestService guestService;
    private final BillRepository billRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              RoomRepository roomRepository,
                              GuestService guestService,
                              BillRepository billRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.guestService = guestService;
        this.billRepository = billRepository;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations(String query, ReservationStatus status, LocalDate fromDate, LocalDate toDate) {
        String trimmedQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        List<Reservation> list = reservationRepository.searchReservations(trimmedQuery, status, fromDate, toDate);
        return list.stream().map(this::mapToResponseWithBill).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = findEntityById(id);
        return mapToResponseWithBill(reservation);
    }

    public ReservationResponse createReservation(ReservationRequest request) {
        validateReservationDates(request.getCheckInDate(), request.getCheckOutDate());

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId()));

        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            throw new InvalidBookingException("Room " + room.getRoomNumber() + " is currently under maintenance and cannot be reserved");
        }

        if (request.getNumberOfGuests() > room.getMaxOccupancy()) {
            throw new InvalidBookingException("Number of guests (" + request.getNumberOfGuests() +
                    ") exceeds room maximum occupancy (" + room.getMaxOccupancy() + ")");
        }

        List<Reservation> overlaps = reservationRepository.findOverlappingReservations(
                room.getId(), request.getCheckInDate(), request.getCheckOutDate(), null);

        if (!overlaps.isEmpty()) {
            throw new InvalidBookingException("Room " + room.getRoomNumber() +
                    " is already reserved for the selected date range (" + request.getCheckInDate() + " to " + request.getCheckOutDate() + ")");
        }

        Guest guest = guestService.getOrCreateGuest(request);

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal totalAmount = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        String bookingRef = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Reservation reservation = new Reservation(
                bookingRef,
                guest,
                room,
                request.getCheckInDate(),
                request.getCheckOutDate(),
                request.getNumberOfGuests(),
                totalAmount,
                ReservationStatus.CONFIRMED,
                request.getNotes()
        );

        Reservation savedReservation = reservationRepository.save(reservation);

        // Auto-generate associated Bill
        String billNumber = "INV-" + (1000 + savedReservation.getId());
        Bill bill = new Bill(billNumber, savedReservation, totalAmount);
        billRepository.save(bill);

        return mapToResponseWithBill(savedReservation);
    }

    public ReservationResponse updateReservation(Long id, ReservationRequest request) {
        Reservation reservation = findEntityById(id);

        if (reservation.getStatus() == ReservationStatus.CANCELLED || reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new InvalidBookingException("Cannot modify a reservation that is " + reservation.getStatus());
        }

        validateReservationDates(request.getCheckInDate(), request.getCheckOutDate());

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + request.getRoomId()));

        if (request.getNumberOfGuests() > room.getMaxOccupancy()) {
            throw new InvalidBookingException("Number of guests (" + request.getNumberOfGuests() +
                    ") exceeds room maximum occupancy (" + room.getMaxOccupancy() + ")");
        }

        List<Reservation> overlaps = reservationRepository.findOverlappingReservations(
                room.getId(), request.getCheckInDate(), request.getCheckOutDate(), reservation.getId());

        if (!overlaps.isEmpty()) {
            throw new InvalidBookingException("Room " + room.getRoomNumber() +
                    " is already reserved for the selected date range (" + request.getCheckInDate() + " to " + request.getCheckOutDate() + ")");
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal newRoomCharges = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        reservation.setRoom(room);
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setNumberOfGuests(request.getNumberOfGuests());
        reservation.setTotalAmount(newRoomCharges);
        reservation.setNotes(request.getNotes());

        Reservation updatedReservation = reservationRepository.save(reservation);

        // Synchronize room charges in bill
        billRepository.findByReservationId(reservation.getId()).ifPresent(bill -> {
            bill.setRoomCharges(newRoomCharges);
            billRepository.save(bill);
        });

        return mapToResponseWithBill(updatedReservation);
    }

    public ReservationResponse cancelReservation(Long id) {
        Reservation reservation = findEntityById(id);

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new InvalidBookingException("Reservation is already cancelled");
        }
        if (reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new InvalidBookingException("Cannot cancel a completed stay");
        }

        Room room = reservation.getRoom();
        if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
            room.setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(room);
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation updated = reservationRepository.save(reservation);
        return mapToResponseWithBill(updated);
    }

    public ReservationResponse checkIn(Long id) {
        Reservation reservation = findEntityById(id);

        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new InvalidBookingException("Only confirmed reservations can be checked in. Current status: " + reservation.getStatus());
        }

        Room room = reservation.getRoom();
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            throw new InvalidBookingException("Room " + room.getRoomNumber() + " is currently occupied");
        }

        reservation.setStatus(ReservationStatus.CHECKED_IN);
        room.setStatus(RoomStatus.OCCUPIED);

        roomRepository.save(room);
        Reservation updated = reservationRepository.save(reservation);
        return mapToResponseWithBill(updated);
    }

    public ReservationResponse checkOut(Long id) {
        Reservation reservation = findEntityById(id);

        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new InvalidBookingException("Only checked-in reservations can be checked out. Current status: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        Room room = reservation.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);

        roomRepository.save(room);
        Reservation updated = reservationRepository.save(reservation);
        return mapToResponseWithBill(updated);
    }

    public Reservation findEntityById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));
    }

    private void validateReservationDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new InvalidBookingException("Check-in and check-out dates are required");
        }
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new InvalidBookingException("Check-out date (" + checkOutDate + ") must be after check-in date (" + checkInDate + ")");
        }
    }

    private ReservationResponse mapToResponseWithBill(Reservation reservation) {
        ReservationResponse resp = ReservationResponse.fromEntity(reservation);
        billRepository.findByReservationId(reservation.getId()).ifPresent(bill -> {
            resp.setBillId(bill.getId());
            resp.setBillStatus(bill.getStatus().name());
        });
        return resp;
    }
}

