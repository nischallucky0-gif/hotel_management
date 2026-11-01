package com.hotel.management;

import com.hotel.management.dto.RoomRequest;
import com.hotel.management.dto.RoomResponse;
import com.hotel.management.entity.RoomStatus;
import com.hotel.management.entity.RoomType;
import com.hotel.management.exception.DuplicateResourceException;
import com.hotel.management.service.RoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Test
    @DisplayName("Should create and retrieve room successfully")
    void testCreateAndGetRoom() {
        RoomRequest request = new RoomRequest();
        request.setRoomNumber("999");
        request.setType(RoomType.DELUXE);
        request.setPricePerNight(new BigDecimal("175.00"));
        request.setMaxOccupancy(3);
        request.setDescription("Special test deluxe room");
        request.setStatus(RoomStatus.AVAILABLE);

        RoomResponse created = roomService.createRoom(request);
        assertNotNull(created.getId());
        assertEquals("999", created.getRoomNumber());
        assertEquals(RoomType.DELUXE, created.getType());

        RoomResponse retrieved = roomService.getRoomById(created.getId());
        assertEquals(created.getId(), retrieved.getId());
        assertEquals("999", retrieved.getRoomNumber());
    }

    @Test
    @DisplayName("Should prevent duplicate room numbers")
    void testPreventDuplicateRoomNumber() {
        RoomRequest request = new RoomRequest();
        request.setRoomNumber("888");
        request.setType(RoomType.SINGLE);
        request.setPricePerNight(new BigDecimal("90.00"));
        request.setMaxOccupancy(1);
        roomService.createRoom(request);

        RoomRequest duplicate = new RoomRequest();
        duplicate.setRoomNumber("888");
        duplicate.setType(RoomType.DOUBLE);
        duplicate.setPricePerNight(new BigDecimal("120.00"));
        duplicate.setMaxOccupancy(2);

        assertThrows(DuplicateResourceException.class, () -> roomService.createRoom(duplicate));
    }

    @Test
    @DisplayName("Should update room details")
    void testUpdateRoom() {
        RoomRequest request = new RoomRequest();
        request.setRoomNumber("777");
        request.setType(RoomType.SINGLE);
        request.setPricePerNight(new BigDecimal("85.00"));
        request.setMaxOccupancy(1);
        RoomResponse created = roomService.createRoom(request);

        RoomRequest updateReq = new RoomRequest();
        updateReq.setRoomNumber("777");
        updateReq.setType(RoomType.DOUBLE);
        updateReq.setPricePerNight(new BigDecimal("110.00"));
        updateReq.setMaxOccupancy(2);
        updateReq.setDescription("Upgraded to double");
        updateReq.setStatus(RoomStatus.AVAILABLE);

        RoomResponse updated = roomService.updateRoom(created.getId(), updateReq);
        assertEquals(RoomType.DOUBLE, updated.getType());
        assertEquals(new BigDecimal("110.00"), updated.getPricePerNight());
        assertEquals(2, updated.getMaxOccupancy());
    }

    @Test
    @DisplayName("Should search and filter rooms")
    void testSearchAndFilterRooms() {
        List<RoomResponse> availableRooms = roomService.getAllRooms(null, null, RoomStatus.AVAILABLE);
        assertFalse(availableRooms.isEmpty());
        assertTrue(availableRooms.stream().allMatch(r -> r.getStatus() == RoomStatus.AVAILABLE));
    }
}

