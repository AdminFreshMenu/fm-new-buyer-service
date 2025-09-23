package com.buyer.repository;

import com.buyer.entity.PaymentEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PaymentEntryRepository extends JpaRepository<PaymentEntry, Long> {
    
    // Custom delete methods for test cleanup
    @Query("DELETE FROM PaymentEntry pe WHERE pe.orderId IN (SELECT o.id FROM OrderInfo o WHERE o.externalOrderId LIKE :prefix%)")
    @Modifying
    void deleteByOrderExternalOrderIdPrefix(@Param("prefix") String prefix);
    
    // Delete methods based on user.lastName prefix for test cleanup
    @Transactional
    @Modifying
    @Query("DELETE FROM PaymentEntry pe WHERE pe.orderId IN (SELECT o.id FROM OrderInfo o WHERE o.user.lastName LIKE CONCAT(:prefix, '%'))")
    void deleteByOrderUserLastNamePrefix(@Param("prefix") String prefix);
    
    void deleteByOrderIdIn(List<Long> orderIds);
}
