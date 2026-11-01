package com.hotel.management.dto;

import com.hotel.management.entity.PaymentMethod;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RevenueReportResponse {
    private BigDecimal totalRevenue;
    private BigDecimal totalBilled;
    private BigDecimal totalOutstanding;
    private long totalBillsCount;
    private long paidBillsCount;
    private long partiallyPaidBillsCount;
    private long unpaidBillsCount;
    private Map<PaymentMethod, BigDecimal> revenueByPaymentMethod = new HashMap<>();
    private List<PaymentResponse> recentPayments = new ArrayList<>();

    public RevenueReportResponse() {
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalBilled() {
        return totalBilled;
    }

    public void setTotalBilled(BigDecimal totalBilled) {
        this.totalBilled = totalBilled;
    }

    public BigDecimal getTotalOutstanding() {
        return totalOutstanding;
    }

    public void setTotalOutstanding(BigDecimal totalOutstanding) {
        this.totalOutstanding = totalOutstanding;
    }

    public long getTotalBillsCount() {
        return totalBillsCount;
    }

    public void setTotalBillsCount(long totalBillsCount) {
        this.totalBillsCount = totalBillsCount;
    }

    public long getPaidBillsCount() {
        return paidBillsCount;
    }

    public void setPaidBillsCount(long paidBillsCount) {
        this.paidBillsCount = paidBillsCount;
    }

    public long getPartiallyPaidBillsCount() {
        return partiallyPaidBillsCount;
    }

    public void setPartiallyPaidBillsCount(long partiallyPaidBillsCount) {
        this.partiallyPaidBillsCount = partiallyPaidBillsCount;
    }

    public long getUnpaidBillsCount() {
        return unpaidBillsCount;
    }

    public void setUnpaidBillsCount(long unpaidBillsCount) {
        this.unpaidBillsCount = unpaidBillsCount;
    }

    public Map<PaymentMethod, BigDecimal> getRevenueByPaymentMethod() {
        return revenueByPaymentMethod;
    }

    public void setRevenueByPaymentMethod(Map<PaymentMethod, BigDecimal> revenueByPaymentMethod) {
        this.revenueByPaymentMethod = revenueByPaymentMethod;
    }

    public List<PaymentResponse> getRecentPayments() {
        return recentPayments;
    }

    public void setRecentPayments(List<PaymentResponse> recentPayments) {
        this.recentPayments = recentPayments;
    }
}

