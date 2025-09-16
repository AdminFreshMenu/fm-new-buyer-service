package com.buyer.service;

import com.buyer.entity.Channel;
import com.buyer.entity.OrderAddress;
import com.buyer.entity.OrderInfo;
import com.buyer.entity.OrderUserInfo;
import com.buyer.repository.OrderAddressRepository;
import com.buyer.repository.OrderInfoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZomatoOrderService {

    private static final Logger logger = LoggerFactory.getLogger(ZomatoOrderService.class);

    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @Autowired
    private OrderAddressRepository orderAddressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public ResponseEntity<Map<String, Object>> createOrder(String orderJson) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Zomato create order request received at: {}", new Date());
            logger.debug("Order JSON: {}", orderJson);

            // Parse the incoming JSON
            JsonNode orderNode = objectMapper.readTree(orderJson);
            JsonNode order = orderNode.get("order");

            String zomatoOrderId = extractOrderId(order);

            // Check for duplicate order using lastName (which contains Zomato order ID)
            if (orderInfoRepository.findByUserLastNameAndChannel(zomatoOrderId, Channel.ZOMATO).isPresent()) {
                logger.warn("Duplicate order request received for Zomato orderId: {}", zomatoOrderId);
                response.put("status", "Success");
                response.put("message", "Order already exists");
                response.put("zomatoOrderId", zomatoOrderId);
                return ResponseEntity.ok(response);
            }

            // Create OrderInfo from Zomato request
            OrderInfo orderInfo = mapZomatoOrderToOrderInfo(order, orderJson);
            
            // Save to database first with original Zomato order ID
            OrderInfo savedOrder = orderInfoRepository.save(orderInfo);
            
            // Generate custom external order ID based on brand ID
            String customExternalOrderId = generateExternalOrderId(savedOrder.getBrandId(), savedOrder.getId());
            
            // Update the external order ID
            savedOrder.setExternalOrderId(customExternalOrderId);
            savedOrder = orderInfoRepository.save(savedOrder);
            
            logger.info("Order created successfully with ID: {} for Zomato orderId: {}, Custom externalOrderId: {}", 
                       savedOrder.getId(), zomatoOrderId, customExternalOrderId);

            response.put("status", "Success");
            response.put("message", "Order created successfully");
            response.put("externalOrderId", savedOrder.getExternalOrderId());
            response.put("internalOrderId", savedOrder.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing Zomato order creation", e);
            return createErrorResponse("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Extract order ID from JSON, trying multiple possible field names
     */
    private String extractOrderId(JsonNode order) {
        if (order.has("order_id")) {
            return order.get("order_id").asText();
        }
        if (order.has("orderId")) {
            return order.get("orderId").asText();
        }
        if (order.has("id")) {
            return order.get("id").asText();
        }
        return null;
    }

    private OrderInfo mapZomatoOrderToOrderInfo(JsonNode order, String originalOrderJson) {
        OrderInfo orderInfo = new OrderInfo();
        
        // Basic order information
        String externalOrderId = extractOrderId(order);
        orderInfo.setExternalOrderId(externalOrderId);
        orderInfo.setChannel(Channel.ZOMATO);
        orderInfo.setOrderData(originalOrderJson);
        
        // Set delivery time (default 40 minutes)
        orderInfo.setDeliveryTimeInMinutes("40");
        
        // Customer information - using correct field name from OrderV3
        JsonNode customerDetails = order.get("customer_details");
        if (customerDetails != null) {
            OrderUserInfo userInfo = new OrderUserInfo();
            userInfo.setFirstName(getJsonTextValue(customerDetails, "name"));
            userInfo.setLastName(externalOrderId); // Set order ID as lastName like in old project
            userInfo.setMobileNumber(getJsonTextValue(customerDetails, "phone_number"));
            userInfo.setEmail(getJsonTextValue(customerDetails, "email"));
            orderInfo.setUser(userInfo);
        }
        
        if (customerDetails != null) {
            OrderAddress deliveryAddress = extractAddressFromJson(customerDetails, externalOrderId);
            if (deliveryAddress != null) {
                OrderAddress savedDeliveryAddress = orderAddressRepository.save(deliveryAddress);
                orderInfo.setShippingAddress(savedDeliveryAddress);
                orderInfo.setBillingAddress(savedDeliveryAddress);
            }
        }
        
        if (order.has("gross_amount")) {
            orderInfo.setTotalAmount(order.get("gross_amount").floatValue());
        }
        if (order.has("net_amount")) {
            orderInfo.setFinalAmount(order.get("net_amount").floatValue());
        }
        
        // Handle additional charges for shipping/delivery
        JsonNode additionalCharges = order.get("order_additional_charges");
        if (additionalCharges != null && additionalCharges.isArray()) {
            float totalAdditionalCharges = 0f;
            for (JsonNode charge : additionalCharges) {
                if (charge.has("amount")) {
                    totalAdditionalCharges += charge.get("amount").floatValue();
                }
            }
            orderInfo.setShippingCharges(totalAdditionalCharges);
        }
        
        // Handle order discounts
        JsonNode orderDiscounts = order.get("order_discounts");
        if (orderDiscounts != null && orderDiscounts.isArray()) {
            float totalDiscounts = 0f;
            for (JsonNode discount : orderDiscounts) {
                if (discount.has("amount")) {
                    totalDiscounts += discount.get("amount").floatValue();
                }
            }
            orderInfo.setOfferAmount(totalDiscounts);
            orderInfo.setCartLevelDiscount(new BigDecimal(String.valueOf(totalDiscounts)));
        }
        
        // Payment information
        if (order.has("cash_to_be_collected")) {
            JsonNode cashToCollect = order.get("cash_to_be_collected");
            if (cashToCollect != null && !cashToCollect.isNull()) {
                orderInfo.setAmountToBeCollected(cashToCollect.asInt());
            }
        }
        
        // Restaurant/outlet information - parse outlet_id for brand_id and kitchen_id
        if (order.has("outlet_id")) {
            String outletIdStr = order.get("outlet_id").asText();
            if (outletIdStr != null && !outletIdStr.isEmpty()) {
                parseOutletId(orderInfo, outletIdStr);
            }
        }
        
        // Keep restaurant_id handling as fallback
        if (order.has("restaurant_id") && orderInfo.getBrandId() == null) {
            orderInfo.setBrandId(order.get("restaurant_id").asInt());
        }
        

        
        // Set default values for required BigDecimal fields
        if (orderInfo.getPromoBalance() == null) {
            orderInfo.setPromoBalance(BigDecimal.ZERO);
        }
        if (orderInfo.getBankOffer() == null) {
            orderInfo.setBankOffer(BigDecimal.ZERO);
        }
        if (orderInfo.getPackagingCharges() == null) {
            orderInfo.setPackagingCharges(BigDecimal.ZERO);
        }
        if (orderInfo.getProductDiscount() == null) {
            orderInfo.setProductDiscount(BigDecimal.ZERO);
        }
        if (orderInfo.getCartLevelDiscount() == null) {
            orderInfo.setCartLevelDiscount(BigDecimal.ZERO);
        }
        if (orderInfo.getCgst() == null) {
            orderInfo.setCgst(BigDecimal.ZERO);
        }
        if (orderInfo.getSgst() == null) {
            orderInfo.setSgst(BigDecimal.ZERO);
        }
        orderInfo.setStatus(1);
        
        logger.info("Mapped Zomato order to OrderInfo: externalOrderId={}, grossAmount={}, netAmount={}, outletId={}", 
                    externalOrderId, orderInfo.getTotalAmount(), orderInfo.getFinalAmount(), orderInfo.getKitchenId());
        
        return orderInfo;
    }

    /**
     * Extract address information from customer_details JSON - similar to old project
     */
    private OrderAddress extractAddressFromJson(JsonNode customerDetails, String orderId) {
        OrderAddress address = new OrderAddress();
        
        try {
            if (customerDetails != null) {
                StringBuilder addressBuilder = new StringBuilder();
                String customerAddress = getJsonTextValue(customerDetails, "address");
                String deliveryArea = getJsonTextValue(customerDetails, "delivery_area");
                String city = getJsonTextValue(customerDetails, "city");
                
                if (customerAddress != null && !customerAddress.trim().isEmpty()) {
                    addressBuilder.append(customerAddress);
                }
                if (deliveryArea != null && !deliveryArea.trim().isEmpty()) {
                    if (addressBuilder.length() > 0) addressBuilder.append(" ");
                    addressBuilder.append(deliveryArea);
                }
                if (city != null && !city.trim().isEmpty()) {
                    if (addressBuilder.length() > 0) addressBuilder.append(" ");
                    addressBuilder.append(city);
                }
                
                address.setAddressLine1(addressBuilder.toString().trim());
                address.setFirstName(getJsonTextValue(customerDetails, "name"));
                address.setMobileNumber(getJsonTextValue(customerDetails, "phone_number"));
                address.setPincode(getJsonTextValue(customerDetails, "pincode"));
                address.setCity(city);
                address.setState(getJsonTextValue(customerDetails, "state"));
                address.setLat("0.0");
                address.setLon("0.0");
                
                address.setLastName(orderId);
            }
            
            logger.info("Extracted address: addressLine1={}, city={}, pincode={}, lat={}, lon={}", 
                       address.getAddressLine1(), address.getCity(), address.getPincode(), address.getLat(), address.getLon());
            
            return address;
            
        } catch (Exception e) {
            logger.warn("Error extracting address from JSON: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Parse outlet_id to extract brand_id and kitchen_id
     * If outlet_id is a single digit like "5", set brand_id=1 and kitchen_id=5
     * If outlet_id is in format "6_1", set brand_id=6 and kitchen_id=1
     */
    private void parseOutletId(OrderInfo orderInfo, String outletIdStr) {
        try {
            if (outletIdStr.contains("_")) {
                // Format like "6_1" - split by underscore
                String[] parts = outletIdStr.split("_");
                if (parts.length == 2) {
                    Integer brandId = Integer.parseInt(parts[0]);
                    Long kitchenId = Long.parseLong(parts[1]);
                    orderInfo.setBrandId(brandId);
                    orderInfo.setKitchenId(kitchenId);
                    logger.info("Parsed outlet_id '{}' -> brand_id={}, kitchen_id={}", outletIdStr, brandId, kitchenId);
                } else {
                    logger.warn("Invalid outlet_id format with underscore: {}", outletIdStr);
                }
            } else {
                // Single digit format like "5" - set brand_id=1 and kitchen_id=outlet_id
                Long kitchenId = Long.parseLong(outletIdStr);
                Integer brandId = 1;
                orderInfo.setBrandId(brandId);
                orderInfo.setKitchenId(kitchenId);
                logger.info("Parsed outlet_id '{}' -> brand_id={}, kitchen_id={}", outletIdStr, brandId, kitchenId);
            }
        } catch (NumberFormatException e) {
            logger.warn("Could not parse outlet_id '{}': {}", outletIdStr, e.getMessage());
        }
    }
    
    /**
     * Generate external order ID based on brand ID
     * brandId = 1 -> "fm" + orderId
     * brandId = 6 -> "GC" + orderId  
     * default -> "fm" + orderId
     */
    private String generateExternalOrderId(Integer brandId, Long orderId) {
        if (brandId != null && brandId == 6) {
            return "GC" + orderId;
        } else {
            // Default to "fm" prefix for brandId = 1 or any other value
            return "fm" + orderId;
        }
    }
    
    /**
     * Helper method to safely extract string values from JsonNode
     */
    private String getJsonTextValue(JsonNode node, String fieldName) {
        if (node != null && node.has(fieldName)) {
            return node.get(fieldName).asText();
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "failed");
        response.put("message", message);
        response.put("error_code", status.value());
        return ResponseEntity.status(status).body(response);
    }
}