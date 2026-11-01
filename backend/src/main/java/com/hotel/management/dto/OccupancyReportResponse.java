package com.hotel.management.dto;

import com.hotel.management.entity.RoomType;
import java.util.HashMap;
import java.util.Map;

public class OccupancyReportResponse {
    private long totalRooms;
    private long occupiedRooms;
    private long availableRooms;
    private long maintenanceRooms;
    private double occupancyPercentage;
    private Map<RoomType, Long> roomsByType = new HashMap<>();
    private Map<RoomType, Long> occupiedByType = new HashMap<>();

    public OccupancyReportResponse() {
    }

    public long getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(long totalRooms) {
        this.totalRooms = totalRooms;
    }

    public long getOccupiedRooms() {
        return occupiedRooms;
    }

    public void setOccupiedRooms(long occupiedRooms) {
        this.occupiedRooms = occupiedRooms;
    }

    public long getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(long availableRooms) {
        this.availableRooms = availableRooms;
    }

    public long getMaintenanceRooms() {
        return maintenanceRooms;
    }

    public void setMaintenanceRooms(long maintenanceRooms) {
        this.maintenanceRooms = maintenanceRooms;
    }

    public double getOccupancyPercentage() {
        return occupancyPercentage;
    }

    public void setOccupancyPercentage(double occupancyPercentage) {
        this.occupancyPercentage = occupancyPercentage;
    }

    public Map<RoomType, Long> getRoomsByType() {
        return roomsByType;
    }

    public void setRoomsByType(Map<RoomType, Long> roomsByType) {
        this.roomsByType = roomsByType;
    }

    public Map<RoomType, Long> getOccupiedByType() {
        return occupiedByType;
    }

    public void setOccupiedByType(Map<RoomType, Long> occupiedByType) {
        this.occupiedByType = occupiedByType;
    }
}

