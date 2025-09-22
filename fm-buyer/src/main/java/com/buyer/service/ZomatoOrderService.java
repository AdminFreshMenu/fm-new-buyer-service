package com.buyer.service;

import com.buyer.entity.OrderEnum.Channel;
import com.buyer.entity.OrderAddress;
import com.buyer.entity.OrderEnum.OrderAdditionalData;
import com.buyer.entity.OrderAdditionalDetails;
import com.buyer.entity.OrderInfo;
import com.buyer.entity.OrderItem;
import com.buyer.entity.OrderEnum.OrderItemType;
import com.buyer.entity.OrderUserInfo;
import com.buyer.dto.OrderAdditionalDetailsDto;
import com.buyer.entity.PaymentEntry;
import com.buyer.entity.PaymentEnum.PaymentFor;
import com.buyer.entity.PaymentEnum.PaymentGateway;
import com.buyer.entity.PaymentEnum.PaymentMethod;
import com.buyer.entity.PaymentEnum.PaymentMode;
import com.buyer.entity.PaymentEnum.PaymentStatus;
import com.buyer.dto.PaymentUserInfo;
import com.buyer.repository.OrderAddressRepository;
import com.buyer.repository.OrderAdditionalDetailsRepository;
import com.buyer.repository.OrderInfoRepository;
import com.buyer.repository.OrderItemRepository;
import com.buyer.delivery.repository.OrderRepository;
import com.buyer.delivery.entity.Order;
import com.buyer.repository.PaymentEntryRepository;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.util.StringUtils;

@Service
public class ZomatoOrderService {

    private static final Logger logger = LoggerFactory.getLogger(ZomatoOrderService.class);

    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @Autowired
    private OrderAddressRepository orderAddressRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderAdditionalDetailsRepository orderAdditionalDetailsRepository;

    @Autowired
    private OrderRepository orderRepository; // Delivery database

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentEntryRepository paymentEntryRepository;

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
            
            SaveOrderItems(order, savedOrder.getId());
            
            // Save additional order details
            saveOrderAdditionalDetails(order, savedOrder.getId(), savedOrder);
            
            // Save order in delivery database
            saveOrderInDeliveryDatabase(savedOrder);

            // Save payment entry in payment entry table
            savePaymentEntry(savedOrder);
            
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

