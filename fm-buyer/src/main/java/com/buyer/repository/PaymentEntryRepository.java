package com.buyer.repository;

import com.buyer.entity.PaymentEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentEntryRepository extends JpaRepository<PaymentEntry, Long> {
}
