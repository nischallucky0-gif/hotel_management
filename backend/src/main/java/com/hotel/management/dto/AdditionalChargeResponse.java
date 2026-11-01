package com.hotel.management.dto;

import com.hotel.management.entity.AdditionalCharge;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdditionalChargeResponse {
    private Long id;
    private String serviceCategory;
    private String description;
    private BigDecimal amount;
    private LocalDateTime chargeDate;

    public AdditionalChargeResponse() {
    }

    public static AdditionalChargeResponse fromEntity(AdditionalCharge charge) {
        AdditionalChargeResponse resp = new AdditionalChargeResponse();
        resp.setId(charge.getId());
        resp.setServiceCategory(charge.getServiceCategory());
        resp.setDescription(charge.getDescription());
        resp.setAmount(charge.getAmount());
        resp.setChargeDate(charge.getChargeDate());
        return resp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getChargeDate() {
        return chargeDate;
    }

    public void setChargeDate(LocalDateTime chargeDate) {
        this.chargeDate = chargeDate;
    }
}

