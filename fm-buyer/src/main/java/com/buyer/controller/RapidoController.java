package com.buyer.controller;

import com.buyer.dto.rapido.RapidoOrderCreate;
import com.buyer.service.RapidoOrderService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rapido")
public class RapidoController {

    private static final Logger logger = LoggerFactory.getLogger(RapidoController.class);

    @Autowired
    private RapidoOrderService rapidoOrderService;

    @PostMapping("/create_order")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody String orderJson,
            @RequestHeader(value = "Auth-Key", required = false) String authKey,
            @RequestHeader(value = "fm_cl", required = false) String channel,
            @RequestHeader(value = "brand_id", required = false, defaultValue = "1") Integer brandId) {

        try {
            logger.info("Rapido food create_order API called at: {}", new Date());

            // Authentication
            if (!isValidAuthKey(authKey)) {
                return createErrorResponse("Invalid or missing Auth-Key", HttpStatus.UNAUTHORIZED);
            }

            // Validate request body
            if (orderJson == null || orderJson.trim().isEmpty()) {
                return createErrorResponse("Empty request body", HttpStatus.BAD_REQUEST);
            }

            logger.debug("Processing Rapido order creation request: {}", orderJson);

            // Deserialize order JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            RapidoOrderCreate orderRequest = objectMapper.readValue(orderJson, RapidoOrderCreate.class);

            // Call service
            return rapidoOrderService.createOrder(orderRequest, brandId);

        } catch (Exception e) {
            logger.error("Unexpected error in create_order endpoint", e);
            return createErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
