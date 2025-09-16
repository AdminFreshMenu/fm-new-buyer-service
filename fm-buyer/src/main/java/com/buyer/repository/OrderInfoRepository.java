package com.buyer.repository;

import com.buyer.entity.OrderInfo;
import com.buyer.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderInfoRepository extends JpaRepository<OrderInfo, Long> {
    
    Optional<OrderInfo> findByExternalOrderId(String externalOrderId);
    
    @Query("SELECT o FROM OrderInfo o WHERE o.externalOrderId = :externalOrderId AND o.channel = :channel")
    Optional<OrderInfo> findByExternalOrderIdAndChannel(@Param("externalOrderId") String externalOrderId, 
                                                        @Param("channel") Channel channel);
    
    @Query("SELECT o FROM OrderInfo o WHERE o.user.lastName = :lastName AND o.channel = :channel")
    Optional<OrderInfo> findByUserLastNameAndChannel(@Param("lastName") String lastName, 
                                                     @Param("channel") Channel channel);
}