    private void savePaymentEntry(OrderInfo orderInfo) {
        PaymentEntry paymentEntry = new PaymentEntry();

        paymentEntry.setOrderId(orderInfo.getId());
        paymentEntry.setAmount(
                orderInfo.getFinalAmount() != null
                        ? String.valueOf(orderInfo.getFinalAmount().intValue())
                        : "0"
        );

        // Set default payment for order
        paymentEntry.setPaymentFor(PaymentFor.ORDER);

        // Set brandId from order if available
        paymentEntry.setBrandId(orderInfo.getBrandId());

        // Populate user info if present
        if (orderInfo.getUser() != null) {
            PaymentUserInfo userInfo = new PaymentUserInfo();
            userInfo.setFirstName(orderInfo.getUser().getFirstName());
            userInfo.setLastName(orderInfo.getUser().getLastName());
            userInfo.setEmail(orderInfo.getUser().getEmail());
            userInfo.setMobileNumber(orderInfo.getUser().getMobileNumber());
            paymentEntry.setUser(userInfo);
        }

        // Set initial status
        paymentEntry.setStatus(PaymentStatus.DONE);

        // Optional: default payment gateway or method
        paymentEntry.setPaymentGateway(PaymentGateway.THIRD_PARTY);


        // Map channel to PaymentMethod
        if (orderInfo.getChannel() != null) {
            switch (orderInfo.getChannel()) {
                case ZOMATO -> paymentEntry.setPaymentMethod(PaymentMethod.ZOMATO);
                case SWIGGY -> paymentEntry.setPaymentMethod(PaymentMethod.SWIGGY);
                case BITSILA_ONDC -> paymentEntry.setPaymentMethod(PaymentMethod.BITSILA_ONDC);
                case MAGIC_PIN -> paymentEntry.setPaymentMethod(PaymentMethod.MAGIC_PIN);
                case RAPIDO_FOOD -> paymentEntry.setPaymentMethod(PaymentMethod.RAPIDO_FOOD);

                default -> paymentEntry.setPaymentMethod(PaymentMethod.ONLINE);
            }
        } else {
            paymentEntry.setPaymentMethod(PaymentMethod.ONLINE);
        }

        paymentEntry.setPaymentMode(PaymentMode.ONLINE);
        Map<String, String> data = new HashMap<>();
        data.put("channel", orderInfo.getChannel().name());
        paymentEntry.setData(data);

        // Generate unique 18-digit transaction ID
        long transactionId = System.currentTimeMillis() * 1000
                + ThreadLocalRandom.current().nextInt(0, 1000);
        paymentEntry.setTransactionId(String.valueOf(transactionId));

        // Save to DB
        paymentEntryRepository.save(paymentEntry);

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
        orderInfo.setOrderData("Buyer_v2");
        
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
        

        BigDecimal dishFinalCost = BigDecimal.ZERO;
        BigDecimal addonTotal = BigDecimal.ZERO;
        BigDecimal dishDiscount = BigDecimal.ZERO;
        BigDecimal packagingCharge = BigDecimal.ZERO;

        JsonNode dishes = order.get("dishes");
        if (dishes != null && dishes.isArray()) {
            for (JsonNode dish : dishes) {
                if (dish.has("final_cost")) {
                    dishFinalCost = dishFinalCost.add(dish.get("final_cost").decimalValue());
                }

                JsonNode composition = dish.get("composition");
                if (composition != null && composition.has("modifier_groups")) {
                    for (JsonNode group : composition.get("modifier_groups")) {
                        JsonNode variants = group.get("variants");
                        if (variants != null && variants.isArray()) {
                            for (JsonNode variant : variants) {
                                if (variant.has("total_cost")) {
                                    addonTotal = addonTotal.add(new BigDecimal(variant.get("total_cost").asText()));
                                }
                            }
                        }
                    }
                }


                // discounts
                JsonNode discounts = dish.get("dish_discounts");
                if (discounts != null && discounts.isArray()) {
                    for (JsonNode discount : discounts) {
                        if (discount.has("amount")) {
                            dishDiscount = dishDiscount.add(discount.get("amount").decimalValue());
                        }
                    }
                }

                // packaging charges
                JsonNode charges = dish.get("charges");
                if (charges != null && charges.isArray()) {
                    for (JsonNode charge : charges) {
                        if (charge.has("name")
                                && "Restaurant Packaging Charges".equalsIgnoreCase(charge.get("name").asText())) {
                            if (charge.has("amount")) {
                                packagingCharge = packagingCharge.add(charge.get("amount").decimalValue());
                            }
                        }
                    }
                }

            }
        }
        if (order.has("net_amount")) {
            orderInfo.setTotalAmount(dishFinalCost.floatValue()+ addonTotal.floatValue());
        }


        if (order.has("total_merchant")) {
            orderInfo.setFinalAmount(order.get("total_merchant").floatValue());
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

        orderInfo.setPackagingCharges(packagingCharge);
        orderInfo.setProductDiscount(dishDiscount);
        orderInfo.setOfferAmount(dishDiscount.floatValue());
        orderInfo.setOfferCode("ZOMATO_DISCOUNT");
        
        // Set default values for required BigDecimal fields
        if (orderInfo.getPromoBalance() == null) {
            orderInfo.setPromoBalance(BigDecimal.ZERO);
        }
        if (orderInfo.getBankOffer() == null) {
            orderInfo.setBankOffer(BigDecimal.ZERO);
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


    private String generateExternalOrderId(Integer brandId, Long orderId) {
        if (brandId == null) {
            return "fm" + orderId;
        }

        switch (brandId) {
            case 6:  return "GC"  + orderId;
            case 8:  return "EDF" + orderId;
            case 11: return "BS"  + orderId;
            case 12: return "DB"  + orderId;
            case 14: return "TC"  + orderId;
            case 17: return "PC"  + orderId;
            case 18: return "SS"  + orderId;
            case 19: return "CFX" + orderId;
            case 20: return "GG"  + orderId;
            default: return "fm"  + orderId;
        }
    }


    /**
     * Extract and save order items from the dish array in Zomato order JSON
     */
    private void SaveOrderItems(JsonNode order, Long orderId) {
        try {
            JsonNode dishes = order.get("dishes");
            if (dishes != null && dishes.isArray()) {
                logger.info("Processing {} dishes for orderId: {}", dishes.size(), orderId);
                
                for (JsonNode dish : dishes) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(orderId);
                    orderItem.setOrderItemType(OrderItemType.PRODUCT);
                    
                    // Product information
                    if (dish.has("dish_id")) {
                        orderItem.setProductId(dish.get("dish_id").asLong());
                    }
                    
                    // Quantity
                    if (dish.has("quantity")) {
                        orderItem.setQuantity(dish.get("quantity").asInt());
                    }
                    
                    // Price information from composition
                    if (dish.has("composition") && dish.get("composition").has("unit_cost")) {
                        int price = dish.get("composition").get("unit_cost").asInt();
                        orderItem.setSellingPrice(price);
                        orderItem.setMrp(price);
                        logger.debug("Set dish price: {} for dish_id: {}", price, orderItem.getProductId());
                    } else {
                        logger.warn("No unit_cost found in composition for dish_id: {}", dish.get("dish_id").asText());
                    }
                    
                    if (dish.has("total_cost")) {
                        orderItem.setTsp(dish.get("total_cost").asInt());
                    }
                    
                    if (dish.has("discount_amount")) {
                        orderItem.setDiscountAmount(new BigDecimal(dish.get("discount_amount").asText()));
                    } else {
                        orderItem.setDiscountAmount(BigDecimal.ZERO);
                    }
                    
                    if (dish.has("cashback_amount")) {
                        orderItem.setCashbackAmount(new BigDecimal(dish.get("cashback_amount").asText()));
                    } else {
                        orderItem.setCashbackAmount(BigDecimal.ZERO);
                    }
                    
                    if (dish.has("packaging_price")) {
                        orderItem.setPackagingPrice(new BigDecimal(dish.get("packaging_price").asText()));
                    } else {
                        orderItem.setPackagingPrice(BigDecimal.ZERO);
                    }
                    
                    if (dish.has("customer_discount")) {
                        orderItem.setcDisc(new BigDecimal(dish.get("customer_discount").asText()));
                    } else {
                        orderItem.setcDisc(BigDecimal.ZERO);
                    }
                    
                    OrderItem savedOrderItem = orderItemRepository.save(orderItem);
                    logger.debug("Saved order item with ID: {} for dish_id: {}, quantity: {}", 
                               savedOrderItem.getId(), orderItem.getProductId(), orderItem.getQuantity());
                    
                    // Check for addons in modifier_groups or direct addons/modifications
                    boolean hasModifierGroups = dish.has("composition") && 
                                              dish.get("composition").has("modifier_groups") &&
                                              dish.get("composition").get("modifier_groups").isArray() &&
                                              dish.get("composition").get("modifier_groups").size() > 0;
                    
                    if (hasModifierGroups || dish.has("addons") || dish.has("modifications")) {
                        processAddonsAndModifications(dish, orderId, savedOrderItem.getId());
                    }
                }
                
                logger.info("Successfully processed and saved order items for orderId: {}", orderId);
            } else {
                logger.info("No dishes found in order for orderId: {}", orderId);
            }
        } catch (Exception e) {
            logger.error("Error processing order items for orderId: {}", orderId, e);
        }
    }
    
    /**
     * Process addons and modifications for a dish
     */
    private void processAddonsAndModifications(JsonNode dish, Long orderId, Long parentOrderItemId) {
        try {
            // Handle addons from modifier_groups in composition
            JsonNode composition = dish.get("composition");
            if (composition != null && composition.has("modifier_groups")) {
                JsonNode modifierGroups = composition.get("modifier_groups");
                if (modifierGroups != null && modifierGroups.isArray()) {
                    for (JsonNode modifierGroup : modifierGroups) {
                        if (modifierGroup.has("variants")) {
                            JsonNode variants = modifierGroup.get("variants");
                            if (variants != null && variants.isArray()) {
                                for (JsonNode variant : variants) {
                                    OrderItem addonItem = new OrderItem();
                                    addonItem.setOrderId(orderId);
                                    addonItem.setParentOrderItemId(parentOrderItemId);
                                    addonItem.setOrderItemType(OrderItemType.ADDON);
                                    
                                    if (variant.has("variant_id")) {
                                        addonItem.setProductId(variant.get("variant_id").asLong());
                                    }
                                    if (variant.has("quantity")) {
                                        addonItem.setQuantity(variant.get("quantity").asInt());
                                    }
                                    if (variant.has("unit_cost")) {
                                        int price = variant.get("unit_cost").asInt();
                                        addonItem.setSellingPrice(price);
                                        addonItem.setMrp(price);
                                        addonItem.setTsp(price * addonItem.getQuantity());
                                        logger.debug("Set addon price: {} for variant_id: {}", price, variant.get("variant_id").asText());
                                    } else {
                                        logger.warn("No unit_cost found for addon variant_id: {}", variant.get("variant_id").asText());
                                    }
                                    
                                    addonItem.setDiscountAmount(BigDecimal.ZERO);
                                    addonItem.setCashbackAmount(BigDecimal.ZERO);
                                    addonItem.setPackagingPrice(BigDecimal.ZERO);
                                    addonItem.setcDisc(BigDecimal.ZERO);
                                    
                                    orderItemRepository.save(addonItem);
                                    logger.debug("Saved addon item from modifier_groups for parent order item: {}", parentOrderItemId);
                                }
                            }
                        }
                    }
                }
            }
            
            // Also handle direct addons array (for backward compatibility)
            JsonNode addons = dish.get("addons");
            if (addons != null && addons.isArray()) {
                for (JsonNode addon : addons) {
                    OrderItem addonItem = new OrderItem();
                    addonItem.setOrderId(orderId);
                    addonItem.setParentOrderItemId(parentOrderItemId);
                    addonItem.setOrderItemType(OrderItemType.ADDON);
                    
                    if (addon.has("addon_id")) {
                        addonItem.setProductId(addon.get("addon_id").asLong());
                    }
                    if (addon.has("quantity")) {
                        addonItem.setQuantity(addon.get("quantity").asInt());
                    }
                    if (addon.has("price")) {
                        int price = (int) (addon.get("price").asDouble() * 100);
                        addonItem.setSellingPrice(price);
                        addonItem.setMrp(price);
                        addonItem.setTsp(price * addonItem.getQuantity());
                    }
                    
                    addonItem.setDiscountAmount(BigDecimal.ZERO);
                    addonItem.setCashbackAmount(BigDecimal.ZERO);
                    addonItem.setPackagingPrice(BigDecimal.ZERO);
                    addonItem.setcDisc(BigDecimal.ZERO);
                    
                    orderItemRepository.save(addonItem);
                    logger.debug("Saved addon item from addons array for parent order item: {}", parentOrderItemId);
                }
            }
            
            JsonNode modifications = dish.get("modifications");
            if (modifications != null && modifications.isArray()) {
                for (JsonNode modification : modifications) {
                    OrderItem modificationItem = new OrderItem();
                    modificationItem.setOrderId(orderId);
                    modificationItem.setParentOrderItemId(parentOrderItemId);
                    modificationItem.setOrderItemType(OrderItemType.OTHER);
                    
                    if (modification.has("modification_id")) {
                        modificationItem.setProductId(modification.get("modification_id").asLong());
                    }
                    if (modification.has("quantity")) {
                        modificationItem.setQuantity(modification.get("quantity").asInt());
                    }
                    if (modification.has("price")) {
                        int price = (int) (modification.get("price").asDouble() * 100);
                        modificationItem.setSellingPrice(price);
                        modificationItem.setMrp(price);
                        modificationItem.setTsp(price * modificationItem.getQuantity());
                    }
                    
                    modificationItem.setDiscountAmount(BigDecimal.ZERO);
                    modificationItem.setCashbackAmount(BigDecimal.ZERO);
                    modificationItem.setPackagingPrice(BigDecimal.ZERO);
                    modificationItem.setcDisc(BigDecimal.ZERO);
                    
                    orderItemRepository.save(modificationItem);
                    logger.debug("Saved modification item for parent order item: {}", parentOrderItemId);
                }
            }
        } catch (Exception e) {
            logger.error("Error processing addons/modifications for parent order item: {}", parentOrderItemId, e);
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
    
    /**
     * Save additional order details from Zomato order JSON
     */
    private void saveOrderAdditionalDetails(JsonNode order, Long orderId, OrderInfo orderInfo) {
        try {
            List<OrderAdditionalDetailsDto> additionalDetailsDtos = new ArrayList<>();
            
            // Zomato Order ID
            if (order.has("order_id")) {
                additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ZOMATO_ORDER_ID,
                        order.get("order_id").asText()));
            }
            
            // Amount Balance
            if (order.has("amount_balance")) {
                additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ORDER_AMOUNT_BALANCE,
                        order.get("amount_balance").asText()));
            }
            
            // Amount Paid
            if (order.has("amount_paid")) {
                additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ORDER_AMOUNT_PAID,
                        order.get("amount_paid").asText()));
            }
            
