package com.buyer.repository;

import com.buyer.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    List<OrderItem> findByOrderId(Long orderId);
    
    List<OrderItem> findByProductId(Long productId);
    
    List<OrderItem> findByParentOrderItemId(Long parentOrderItemId);
    
    // Custom delete methods for test cleanup
    @Query("DELETE FROM OrderItem oi WHERE oi.orderId IN (SELECT o.id FROM OrderInfo o WHERE o.externalOrderId LIKE :prefix%)")
    @Modifying
    void deleteByOrderExternalOrderIdPrefix(@Param("prefix") String prefix);
    
    // Delete methods based on user.lastName prefix for test cleanup
    @Transactional
    @Modifying
    @Query("DELETE FROM OrderItem oi WHERE oi.orderId IN (SELECT o.id FROM OrderInfo o WHERE o.user.lastName LIKE CONCAT(:prefix, '%'))")
    void deleteByOrderUserLastNamePrefix(@Param("prefix") String prefix);

    @Transactional
    @Modifying
    @Query("DELETE FROM OrderItem oi WHERE oi.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") Long orderId);


    void deleteByOrderIdIn(List<Long> orderIds);
}