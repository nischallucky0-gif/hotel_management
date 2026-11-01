package com.hotel.management.controller;

import com.hotel.management.dto.GuestRequest;
import com.hotel.management.dto.GuestResponse;
import com.hotel.management.dto.ReservationResponse;
import com.hotel.management.service.GuestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
@CrossOrigin(origins = "*")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    public ResponseEntity<List<GuestResponse>> getAllGuests(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(guestService.getAllGuests(query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestResponse> getGuestById(@PathVariable Long id) {
        return ResponseEntity.ok(guestService.getGuestById(id));
    }

    @PostMapping
    public ResponseEntity<GuestResponse> createGuest(@Valid @RequestBody GuestRequest request) {
        GuestResponse created = guestService.createGuest(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuestResponse> updateGuest(@PathVariable Long id, @Valid @RequestBody GuestRequest request) {
        return ResponseEntity.ok(guestService.updateGuest(id, request));
    }

    @GetMapping("/{id}/reservations")
    public ResponseEntity<List<ReservationResponse>> getGuestReservations(@PathVariable Long id) {
        return ResponseEntity.ok(guestService.getGuestReservations(id));
    }
}

