package com.buyer.controller;

import com.buyer.service.ZomatoOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Zomato API Controller
 * Handles Zomato integration endpoints including order creation
 */
@RestController
@RequestMapping("/api/zomato")
public class ZomatoController {

    private static final Logger logger = LoggerFactory.getLogger(ZomatoController.class);

    @Autowired
    private ZomatoOrderService zomatoOrderService;

    /**
     * Create order endpoint for Zomato integration
     * POST /api/zomato/create_order
     * 
     * @param orderJson Raw JSON string containing order data from Zomato
     * @param authKey Authentication key for request validation
     * @return ResponseEntity with order creation result
     */
    @PostMapping("/create_order")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody String orderJson,
            @RequestHeader(value = "Auth-Key", required = false) String authKey) {
        
        try {
            logger.info("Zomato create_order API called at: {}", new Date());
            
            if (!isValidAuthKey(authKey)) {
                return createErrorResponse("Invalid or missing Auth-Key", HttpStatus.UNAUTHORIZED);
            }
            
            if (orderJson == null || orderJson.trim().isEmpty()) {
                return createErrorResponse("Empty request body", HttpStatus.BAD_REQUEST);
            }
            
            logger.debug("Processing Zomato order creation request");
            
            return zomatoOrderService.createOrder(orderJson);
            
        } catch (Exception e) {
            logger.error("Unexpected error in create_order endpoint", e);
            return createErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Health check endpoint for Zomato integration
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "fm-buyer Zomato Integration");
        response.put("timestamp", new Date());
        return ResponseEntity.ok(response);
    }


    /**
     * Validate authentication key
     * 
     * @param authKey The authentication key to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidAuthKey(String authKey) {
        // Using the same auth key as the original service
        return "jvVGCPqkPv".equals(authKey);
    }

    /**
     * Create standardized error response
     * 
     * @param message Error message
     * @param status HTTP status
     * @return ResponseEntity with error response
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "failed");
        response.put("message", message);
        response.put("error_code", status.value());
        response.put("timestamp", new Date());
        return ResponseEntity.status(status).body(response);
    }
}