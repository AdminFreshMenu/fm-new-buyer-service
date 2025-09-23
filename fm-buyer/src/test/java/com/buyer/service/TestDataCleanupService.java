package com.buyer.service;

import com.buyer.repository.OrderAdditionalDetailsRepository;
import com.buyer.repository.OrderInfoRepository;
import com.buyer.repository.OrderItemRepository;
import com.buyer.repository.PaymentEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Service for cleaning up test data from the database
 * Used in test methods to ensure clean state before and after tests
 */
@Service
public class TestDataCleanupService {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDataCleanupService.class);
    
    @Autowired
    private OrderInfoRepository orderInfoRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private PaymentEntryRepository paymentEntryRepository;
    
    @Autowired
    private OrderAdditionalDetailsRepository orderAdditionalDetailsRepository;
    
    private static final List<String> TEST_PREFIXES = Arrays.asList("TEST", "DISH_TEST");
    
    /**
     * Clean up all test data for orders with external order IDs starting with test prefixes
     */
    @Transactional
    public void cleanupTestData() {
        logger.info("Starting test data cleanup...");
        
        for (String prefix : TEST_PREFIXES) {
            try {
                cleanupTestDataByPrefix(prefix);
            } catch (Exception e) {
                logger.error("Error cleaning up test data for prefix: {}", prefix, e);
            }
        }
        
        logger.info("Test data cleanup completed");
    }
    
    /**
     * Clean up test data for a specific prefix
     * Order of deletion is important to maintain referential integrity:
     * 1. Order Items (child records)
     * 2. Order Additional Details (child records)
     * 3. Payment Entries (child records)
     * 4. Order Info (parent record)
     */
    @Transactional
    public void cleanupTestDataByPrefix(String prefix) {
        logger.debug("Cleaning up test data for prefix: {}", prefix);
        
        try {
            // 1. Delete Order Items first (foreign key dependency)
            orderItemRepository.deleteByOrderExternalOrderIdPrefix(prefix);
            logger.debug("Cleaned up order items for prefix: {}", prefix);
            
            // 2. Delete Order Additional Details
            orderAdditionalDetailsRepository.deleteByOrderExternalOrderIdPrefix(prefix);
            logger.debug("Cleaned up order additional details for prefix: {}", prefix);
            
            // 3. Delete Payment Entries
            paymentEntryRepository.deleteByOrderExternalOrderIdPrefix(prefix);
            logger.debug("Cleaned up payment entries for prefix: {}", prefix);
            
            // 4. Finally delete Order Info (parent record)
            orderInfoRepository.deleteByExternalOrderIdPrefix(prefix);
            logger.debug("Cleaned up order info for prefix: {}", prefix);
            
        } catch (Exception e) {
            logger.error("Error during cleanup for prefix: {}", prefix, e);
            throw e; // Re-throw to trigger transaction rollback
        }
    }
    
    /**
     * Get count of test orders for monitoring purposes
     */
    public long getTestOrderCount() {
        return TEST_PREFIXES.stream()
                .mapToLong(prefix -> orderInfoRepository.findByExternalOrderIdStartingWith(prefix).size())
                .sum();
    }
    
    /**
     * Check if there are any test orders in the database
     */
    public boolean hasTestOrders() {
        return getTestOrderCount() > 0;
    }
}