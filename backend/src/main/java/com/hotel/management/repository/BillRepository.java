package com.hotel.management.repository;

import com.hotel.management.entity.Bill;
import com.hotel.management.entity.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByReservationId(Long reservationId);

    Optional<Bill> findByBillNumber(String billNumber);

    List<Bill> findByStatus(BillStatus status);

    @Query("SELECT b FROM Bill b JOIN b.reservation r JOIN r.guest g WHERE " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:query IS NULL OR " +
           "LOWER(b.billNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.bookingReference) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(g.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.room.roomNumber) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY b.createdAt DESC")
    List<Bill> searchBills(@Param("query") String query, @Param("status") BillStatus status);

    @Query("SELECT COALESCE(SUM(b.paidAmount), 0) FROM Bill b")
    BigDecimal sumTotalPaidRevenue();
}

