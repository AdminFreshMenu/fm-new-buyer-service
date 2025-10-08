package com.buyer.repository;

import com.buyer.entity.OrderInfo;
import com.buyer.entity.OrderEnum.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    
    // Custom delete methods for test cleanup
    @Query("SELECT o FROM OrderInfo o WHERE o.externalOrderId LIKE :prefix%")
    List<OrderInfo> findByExternalOrderIdStartingWith(@Param("prefix") String prefix);
    
    void deleteByExternalOrderIdStartingWith(String prefix);
    
    @Query("DELETE FROM OrderInfo o WHERE o.externalOrderId LIKE :prefix%")
    @Modifying
    void deleteByExternalOrderIdPrefix(@Param("prefix") String prefix);
    
    // Delete methods based on user.lastName prefix for test cleanup
    @Transactional
    @Modifying
    @Query("DELETE FROM OrderInfo o WHERE o.user.lastName LIKE CONCAT(:prefix, '%')")
    void deleteByUserLastNamePrefix(@Param("prefix") String prefix);

    @Query("SELECT o FROM OrderInfo o WHERE o.user.firstName LIKE :pattern")
    List<OrderInfo> findByUserFirstNameLike(@Param("pattern") String pattern);

    @Query("SELECT o FROM OrderInfo o WHERE o.user.lastName LIKE CONCAT(:pattern, '%')")
    List<OrderInfo> findByUserLastNameLike(@Param("pattern") String pattern);


}