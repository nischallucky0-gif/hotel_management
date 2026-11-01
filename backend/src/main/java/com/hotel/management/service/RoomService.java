package com.hotel.management.service;

import com.hotel.management.dto.RoomRequest;
import com.hotel.management.dto.RoomResponse;
import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.ReservationStatus;
import com.hotel.management.entity.Room;
import com.hotel.management.entity.RoomStatus;
import com.hotel.management.entity.RoomType;
import com.hotel.management.exception.DuplicateResourceException;
import com.hotel.management.exception.InvalidBookingException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.ReservationRepository;
import com.hotel.management.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public RoomService(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms(String query, RoomType type, RoomStatus status) {
        String trimmedQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        List<Room> rooms = roomRepository.searchRooms(trimmedQuery, type, status);
        return rooms.stream().map(RoomResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long id) {
        Room room = findEntityById(id);
        return RoomResponse.fromEntity(room);
    }

    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new DuplicateResourceException("Room number '" + request.getRoomNumber() + "' already exists");
        }

        Room room = new Room(
                request.getRoomNumber().trim(),
                request.getType(),
                request.getPricePerNight(),
                request.getMaxOccupancy(),
                request.getDescription(),
                request.getStatus() != null ? request.getStatus() : RoomStatus.AVAILABLE
        );

        Room saved = roomRepository.save(room);
        return RoomResponse.fromEntity(saved);
    }

    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = findEntityById(id);

        if (roomRepository.existsByRoomNumberAndIdNot(request.getRoomNumber(), id)) {
            throw new DuplicateResourceException("Room number '" + request.getRoomNumber() + "' is already in use by another room");
        }

        room.setRoomNumber(request.getRoomNumber().trim());
        room.setType(request.getType());
        room.setPricePerNight(request.getPricePerNight());
        room.setMaxOccupancy(request.getMaxOccupancy());
        room.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }

        Room updated = roomRepository.save(room);
        return RoomResponse.fromEntity(updated);
    }

    public void deleteRoom(Long id) {
        Room room = findEntityById(id);

        List<Reservation> activeReservations = reservationRepository.findByRoomId(id).stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED || r.getStatus() == ReservationStatus.CHECKED_IN)
                .collect(Collectors.toList());

        if (!activeReservations.isEmpty()) {
            throw new InvalidBookingException("Cannot delete Room " + room.getRoomNumber() + " because it has active or upcoming reservations");
        }

        roomRepository.delete(room);
    }

    public RoomResponse updateStatus(Long id, RoomStatus status) {
        Room room = findEntityById(id);
        room.setStatus(status);
        return RoomResponse.fromEntity(roomRepository.save(room));
    }

    public Room findEntityById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + id));
    }
}

