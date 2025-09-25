package com.buyer.controller;

import com.buyer.dto.swiggy.SwiggyOrderRequest;
import com.buyer.service.SwiggyOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Swiggy API Controller
 * Handles Swiggy integration endpoints including order creation
 */
@RestController
@RequestMapping("/partners/api/v1/swiggy")
public class SwiggyController {

    private static final Logger logger = LoggerFactory.getLogger(SwiggyController.class);

    @Autowired
    private SwiggyOrderService swiggyOrderService;

    /**
     * Create order endpoint for Swiggy integration
     * POST /api/swiggy/orders/{merchantId}
     * 
     * @param merchantId Merchant ID from path variable
     * @param authToken Authentication token from header
     * @param xForwardedFor X-Forwarded-For header
     * @param swiggyOrderRequest SwiggyOrderRequest DTO containing order data from Swiggy
     * @return ResponseEntity with order creation result
     */
    @PostMapping("/orders/{merchantId}")
    public ResponseEntity<Map<String, Object>> createOrderForSwiggy(
            @PathVariable(value = "merchantId") String merchantId,
            @RequestHeader(value = "x-auth-token", required = false) String authToken,
            @RequestHeader(value = "x-forwarded-for", required = false) String xForwardedFor,
            @RequestBody SwiggyOrderRequest swiggyOrderRequest) {
        
        try {
            logger.info("Swiggy create_order API called at: {} for merchantId: {}, orderId: {}", 
                       new Date(), merchantId, swiggyOrderRequest.getOrderId());
            
            // Basic validation
            if (!isValidMerchant(merchantId)) {
                return createErrorResponse("Invalid merchant ID", HttpStatus.BAD_REQUEST);
            }
            
            if (!isValidAuthToken(authToken)) {
                return createErrorResponse("Invalid or missing x-auth-token", HttpStatus.UNAUTHORIZED);
            }
            
            if (swiggyOrderRequest == null) {
                return createErrorResponse("Empty request body", HttpStatus.BAD_REQUEST);
            }
            
            if (swiggyOrderRequest.getOrderId() == null) {
                return createErrorResponse("Missing order ID", HttpStatus.BAD_REQUEST);
            }
            
            logger.debug("Processing Swiggy order creation request for merchantId: {}, orderId: {}", 
                        merchantId, swiggyOrderRequest.getOrderId());
            
            return swiggyOrderService.createOrder(swiggyOrderRequest, merchantId);
            
        } catch (Exception e) {
            logger.error("Unexpected error in Swiggy create_order endpoint", e);
            return createErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Health check endpoint for Swiggy integration
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "fm-buyer Swiggy Integration");
        response.put("timestamp", new Date());
        return ResponseEntity.ok(response);
    }

    /**
     * Validate merchant ID
     * 
     * @param merchantId The merchant ID to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidMerchant(String merchantId) {
        // Add your merchant validation logic here
        return merchantId != null && !merchantId.trim().isEmpty();
    }

    /**
     * Validate authentication token
     * 
     * @param authToken The authentication token to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidAuthToken(String authToken) {
        // Add your token validation logic here
        // For now, using a simple validation - replace with actual logic
        return authToken != null && authToken.length() > 0;
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