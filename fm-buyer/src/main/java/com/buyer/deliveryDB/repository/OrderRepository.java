package com.buyer.deliveryDB.repository;

import com.buyer.deliveryDB.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find order by order number
     */
    Optional<Order> findByOrderNumber(Integer orderNumber);

    /**
     * Find all orders assigned to a specific delivery person
     */
    List<Order> findByDeliveryPersonId(Long deliveryPersonId);

    /**
     * Find all orders for a specific user address
     */
    List<Order> findByUserAddressId(Long userAddressId);

    /**
     * Find all orders with a specific order invoice
     */
    List<Order> findByOrderInvoiceId(Long orderInvoiceId);

    /**
     * Find orders delivered within a date range
     */
    @Query("SELECT o FROM Order o WHERE o.deliveredAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersDeliveredBetween(@Param("startDate") Timestamp startDate, 
                                          @Param("endDate") Timestamp endDate);

    /**
     * Find orders by delivery person and delivery date
     */
    @Query("SELECT o FROM Order o WHERE o.deliveryPersonId = :deliveryPersonId AND " +
           "DATE(o.deliveredAt) = DATE(:deliveryDate)")
    List<Order> findByDeliveryPersonIdAndDeliveryDate(@Param("deliveryPersonId") Long deliveryPersonId,
                                                     @Param("deliveryDate") Timestamp deliveryDate);

    /**
     * Find orders within a geographical area (by latitude and longitude range)
     */
    @Query("SELECT o FROM Order o WHERE " +
           "CAST(o.latitude AS double) BETWEEN :minLat AND :maxLat AND " +
           "CAST(o.longitude AS double) BETWEEN :minLon AND :maxLon")
    List<Order> findOrdersInArea(@Param("minLat") Double minLatitude,
                                @Param("maxLat") Double maxLatitude,
                                @Param("minLon") Double minLongitude,
                                @Param("maxLon") Double maxLongitude);

    /**
     * Find delivered orders (orders with deliveredAt not null)
     */
    @Query("SELECT o FROM Order o WHERE o.deliveredAt IS NOT NULL ORDER BY o.deliveredAt DESC")
    List<Order> findAllDeliveredOrders();

    /**
     * Find pending orders (orders with deliveredAt is null)
     */
    @Query("SELECT o FROM Order o WHERE o.deliveredAt IS NULL")
    List<Order> findAllPendingOrders();

    /**
     * Count orders by delivery person
     */
    Long countByDeliveryPersonId(Long deliveryPersonId);

    /**
     * Check if order exists by order number
     */
    boolean existsByOrderNumber(Integer orderNumber);

    /**
     * Find orders by multiple order numbers
     */
    List<Order> findByOrderNumberIn(List<Integer> orderNumbers);
}