package com.hotel.management;

import com.hotel.management.dto.DashboardStatsResponse;
import com.hotel.management.dto.OccupancyReportResponse;
import com.hotel.management.dto.RevenueReportResponse;
import com.hotel.management.service.DashboardService;
import com.hotel.management.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DashboardAndReportServiceTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ReportService reportService;

    @Test
    @DisplayName("Should fetch accurate dashboard statistics")
    void testDashboardStats() {
        DashboardStatsResponse stats = dashboardService.getDashboardStats();
        assertNotNull(stats);
        assertTrue(stats.getTotalRooms() >= 8); // Seeded rooms
        assertTrue(stats.getTotalReservations() >= 5);
        assertNotNull(stats.getTotalRevenue());
        assertTrue(stats.getOccupancyRate() >= 0.0);
        assertNotNull(stats.getRecentReservations());
    }

    @Test
    @DisplayName("Should generate revenue and occupancy reports")
    void testReports() {
        RevenueReportResponse revReport = reportService.getRevenueReport(LocalDate.now().minusDays(30), LocalDate.now().plusDays(30));
        assertNotNull(revReport);
        assertNotNull(revReport.getTotalRevenue());
        assertNotNull(revReport.getRevenueByPaymentMethod());

        OccupancyReportResponse occReport = reportService.getOccupancyReport();
        assertNotNull(occReport);
        assertTrue(occReport.getTotalRooms() > 0);
        assertNotNull(occReport.getRoomsByType());
    }
}

