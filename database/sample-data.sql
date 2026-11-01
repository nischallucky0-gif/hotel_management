-- ========================================================
-- Hotel Management System - Sample Development Data
-- ========================================================

USE hotel_db;

-- 1. Insert Sample Rooms
INSERT INTO rooms (id, room_number, type, price_per_night, max_occupancy, description, status, created_at, updated_at) VALUES
(1, '101', 'SINGLE', 75.00, 1, 'Standard Single Room with City View, High-Speed Wi-Fi', 'OCCUPIED', NOW(), NOW()),
(2, '102', 'SINGLE', 80.00, 1, 'Deluxe Single Room with Balcony and Workspace', 'AVAILABLE', NOW(), NOW()),
(3, '201', 'DOUBLE', 120.00, 2, 'Superior Double Queen Room with Garden View', 'AVAILABLE', NOW(), NOW()),
(4, '202', 'DOUBLE', 130.00, 2, 'Executive Double Room with Mini Bar and Smart TV', 'OCCUPIED', NOW(), NOW()),
(5, '301', 'DELUXE', 190.00, 3, 'Deluxe King Suite with Jacuzzi and Ocean Panorama', 'AVAILABLE', NOW(), NOW()),
(6, '302', 'DELUXE', 210.00, 3, 'Luxury Deluxe Suite with Private Terrace', 'MAINTENANCE', NOW(), NOW()),
(7, '401', 'SUITE', 350.00, 4, 'Presidential Suite with 2 Bedrooms, Living Lounge & Butler', 'AVAILABLE', NOW(), NOW()),
(8, '402', 'SUITE', 320.00, 4, 'Grand Penthouse Suite with Panoramic Skyline View', 'AVAILABLE', NOW(), NOW());

-- 2. Insert Sample Guests
INSERT INTO guests (id, full_name, phone_number, email, address, id_reference, created_at, updated_at) VALUES
(1, 'Alexander Wright', '+1-555-0192', 'alexander.wright@example.com', '742 Evergreen Terrace, Springfield', 'PASSPORT-US-98124', NOW(), NOW()),
(2, 'Sophia Martinez', '+1-555-0283', 'sophia.martinez@example.com', '1204 Sunset Blvd, Los Angeles, CA', 'DL-CA-449102', NOW(), NOW()),
(3, 'David Chen', '+1-555-0374', 'david.chen@example.com', '88 Orchard Road, Singapore', 'PASSPORT-SG-33109', NOW(), NOW()),
(4, 'Emma Johnson', '+1-555-0465', 'emma.johnson@example.com', '15 Victoria Street, London, UK', 'DL-UK-772911', NOW(), NOW()),
(5, 'Marcus Vance', '+1-555-0556', 'marcus.vance@example.com', '452 Pine Hill Road, Seattle, WA', 'PASSPORT-US-55102', NOW(), NOW());

-- 3. Insert Sample Reservations
INSERT INTO reservations (id, booking_reference, guest_id, room_id, check_in_date, check_out_date, number_of_guests, total_amount, status, notes, created_at, updated_at) VALUES
(1, 'RES-1001', 1, 1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 1, 300.00, 'CHECKED_IN', 'Guest requested late checkout.', NOW(), NOW()),
(2, 'RES-1002', 2, 4, DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 2, 520.00, 'CHECKED_IN', 'VIP Guest anniversary stay.', NOW(), NOW()),
(3, 'RES-1003', 3, 2, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), 1, 240.00, 'CONFIRMED', 'Arriving around 4:00 PM via airport taxi.', NOW(), NOW()),
(4, 'RES-1004', 4, 5, DATE_ADD(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 9 DAY), 2, 760.00, 'CONFIRMED', 'Request high floor quiet room.', NOW(), NOW()),
(5, 'RES-1005', 5, 7, DATE_SUB(CURDATE(), INTERVAL 6 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY), 3, 1750.00, 'CHECKED_OUT', 'Executive corporate retreat.', NOW(), NOW());

-- 4. Insert Sample Bills
INSERT INTO bills (id, bill_number, reservation_id, room_charges, additional_charges_total, total_amount, paid_amount, balance_amount, status, created_at, updated_at) VALUES
(1, 'INV-1001', 1, 300.00, 73.50, 373.50, 200.00, 173.50, 'PARTIALLY_PAID', NOW(), NOW()),
(2, 'INV-1002', 2, 520.00, 65.00, 585.00, 585.00, 0.00, 'PAID', NOW(), NOW()),
(3, 'INV-1003', 3, 240.00, 0.00, 240.00, 0.00, 240.00, 'UNPAID', NOW(), NOW()),
(4, 'INV-1004', 4, 760.00, 0.00, 760.00, 380.00, 380.00, 'PARTIALLY_PAID', NOW(), NOW()),
(5, 'INV-1005', 5, 1750.00, 95.00, 1845.00, 1845.00, 0.00, 'PAID', NOW(), NOW());

-- 5. Insert Additional Charges
INSERT INTO additional_charges (id, bill_id, service_category, description, amount, charge_date) VALUES
(1, 1, 'Food & Beverage', 'In-Room Dining: Steak & Wine', 48.50, NOW()),
(2, 1, 'Laundry', 'Express Suit Dry Cleaning', 25.00, NOW()),
(3, 2, 'Room Service', 'Champagne & Fruit Platter', 65.00, NOW()),
(4, 5, 'Airport Shuttle', 'Luxury Mercedes Van Transfer', 95.00, NOW());

-- 6. Insert Payments
INSERT INTO payments (id, bill_id, transaction_reference, amount, payment_method, payment_date, notes) VALUES
(1, 1, 'TXN-A101-1', 200.00, 'CARD', NOW(), 'Simulated Credit Card Advance'),
(2, 2, 'TXN-B202-1', 585.00, 'UPI', NOW(), 'Simulated Full UPI Payment'),
(3, 4, 'TXN-D301-1', 380.00, 'CARD', NOW(), 'Simulated 50% Deposit'),
(4, 5, 'TXN-E401-1', 1845.00, 'CASH', NOW(), 'Simulated Corporate Cash Settlement');

