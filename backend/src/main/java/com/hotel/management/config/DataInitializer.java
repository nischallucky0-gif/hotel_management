package com.hotel.management.config;

import com.hotel.management.entity.*;
import com.hotel.management.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final ReservationRepository reservationRepository;
    private final BillRepository billRepository;

    public DataInitializer(RoomRepository roomRepository,
                           GuestRepository guestRepository,
                           ReservationRepository reservationRepository,
                           BillRepository billRepository) {
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
        this.billRepository = billRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roomRepository.count() > 0) {
            log.info("Database already initialized with data. Skipping sample data initialization.");
            return;
        }

        log.info("Initializing sample data for Hotel Management System...");

        // 1. Create Sample Rooms
        Room r101 = new Room("101", RoomType.SINGLE, new BigDecimal("75.00"), 1, "Standard Single Room with City View, High-Speed Wi-Fi", RoomStatus.OCCUPIED);
        Room r102 = new Room("102", RoomType.SINGLE, new BigDecimal("80.00"), 1, "Deluxe Single Room with Balcony and Workspace", RoomStatus.AVAILABLE);
        Room r201 = new Room("201", RoomType.DOUBLE, new BigDecimal("120.00"), 2, "Superior Double Queen Room with Garden View", RoomStatus.AVAILABLE);
        Room r202 = new Room("202", RoomType.DOUBLE, new BigDecimal("130.00"), 2, "Executive Double Room with Mini Bar and Smart TV", RoomStatus.OCCUPIED);
        Room r301 = new Room("301", RoomType.DELUXE, new BigDecimal("190.00"), 3, "Deluxe King Suite with Jacuzzi and Ocean Panorama", RoomStatus.AVAILABLE);
        Room r302 = new Room("302", RoomType.DELUXE, new BigDecimal("210.00"), 3, "Luxury Deluxe Suite with Private Terrace", RoomStatus.MAINTENANCE);
        Room r401 = new Room("401", RoomType.SUITE, new BigDecimal("350.00"), 4, "Presidential Suite with 2 Bedrooms, Living Lounge & Butler", RoomStatus.AVAILABLE);
        Room r402 = new Room("402", RoomType.SUITE, new BigDecimal("320.00"), 4, "Grand Penthouse Suite with Panoramic Skyline View", RoomStatus.AVAILABLE);

        roomRepository.saveAll(List.of(r101, r102, r201, r202, r301, r302, r401, r402));

        // 2. Create Sample Guests
        Guest g1 = new Guest("Alexander Wright", "+1-555-0192", "alexander.wright@example.com", "742 Evergreen Terrace, Springfield", "PASSPORT-US-98124");
        Guest g2 = new Guest("Sophia Martinez", "+1-555-0283", "sophia.martinez@example.com", "1204 Sunset Blvd, Los Angeles, CA", "DL-CA-449102");
        Guest g3 = new Guest("David Chen", "+1-555-0374", "david.chen@example.com", "88 Orchard Road, Singapore", "PASSPORT-SG-33109");
        Guest g4 = new Guest("Emma Johnson", "+1-555-0465", "emma.johnson@example.com", "15 Victoria Street, London, UK", "DL-UK-772911");
        Guest g5 = new Guest("Marcus Vance", "+1-555-0556", "marcus.vance@example.com", "452 Pine Hill Road, Seattle, WA", "PASSPORT-US-55102");

        guestRepository.saveAll(List.of(g1, g2, g3, g4, g5));

        LocalDate today = LocalDate.now();

        // 3. Create Sample Reservations
        // Active Stay 1: Checked In (r101, g1)
        Reservation res1 = new Reservation("RES-1001", g1, r101, today.minusDays(2), today.plusDays(2), 1, new BigDecimal("300.00"), ReservationStatus.CHECKED_IN, "Guest requested late checkout.");
        reservationRepository.save(res1);
        Bill bill1 = new Bill("INV-1001", res1, new BigDecimal("300.00"));
        bill1.addAdditionalCharge(new AdditionalCharge(bill1, "Food & Beverage", "In-Room Dining: Steak & Wine", new BigDecimal("48.50")));
        bill1.addAdditionalCharge(new AdditionalCharge(bill1, "Laundry", "Express Suit Dry Cleaning", new BigDecimal("25.00")));
        bill1.addPayment(new Payment(bill1, "TXN-A101-1", new BigDecimal("200.00"), PaymentMethod.CARD, "Simulated Credit Card Advance"));
        billRepository.save(bill1);

        // Active Stay 2: Checked In (r202, g2)
        Reservation res2 = new Reservation("RES-1002", g2, r202, today.minusDays(1), today.plusDays(3), 2, new BigDecimal("520.00"), ReservationStatus.CHECKED_IN, "VIP Guest anniversary stay.");
        reservationRepository.save(res2);
        Bill bill2 = new Bill("INV-1002", res2, new BigDecimal("520.00"));
        bill2.addAdditionalCharge(new AdditionalCharge(bill2, "Room Service", "Champagne & Fruit Platter", new BigDecimal("65.00")));
        bill2.addPayment(new Payment(bill2, "TXN-B202-1", new BigDecimal("585.00"), PaymentMethod.UPI, "Simulated Full UPI Payment"));
        billRepository.save(bill2);

        // Upcoming Confirmed 1: Checking in today (r102, g3)
        Reservation res3 = new Reservation("RES-1003", g3, r102, today, today.plusDays(3), 1, new BigDecimal("240.00"), ReservationStatus.CONFIRMED, "Arriving around 4:00 PM via airport taxi.");
        reservationRepository.save(res3);
        Bill bill3 = new Bill("INV-1003", res3, new BigDecimal("240.00"));
        billRepository.save(bill3);

        // Upcoming Confirmed 2: Next week (r301, g4)
        Reservation res4 = new Reservation("RES-1004", g4, r301, today.plusDays(5), today.plusDays(9), 2, new BigDecimal("760.00"), ReservationStatus.CONFIRMED, "Request high floor quiet room.");
        reservationRepository.save(res4);
        Bill bill4 = new Bill("INV-1004", res4, new BigDecimal("760.00"));
        bill4.addPayment(new Payment(bill4, "TXN-D301-1", new BigDecimal("380.00"), PaymentMethod.CARD, "Simulated 50% Deposit"));
        billRepository.save(bill4);

        // Past Completed Stay: Checked out (r401, g5)
        Reservation res5 = new Reservation("RES-1005", g5, r401, today.minusDays(6), today.minusDays(1), 3, new BigDecimal("1750.00"), ReservationStatus.CHECKED_OUT, "Executive corporate retreat.");
        reservationRepository.save(res5);
        Bill bill5 = new Bill("INV-1005", res5, new BigDecimal("1750.00"));
        bill5.addAdditionalCharge(new AdditionalCharge(bill5, "Airport Shuttle", "Luxury Mercedes Van Transfer", new BigDecimal("95.00")));
        bill5.addPayment(new Payment(bill5, "TXN-E401-1", new BigDecimal("1845.00"), PaymentMethod.CASH, "Simulated Corporate Cash Settlement"));
        billRepository.save(bill5);

        log.info("Sample hotel data successfully populated: 8 rooms, 5 guests, 5 reservations, and associated bills.");
    }
}

