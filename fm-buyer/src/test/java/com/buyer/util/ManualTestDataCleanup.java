package com.buyer.util;

import com.buyer.service.TestDataCleanupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Manual utility for cleaning up test data from the database
 * Run this class to clean up test orders outside of test execution
 * 
 * Usage:
 * mvn exec:java -Dexec.mainClass="com.buyer.util.ManualTestDataCleanup" -Dexec.classpathScope=test
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.buyer"})
public class ManualTestDataCleanup implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(ManualTestDataCleanup.class);
    
    @Autowired
    private TestDataCleanupService testDataCleanupService;
    
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "test");
        SpringApplication.run(ManualTestDataCleanup.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("===========================================");
        logger.info("MANUAL TEST DATA CLEANUP STARTED");
        logger.info("===========================================");
        
        // Check if there are any test orders
        long testOrderCount = testDataCleanupService.getTestOrderCount();
        logger.info("Found {} test orders to clean up", testOrderCount);
        
        if (testOrderCount > 0) {
            // Clean up test data
            testDataCleanupService.cleanupTestData();
            
            // Verify cleanup
            long remainingCount = testDataCleanupService.getTestOrderCount();
            logger.info("Cleanup completed. Remaining test orders: {}", remainingCount);
            
            if (remainingCount == 0) {
                logger.info("✅ All test data cleaned up successfully!");
            } else {
                logger.warn("⚠️ Some test data may still remain: {} orders", remainingCount);
            }
        } else {
            logger.info("✅ No test data found. Database is already clean!");
        }
        
        logger.info("===========================================");
        logger.info("MANUAL TEST DATA CLEANUP COMPLETED");
        logger.info("===========================================");
    }
}