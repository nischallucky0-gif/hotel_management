package com.hotel.management.service;

import com.hotel.management.dto.GuestRequest;
import com.hotel.management.dto.GuestResponse;
import com.hotel.management.dto.ReservationRequest;
import com.hotel.management.dto.ReservationResponse;
import com.hotel.management.entity.Guest;
import com.hotel.management.exception.DuplicateResourceException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.GuestRepository;
import com.hotel.management.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GuestService {

    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;

    public GuestService(GuestRepository guestRepository, ReservationRepository reservationRepository) {
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    public List<GuestResponse> getAllGuests(String query) {
        String trimmedQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        List<Guest> guests = guestRepository.searchGuests(trimmedQuery);
        return guests.stream().map(GuestResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GuestResponse getGuestById(Long id) {
        return GuestResponse.fromEntity(findEntityById(id));
    }

    public GuestResponse createGuest(GuestRequest request) {
        if (guestRepository.existsByEmail(request.getEmail().trim())) {
            throw new DuplicateResourceException("Guest with email '" + request.getEmail() + "' already exists");
        }

        Guest guest = new Guest(
                request.getFullName().trim(),
                request.getPhoneNumber().trim(),
                request.getEmail().trim().toLowerCase(),
                request.getAddress() != null ? request.getAddress().trim() : null,
                request.getIdReference() != null ? request.getIdReference().trim() : null
        );

        Guest saved = guestRepository.save(guest);
        return GuestResponse.fromEntity(saved);
    }

    public GuestResponse updateGuest(Long id, GuestRequest request) {
        Guest guest = findEntityById(id);

        if (guestRepository.existsByEmailAndIdNot(request.getEmail().trim(), id)) {
            throw new DuplicateResourceException("Guest with email '" + request.getEmail() + "' already exists");
        }

        guest.setFullName(request.getFullName().trim());
        guest.setPhoneNumber(request.getPhoneNumber().trim());
        guest.setEmail(request.getEmail().trim().toLowerCase());
        guest.setAddress(request.getAddress() != null ? request.getAddress().trim() : null);
        guest.setIdReference(request.getIdReference() != null ? request.getIdReference().trim() : null);

        Guest updated = guestRepository.save(guest);
        return GuestResponse.fromEntity(updated);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getGuestReservations(Long guestId) {
        findEntityById(guestId); // ensure guest exists
        return reservationRepository.findByGuestIdOrderByCreatedAtDesc(guestId).stream()
                .map(ReservationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Guest findEntityById(Long id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with ID: " + id));
    }

    public Guest getOrCreateGuest(ReservationRequest request) {
        if (request.getGuestId() != null) {
            return findEntityById(request.getGuestId());
        }

        // Inline guest creation
        if (request.getGuestFullName() == null || request.getGuestFullName().trim().isEmpty() ||
            request.getGuestPhoneNumber() == null || request.getGuestPhoneNumber().trim().isEmpty() ||
            request.getGuestEmail() == null || request.getGuestEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Either guestId or complete guest details (fullName, phoneNumber, email) must be provided");
        }

        // Check if existing guest with email exists
        return guestRepository.findByEmail(request.getGuestEmail().trim().toLowerCase())
                .orElseGet(() -> {
                    Guest newGuest = new Guest(
                            request.getGuestFullName().trim(),
                            request.getGuestPhoneNumber().trim(),
                            request.getGuestEmail().trim().toLowerCase(),
                            request.getGuestAddress(),
                            request.getGuestIdReference()
                    );
                    return guestRepository.save(newGuest);
                });
    }
}
