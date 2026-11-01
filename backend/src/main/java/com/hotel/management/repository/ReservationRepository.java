package com.hotel.management.repository;

import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByBookingReference(String bookingReference);

    List<Reservation> findByGuestIdOrderByCreatedAtDesc(Long guestId);

    List<Reservation> findByRoomId(Long roomId);

    List<Reservation> findByStatus(ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId " +
           "AND r.status != com.hotel.management.entity.ReservationStatus.CANCELLED " +
           "AND r.checkInDate < :checkOutDate AND r.checkOutDate > :checkInDate " +
           "AND (:excludeId IS NULL OR r.id != :excludeId)")
    List<Reservation> findOverlappingReservations(@Param("roomId") Long roomId,
                                                 @Param("checkInDate") LocalDate checkInDate,
                                                 @Param("checkOutDate") LocalDate checkOutDate,
                                                 @Param("excludeId") Long excludeId);

    @Query("SELECT r FROM Reservation r WHERE " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:query IS NULL OR " +
           "LOWER(r.bookingReference) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.guest.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.room.roomNumber) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:fromDate IS NULL OR r.checkInDate >= :fromDate) AND " +
           "(:toDate IS NULL OR r.checkOutDate <= :toDate) " +
           "ORDER BY r.checkInDate DESC")
    List<Reservation> searchReservations(@Param("query") String query,
                                        @Param("status") ReservationStatus status,
                                        @Param("fromDate") LocalDate fromDate,
                                        @Param("toDate") LocalDate toDate);

    @Query("SELECT r FROM Reservation r WHERE r.checkInDate = :date AND r.status = com.hotel.management.entity.ReservationStatus.CONFIRMED")
    List<Reservation> findExpectedCheckIns(@Param("date") LocalDate date);

    @Query("SELECT r FROM Reservation r WHERE r.checkOutDate = :date AND r.status = com.hotel.management.entity.ReservationStatus.CHECKED_IN")
    List<Reservation> findExpectedCheckOuts(@Param("date") LocalDate date);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.checkInDate = :date AND (r.status = com.hotel.management.entity.ReservationStatus.CONFIRMED OR r.status = com.hotel.management.entity.ReservationStatus.CHECKED_IN)")
    long countTodayCheckIns(@Param("date") LocalDate date);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.checkOutDate = :date AND (r.status = com.hotel.management.entity.ReservationStatus.CHECKED_IN OR r.status = com.hotel.management.entity.ReservationStatus.CHECKED_OUT)")
    long countTodayCheckOuts(@Param("date") LocalDate date);

    @Query("SELECT r FROM Reservation r WHERE r.checkInDate >= :startDate AND r.checkInDate <= :endDate ORDER BY r.checkInDate ASC")
    List<Reservation> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

