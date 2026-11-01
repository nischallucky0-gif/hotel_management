package com.hotel.management.service;

import com.hotel.management.dto.DashboardStatsResponse;
import com.hotel.management.dto.ReservationResponse;
import com.hotel.management.entity.RoomStatus;
import com.hotel.management.repository.BillRepository;
import com.hotel.management.repository.ReservationRepository;
import com.hotel.management.repository.RoomRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final BillRepository billRepository;

    public DashboardService(RoomRepository roomRepository,
                            ReservationRepository reservationRepository,
                            BillRepository billRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.billRepository = billRepository;
    }

    public DashboardStatsResponse getDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();

        long totalRooms = roomRepository.count();
        long availableRooms = roomRepository.countByStatus(RoomStatus.AVAILABLE);
        long occupiedRooms = roomRepository.countByStatus(RoomStatus.OCCUPIED);
        long maintenanceRooms = roomRepository.countByStatus(RoomStatus.MAINTENANCE);

        LocalDate today = LocalDate.now();
        long todayCheckIns = reservationRepository.countTodayCheckIns(today);
        long todayCheckOuts = reservationRepository.countTodayCheckOuts(today);
        long totalReservations = reservationRepository.count();

        BigDecimal totalRevenue = billRepository.sumTotalPaidRevenue();
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        double occupancyRate = 0.0;
        if (totalRooms > 0) {
            occupancyRate = BigDecimal.valueOf((double) occupiedRooms / totalRooms * 100.0)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        List<ReservationResponse> recentReservations = reservationRepository.findAll(
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).stream().map(ReservationResponse::fromEntity).collect(Collectors.toList());

        List<ReservationResponse> arrivals = reservationRepository.findExpectedCheckIns(today).stream()
                .map(ReservationResponse::fromEntity).collect(Collectors.toList());

        List<ReservationResponse> departures = reservationRepository.findExpectedCheckOuts(today).stream()
                .map(ReservationResponse::fromEntity).collect(Collectors.toList());

        stats.setTotalRooms(totalRooms);
        stats.setAvailableRooms(availableRooms);
        stats.setOccupiedRooms(occupiedRooms);
        stats.setMaintenanceRooms(maintenanceRooms);
        stats.setTodayCheckIns(todayCheckIns);
        stats.setTodayCheckOuts(todayCheckOuts);
        stats.setTotalReservations(totalReservations);
        stats.setTotalRevenue(totalRevenue);
        stats.setOccupancyRate(occupancyRate);
        stats.setRecentReservations(recentReservations);
        stats.setExpectedArrivals(arrivals);
        stats.setExpectedDepartures(departures);

        return stats;
    }
}

