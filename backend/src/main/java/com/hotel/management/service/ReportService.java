package com.hotel.management.service;

import com.hotel.management.dto.*;
import com.hotel.management.entity.*;
import com.hotel.management.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public ReportService(BillRepository billRepository,
                         PaymentRepository paymentRepository,
                         RoomRepository roomRepository,
                         ReservationRepository reservationRepository) {
        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    public RevenueReportResponse getRevenueReport(LocalDate fromDate, LocalDate toDate) {
        RevenueReportResponse report = new RevenueReportResponse();

        LocalDateTime start = (fromDate != null) ? fromDate.atStartOfDay() : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime end = (toDate != null) ? toDate.atTime(LocalTime.MAX) : LocalDate.now().atTime(LocalTime.MAX);

        List<Payment> payments = paymentRepository.findPaymentsBetween(start, end);
        BigDecimal totalRevenue = paymentRepository.sumPaymentsBetween(start, end);
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        List<Bill> allBills = billRepository.findAll();
        BigDecimal totalBilled = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        long paidCount = 0;
        long partiallyPaidCount = 0;
        long unpaidCount = 0;

        for (Bill b : allBills) {
            totalBilled = totalBilled.add(b.getTotalAmount());
            totalPaid = totalPaid.add(b.getPaidAmount());
            if (b.getStatus() == BillStatus.PAID) paidCount++;
            else if (b.getStatus() == BillStatus.PARTIALLY_PAID) partiallyPaidCount++;
            else unpaidCount++;
        }

        Map<PaymentMethod, BigDecimal> methodBreakdown = new HashMap<>();
        for (PaymentMethod method : PaymentMethod.values()) {
            methodBreakdown.put(method, BigDecimal.ZERO);
        }
        for (Payment p : payments) {
            methodBreakdown.put(p.getPaymentMethod(),
                    methodBreakdown.get(p.getPaymentMethod()).add(p.getAmount()));
        }

        report.setTotalRevenue(totalRevenue);
        report.setTotalBilled(totalBilled);
        report.setTotalOutstanding(totalBilled.subtract(totalPaid));
        report.setTotalBillsCount(allBills.size());
        report.setPaidBillsCount(paidCount);
        report.setPartiallyPaidBillsCount(partiallyPaidCount);
        report.setUnpaidBillsCount(unpaidCount);
        report.setRevenueByPaymentMethod(methodBreakdown);
        report.setRecentPayments(payments.stream().limit(10).map(PaymentResponse::fromEntity).collect(Collectors.toList()));

        return report;
    }

    public OccupancyReportResponse getOccupancyReport() {
        OccupancyReportResponse report = new OccupancyReportResponse();

        long totalRooms = roomRepository.count();
        long occupiedRooms = roomRepository.countByStatus(RoomStatus.OCCUPIED);
        long availableRooms = roomRepository.countByStatus(RoomStatus.AVAILABLE);
        long maintenanceRooms = roomRepository.countByStatus(RoomStatus.MAINTENANCE);

        double percentage = 0.0;
        if (totalRooms > 0) {
            percentage = BigDecimal.valueOf((double) occupiedRooms / totalRooms * 100.0)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        Map<RoomType, Long> byType = new HashMap<>();
        Map<RoomType, Long> occupiedByType = new HashMap<>();
        for (RoomType rt : RoomType.values()) {
            byType.put(rt, 0L);
            occupiedByType.put(rt, 0L);
        }

        List<Room> allRooms = roomRepository.findAll();
        for (Room r : allRooms) {
            byType.put(r.getType(), byType.get(r.getType()) + 1);
            if (r.getStatus() == RoomStatus.OCCUPIED) {
                occupiedByType.put(r.getType(), occupiedByType.get(r.getType()) + 1);
            }
        }

        report.setTotalRooms(totalRooms);
        report.setOccupiedRooms(occupiedRooms);
        report.setAvailableRooms(availableRooms);
        report.setMaintenanceRooms(maintenanceRooms);
        report.setOccupancyPercentage(percentage);
        report.setRoomsByType(byType);
        report.setOccupiedByType(occupiedByType);

        return report;
    }

    public List<ReservationResponse> getBookingsReport(LocalDate fromDate, LocalDate toDate) {
        LocalDate start = fromDate != null ? fromDate : LocalDate.now().minusDays(30);
        LocalDate end = toDate != null ? toDate : LocalDate.now().plusDays(30);

        return reservationRepository.findByDateRange(start, end).stream()
                .map(ReservationResponse::fromEntity)
                .collect(Collectors.toList());
    }
}

