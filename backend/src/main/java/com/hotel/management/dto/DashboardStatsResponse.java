package com.hotel.management.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardStatsResponse {
    private long totalRooms;
    private long availableRooms;
    private long occupiedRooms;
    private long maintenanceRooms;
    private long todayCheckIns;
    private long todayCheckOuts;
    private long totalReservations;
    private BigDecimal totalRevenue;
    private double occupancyRate;
    private List<ReservationResponse> recentReservations = new ArrayList<>();
    private List<ReservationResponse> expectedArrivals = new ArrayList<>();
    private List<ReservationResponse> expectedDepartures = new ArrayList<>();

    public DashboardStatsResponse() {
    }

    public long getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(long totalRooms) {
        this.totalRooms = totalRooms;
    }

    public long getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(long availableRooms) {
        this.availableRooms = availableRooms;
    }

    public long getOccupiedRooms() {
        return occupiedRooms;
    }

    public void setOccupiedRooms(long occupiedRooms) {
        this.occupiedRooms = occupiedRooms;
    }

    public long getMaintenanceRooms() {
        return maintenanceRooms;
    }

    public void setMaintenanceRooms(long maintenanceRooms) {
        this.maintenanceRooms = maintenanceRooms;
    }

    public long getTodayCheckIns() {
        return todayCheckIns;
    }

    public void setTodayCheckIns(long todayCheckIns) {
        this.todayCheckIns = todayCheckIns;
    }

    public long getTodayCheckOuts() {
        return todayCheckOuts;
    }

    public void setTodayCheckOuts(long todayCheckOuts) {
        this.todayCheckOuts = todayCheckOuts;
    }

    public long getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(long totalReservations) {
        this.totalReservations = totalReservations;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public List<ReservationResponse> getRecentReservations() {
        return recentReservations;
    }

    public void setRecentReservations(List<ReservationResponse> recentReservations) {
        this.recentReservations = recentReservations;
    }

    public List<ReservationResponse> getExpectedArrivals() {
        return expectedArrivals;
    }

    public void setExpectedArrivals(List<ReservationResponse> expectedArrivals) {
        this.expectedArrivals = expectedArrivals;
    }

    public List<ReservationResponse> getExpectedDepartures() {
        return expectedDepartures;
    }

    public void setExpectedDepartures(List<ReservationResponse> expectedDepartures) {
        this.expectedDepartures = expectedDepartures;
    }
}