            // Net Amount
            if (order.has("net_amount")) {
                additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ORDER_NET_AMOUNT,
                        order.get("net_amount").asText()));
            }
            
            // Gross Amount
            if (order.has("gross_amount")) {
                additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ORDER_GROSS_AMOUNT,
                        order.get("gross_amount").asText()));
            }
            
            // OTP
            if (order.has("otp")) {
                additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ZOMATO_OTP,
                        order.get("otp").asText()));
            }
            
            // Delivery Channel
            if (order.has("enable_delivery") && order.get("enable_delivery").asInt() == 0) {
                additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.DELIVERY_CHANNEL,
                        Channel.ZOMATO.name()));
            }

            // Order Instructions
            if (order.has("order_instructions") && StringUtils.hasText(order.get("order_instructions").asText())) {
                additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.NOTES,
                        order.get("order_instructions").asText()));
            }

            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.EXPECTED_KITCHEN_ID,
                    orderInfo.getKitchenId().toString()));
            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ORDER_DISCOUNT_AMOUNT,
                    orderInfo.getOfferAmount().toString()));

            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ORDER_OFFER_COUPON,
                    "salt"));

            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.IS_EXPRESS_CHECK_OUT,
                    "true"));

            // Save all additional details
            for (OrderAdditionalDetailsDto dto : additionalDetailsDtos) {
                OrderAdditionalDetails detail = new OrderAdditionalDetails();
                detail.setOrderId(dto.getOrderId());
                detail.setOrderKey(dto.getOrderKey());
                detail.setOrderKeyValue(dto.getOrderKeyValue());
                orderAdditionalDetailsRepository.save(detail);
            }
            
            logger.info("Saved {} additional details for orderId: {}", additionalDetailsDtos.size(), orderId);
            
        } catch (Exception e) {
            logger.error("Error saving additional order details for orderId: {}", orderId, e);
        }
    }
    
    /**
     * Create OrderAdditionalDetailsDto helper method
     */
    private OrderAdditionalDetailsDto createOrderAdditionalDetailsDto(Long orderId, OrderAdditionalData orderKey, String orderKeyValue) {
        return new OrderAdditionalDetailsDto(orderId, orderKey, orderKeyValue);
    }
    
    /**
     * Save order information in delivery database
     */
    private void saveOrderInDeliveryDatabase(OrderInfo orderInfo) {
        try {
            Order deliveryOrder = new Order();
            deliveryOrder.setOrderNumber(orderInfo.getId().intValue());
            deliveryOrder.setLatitude("0.0");
            deliveryOrder.setLongitude("0.0");
            if (orderInfo.getShippingAddress() != null) {
                deliveryOrder.setUserAddressId(orderInfo.getShippingAddress().getId());
            }

            deliveryOrder.setDeliveryPersonId(null);
            deliveryOrder.setOrderInvoiceId(null);
            deliveryOrder.setDeliveredAt(null);
            deliveryOrder.setStatus("new");
            deliveryOrder.setKitchenId(orderInfo.getKitchenId().intValue());
            deliveryOrder.setBrandId(orderInfo.getBrandId());

            deliveryOrder.setSource("fm");
            deliveryOrder.setDeliveryChannel(orderInfo.getChannel().toString());
            deliveryOrder.setTripOrderSeq(1L);
            deliveryOrder.setSearchKey(orderInfo.getId().toString() + "," + orderInfo.getShippingAddress().getLastName() +",");
            
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata")).withNano(0);
            deliveryOrder.setCreatedAt(now);
            deliveryOrder.setUpdatedAt(now);

            Order savedDeliveryOrder = orderRepository.save(deliveryOrder);
            logger.info("Saved order in delivery database with ID: {} for order number: {}", 
                       savedDeliveryOrder.getId(), deliveryOrder.getOrderNumber());
                       
        } catch (Exception e) {
            logger.error("Error saving order in delivery database for order ID: {}", orderInfo.getId(), e);
        }
    }
}
