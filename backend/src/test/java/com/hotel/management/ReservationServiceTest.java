package com.hotel.management;

import com.hotel.management.dto.*;
import com.hotel.management.entity.*;
import com.hotel.management.exception.InvalidBookingException;
import com.hotel.management.service.BillingService;
import com.hotel.management.service.GuestService;
import com.hotel.management.service.ReservationService;
import com.hotel.management.service.RoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private GuestService guestService;

    @Autowired
    private BillingService billingService;

    @Test
    @DisplayName("Should create reservation and auto-calculate total on backend")
    void testCreateReservation() {
        RoomRequest roomReq = new RoomRequest();
        roomReq.setRoomNumber("501");
        roomReq.setType(RoomType.DOUBLE);
        roomReq.setPricePerNight(new BigDecimal("150.00"));
        roomReq.setMaxOccupancy(2);
        RoomResponse room = roomService.createRoom(roomReq);

        GuestRequest guestReq = new GuestRequest();
        guestReq.setFullName("John Doe");
        guestReq.setEmail("john.doe.unique@test.com");
        guestReq.setPhoneNumber("+1-555-9000");
        GuestResponse guest = guestService.createGuest(guestReq);

        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = LocalDate.now().plusDays(14); // 4 nights

        ReservationRequest resReq = new ReservationRequest();
        resReq.setGuestId(guest.getId());
        resReq.setRoomId(room.getId());
        resReq.setCheckInDate(checkIn);
        resReq.setCheckOutDate(checkOut);
        resReq.setNumberOfGuests(2);

        ReservationResponse res = reservationService.createReservation(resReq);

        assertNotNull(res.getId());
        assertNotNull(res.getBookingReference());
        assertEquals(new BigDecimal("600.00"), res.getTotalAmount()); // 4 * 150.00
        assertEquals(ReservationStatus.CONFIRMED, res.getStatus());
        assertNotNull(res.getBillId());
    }

    @Test
    @DisplayName("Should reject invalid date ranges (checkout before or on checkin)")
    void testInvalidDateRanges() {
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = checkIn; // Same day

        ReservationRequest req = new ReservationRequest();
        req.setRoomId(1L);
        req.setGuestId(1L);
        req.setCheckInDate(checkIn);
        req.setCheckOutDate(checkOut);
        req.setNumberOfGuests(1);

        assertThrows(InvalidBookingException.class, () -> reservationService.createReservation(req));

        req.setCheckOutDate(checkIn.minusDays(1)); // Before
        assertThrows(InvalidBookingException.class, () -> reservationService.createReservation(req));
    }

    @Test
    @DisplayName("Should prevent overlapping active reservations for the same room")
    void testOverlappingReservations() {
        RoomRequest roomReq = new RoomRequest();
        roomReq.setRoomNumber("502");
        roomReq.setType(RoomType.SINGLE);
        roomReq.setPricePerNight(new BigDecimal("100.00"));
        roomReq.setMaxOccupancy(1);
        RoomResponse room = roomService.createRoom(roomReq);

        GuestRequest guestReq = new GuestRequest();
        guestReq.setFullName("Overlap Guest 1");
        guestReq.setEmail("overlap1@test.com");
        guestReq.setPhoneNumber("+1-555-9111");
        GuestResponse guest1 = guestService.createGuest(guestReq);

        LocalDate checkIn1 = LocalDate.now().plusDays(20);
        LocalDate checkOut1 = LocalDate.now().plusDays(25);

        ReservationRequest req1 = new ReservationRequest();
        req1.setRoomId(room.getId());
        req1.setGuestId(guest1.getId());
        req1.setCheckInDate(checkIn1);
        req1.setCheckOutDate(checkOut1);
        req1.setNumberOfGuests(1);
        reservationService.createReservation(req1);

        // Attempt overlapping reservation: 22 to 27
        ReservationRequest req2 = new ReservationRequest();
        req2.setRoomId(room.getId());
        req2.setGuestFullName("Overlap Guest 2");
        req2.setGuestEmail("overlap2@test.com");
        req2.setGuestPhoneNumber("+1-555-9222");
        req2.setCheckInDate(LocalDate.now().plusDays(22));
        req2.setCheckOutDate(LocalDate.now().plusDays(27));
        req2.setNumberOfGuests(1);

        InvalidBookingException ex = assertThrows(InvalidBookingException.class,
                () -> reservationService.createReservation(req2));
        assertTrue(ex.getMessage().contains("already reserved"));
    }

    @Test
    @DisplayName("Should enforce room capacity limits")
    void testRoomCapacityViolations() {
        RoomRequest roomReq = new RoomRequest();
        roomReq.setRoomNumber("503");
        roomReq.setType(RoomType.SINGLE);
        roomReq.setPricePerNight(new BigDecimal("100.00"));
        roomReq.setMaxOccupancy(1);
        RoomResponse room = roomService.createRoom(roomReq);

        ReservationRequest req = new ReservationRequest();
        req.setRoomId(room.getId());
        req.setGuestFullName("Capacity Guest");
        req.setGuestEmail("capacity@test.com");
        req.setGuestPhoneNumber("+1-555-9333");
        req.setCheckInDate(LocalDate.now().plusDays(30));
        req.setCheckOutDate(LocalDate.now().plusDays(32));
        req.setNumberOfGuests(3); // Capacity is 1

        assertThrows(InvalidBookingException.class, () -> reservationService.createReservation(req));
    }

    @Test
    @DisplayName("Should perform check-in and check-out and update room occupancy")
    void testCheckInAndCheckOutWorkflow() {
        RoomRequest roomReq = new RoomRequest();
        roomReq.setRoomNumber("504");
        roomReq.setType(RoomType.DOUBLE);
        roomReq.setPricePerNight(new BigDecimal("120.00"));
        roomReq.setMaxOccupancy(2);
        RoomResponse room = roomService.createRoom(roomReq);

        ReservationRequest req = new ReservationRequest();
        req.setRoomId(room.getId());
        req.setGuestFullName("Workflow Guest");
        req.setGuestEmail("workflow@test.com");
        req.setGuestPhoneNumber("+1-555-9444");
        req.setCheckInDate(LocalDate.now());
        req.setCheckOutDate(LocalDate.now().plusDays(2));
        req.setNumberOfGuests(2);

        ReservationResponse res = reservationService.createReservation(req);
        assertEquals(ReservationStatus.CONFIRMED, res.getStatus());

        // Perform Check-In
        ReservationResponse checkedIn = reservationService.checkIn(res.getId());
        assertEquals(ReservationStatus.CHECKED_IN, checkedIn.getStatus());
        RoomResponse roomAfterCheckIn = roomService.getRoomById(room.getId());
        assertEquals(RoomStatus.OCCUPIED, roomAfterCheckIn.getStatus());

        // Perform Check-Out
        ReservationResponse checkedOut = reservationService.checkOut(res.getId());
        assertEquals(ReservationStatus.CHECKED_OUT, checkedOut.getStatus());
        RoomResponse roomAfterCheckOut = roomService.getRoomById(room.getId());
        assertEquals(RoomStatus.AVAILABLE, roomAfterCheckOut.getStatus());
    }

    @Test
    @DisplayName("Should handle additional charges and payments correctly in billing")
    void testBillingAndPayments() {
        RoomRequest roomReq = new RoomRequest();
        roomReq.setRoomNumber("505");
        roomReq.setType(RoomType.SINGLE);
        roomReq.setPricePerNight(new BigDecimal("100.00"));
        roomReq.setMaxOccupancy(1);
        RoomResponse room = roomService.createRoom(roomReq);

        ReservationRequest req = new ReservationRequest();
        req.setRoomId(room.getId());
        req.setGuestFullName("Billing Guest");
        req.setGuestEmail("billing@test.com");
        req.setGuestPhoneNumber("+1-555-9555");
        req.setCheckInDate(LocalDate.now().plusDays(40));
        req.setCheckOutDate(LocalDate.now().plusDays(42)); // 2 nights * 100 = 200
        req.setNumberOfGuests(1);

        ReservationResponse res = reservationService.createReservation(req);
        BillResponse initialBill = billingService.getBillByReservationId(res.getId());
        assertEquals(new BigDecimal("200.00"), initialBill.getTotalAmount());
        assertEquals(BillStatus.UNPAID, initialBill.getStatus());

        // Add Room Service Charge 35.00
        AdditionalChargeRequest chargeReq = new AdditionalChargeRequest("Room Service", "Breakfast in bed", new BigDecimal("35.00"));
        BillResponse billWithCharge = billingService.addAdditionalCharge(initialBill.getId(), chargeReq);
        assertEquals(new BigDecimal("235.00"), billWithCharge.getTotalAmount());
        assertEquals(new BigDecimal("235.00"), billWithCharge.getBalanceAmount());

        // Partial Payment: 100.00 via CARD
        PaymentRequest pay1 = new PaymentRequest(new BigDecimal("100.00"), PaymentMethod.CARD, "Partial test payment");
        BillResponse billAfterPay1 = billingService.recordPayment(initialBill.getId(), pay1);
        assertEquals(BillStatus.PARTIALLY_PAID, billAfterPay1.getStatus());
        assertEquals(new BigDecimal("135.00"), billAfterPay1.getBalanceAmount());

        // Final Payment: 135.00 via UPI
        PaymentRequest pay2 = new PaymentRequest(new BigDecimal("135.00"), PaymentMethod.UPI, "Final balance payment");
        BillResponse billAfterPay2 = billingService.recordPayment(initialBill.getId(), pay2);
        assertEquals(BillStatus.PAID, billAfterPay2.getStatus());
        assertEquals(BigDecimal.ZERO, billAfterPay2.getBalanceAmount());
    }
}

