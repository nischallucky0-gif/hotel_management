package com.hotel.management.dto;

import com.hotel.management.entity.Guest;
import java.time.LocalDateTime;

public class GuestResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private String idReference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GuestResponse() {
    }

    public static GuestResponse fromEntity(Guest guest) {
        GuestResponse resp = new GuestResponse();
        resp.setId(guest.getId());
        resp.setFullName(guest.getFullName());
        resp.setPhoneNumber(guest.getPhoneNumber());
        resp.setEmail(guest.getEmail());
        resp.setAddress(guest.getAddress());
        resp.setIdReference(guest.getIdReference());
        resp.setCreatedAt(guest.getCreatedAt());
        resp.setUpdatedAt(guest.getUpdatedAt());
        return resp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdReference() {
        return idReference;
    }

    public void setIdReference(String idReference) {
        this.idReference = idReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

