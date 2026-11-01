package com.hotel.management.repository;

import com.hotel.management.entity.AdditionalCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdditionalChargeRepository extends JpaRepository<AdditionalCharge, Long> {
    List<AdditionalCharge> findByBillId(Long billId);
}

