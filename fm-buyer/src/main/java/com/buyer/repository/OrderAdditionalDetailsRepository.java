package com.buyer.repository;

import com.buyer.entity.OrderEnum.OrderAdditionalData;
import com.buyer.entity.OrderAdditionalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderAdditionalDetailsRepository extends JpaRepository<OrderAdditionalDetails, Long> {
    
    /**
     * Find all additional details for a specific order
     */
    List<OrderAdditionalDetails> findByOrderId(Long orderId);
    
    /**
     * Find specific additional detail by order ID and key
     */
    Optional<OrderAdditionalDetails> findByOrderIdAndOrderKey(Long orderId, OrderAdditionalData orderKey);
    
    /**
     * Find all additional details for multiple orders
     */
    List<OrderAdditionalDetails> findByOrderIdIn(List<Long> orderIds);
    
    /**
     * Check if additional detail exists for order and key
     */
    boolean existsByOrderIdAndOrderKey(Long orderId, OrderAdditionalData orderKey);
    
    /**
     * Delete all additional details for a specific order
     */
    void deleteByOrderId(Long orderId);
    
    /**
     * Delete specific additional detail by order ID and key
     */
    void deleteByOrderIdAndOrderKey(Long orderId, OrderAdditionalData orderKey);
    
    /**
     * Delete all additional details for orders with external order IDs starting with prefix
     */
    @Query("DELETE FROM OrderAdditionalDetails oad WHERE oad.orderId IN (SELECT o.id FROM OrderInfo o WHERE o.externalOrderId LIKE :prefix%)")
    @Modifying
    void deleteByOrderExternalOrderIdPrefix(@Param("prefix") String prefix);
    
    /**
     * Delete all additional details for orders with user.lastName starting with prefix
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM OrderAdditionalDetails oad WHERE oad.orderId IN (SELECT o.id FROM OrderInfo o WHERE o.user.lastName LIKE CONCAT(:prefix, '%'))")
    void deleteByOrderUserLastNamePrefix(@Param("prefix") String prefix);
}