package com.buyer.service;

import com.buyer.dto.swiggy.SwiggyAddon;
import com.buyer.dto.swiggy.SwiggyOrderRequest;
import com.buyer.dto.swiggy.SwiggyItems;
import com.buyer.dto.swiggy.Variant;
import com.buyer.entity.MongoDB.MongoOrder;
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
import com.buyer.repository.MongoDB.OrdersRepository;
import com.buyer.repository.OrderAddressRepository;
import com.buyer.repository.OrderAdditionalDetailsRepository;
import com.buyer.repository.OrderInfoRepository;
import com.buyer.repository.OrderItemRepository;
import com.buyer.deliveryDB.repository.OrderRepository;
import com.buyer.deliveryDB.entity.Order;
import com.buyer.repository.PaymentEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.buyer.service.ZomatoOrderService.createOrderAdditionalDetailsDto;
import static com.buyer.service.ZomatoOrderService.generateExternalOrderId;

@Service
public class SwiggyOrderService {

    private static final Logger logger = LoggerFactory.getLogger(SwiggyOrderService.class);

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
    private PaymentEntryRepository paymentEntryRepository;

    @Autowired
    private ZomatoOrderService zomatoOrderService;

    @Autowired
    private OrdersRepository ordersRepository; // MongoDB repository

    @Transactional
    public ResponseEntity<Map<String, Object>> createOrder(SwiggyOrderRequest swiggyOrderRequest, String merchantId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("Swiggy create order request received at: {} for merchantId: {}, orderId: {}", 
                       new Date(), merchantId, swiggyOrderRequest.getOrderId());
            
            String swiggyOrderId = String.valueOf(swiggyOrderRequest.getOrderId());

            // Check for duplicate order using lastName (which contains Swiggy order ID)
            if (orderInfoRepository.findByUserLastNameAndChannel(swiggyOrderId, Channel.SWIGGY).isPresent()) {
                logger.warn("Duplicate order request received for Swiggy orderId: {}", swiggyOrderId);
                response.put("status", "Success");
                response.put("message", "Order already exists");
                response.put("swiggyOrderId", swiggyOrderId);
                return ResponseEntity.ok(response);
            }

            // Create OrderInfo from Swiggy request
            OrderInfo orderInfo = mapSwiggyOrderToOrderInfo(swiggyOrderRequest, merchantId);

            // Save to database first with original Swiggy order ID
            OrderInfo savedOrder = orderInfoRepository.save(orderInfo);
            
            // Generate custom external order ID based on brand ID
            String customExternalOrderId = generateExternalOrderId(savedOrder.getBrandId(), savedOrder.getId());

            // Update the external order ID
            savedOrder.setExternalOrderId(customExternalOrderId);
            savedOrder = orderInfoRepository.save(savedOrder);
            
            // Save order items
            saveOrderItems(swiggyOrderRequest.getItems(), savedOrder.getId());
            
            // Save additional order details
             List<OrderAdditionalDetailsDto> orderAdditionalDetailsDtos = saveOrderAdditionalDetails(swiggyOrderRequest, savedOrder.getId(), savedOrder);
            
            // Save order in delivery database
            saveOrderInDeliveryDatabase(savedOrder);

            // Save payment entry in payment entry table
            PaymentEntry paymentEntry = savePaymentEntry(savedOrder);

            // save data to mongoDb


            MongoOrder mongoOrder = zomatoOrderService.mapOrderInfoToMongoOrders(orderInfo,orderAdditionalDetailsDtos,paymentEntry);
            if (mongoOrder != null) {
                ordersRepository.save(mongoOrder);
            } else {
                logger.warn("MongoDB order mapping returned null for orderId: {}, skipping MongoDB save", savedOrder.getId());
            }


                logger.info("Order created successfully with ID: {} for Swiggy orderId: {}, Custom externalOrderId: {}",
                       savedOrder.getId(), swiggyOrderId, customExternalOrderId);

            response.put("status", "Success");
            response.put("message", "Order created successfully");
            response.put("externalOrderId", savedOrder.getExternalOrderId());
            response.put("internalOrderId", savedOrder.getId());
            response.put("swiggyOrderId", swiggyOrderId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing Swiggy order creation", e);
            return createErrorResponse("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Map SwiggyOrderRequest to OrderInfo entity
     */
    private OrderInfo mapSwiggyOrderToOrderInfo(SwiggyOrderRequest request, String merchantId) {
        OrderInfo orderInfo = new OrderInfo();
        
        // Basic order information
        String externalOrderId = String.valueOf(request.getOrderId());
        // need to change
        orderInfo.setExternalOrderId(externalOrderId);
        orderInfo.setChannel(Channel.SWIGGY);
        orderInfo.setOrderData("Buyer_v2");
        
        // Set delivery time (default 40 minutes)
        orderInfo.setDeliveryTimeInMinutes("40");

        // Customer information - map from Swiggy request
        OrderUserInfo userInfo = new OrderUserInfo();
        userInfo.setFirstName(request.getCustomerName());
        userInfo.setLastName(externalOrderId);
        userInfo.setMobileNumber(request.getCustomerPhone());
        userInfo.setEmail("swiggy@swiggy.com");
        orderInfo.setUser(userInfo);
        
        // Address information
        OrderAddress deliveryAddress = createAddressFromSwiggyRequest(request, externalOrderId);
        if (deliveryAddress != null) {
            OrderAddress savedDeliveryAddress = orderAddressRepository.save(deliveryAddress);
            orderInfo.setShippingAddress(savedDeliveryAddress);
            orderInfo.setBillingAddress(savedDeliveryAddress);
        }

        // Financial information
        if (request.getRestaurantGrossBill() != null) {
            orderInfo.setTotalAmount(request.getRestaurantGrossBill());
        }
        if (request.getRestaurantGrossBill() != null) {
            orderInfo.setFinalAmount(request.getRestaurantGrossBill());
        }

        // Packaging charges
        if (request.getOrderPackingCharges() != null) {
            orderInfo.setPackagingCharges(BigDecimal.valueOf(request.getOrderPackingCharges()));
        }

        // Restaurant service charges as shipping charges
        if (request.getRestaurantServiceCharges() != null) {
            orderInfo.setShippingCharges(request.getRestaurantServiceCharges());
        }

        // Discount information
        if (request.getRestaurantDiscount() != null) {
            orderInfo.setOfferAmount(request.getRestaurantDiscount());
            orderInfo.setProductDiscount(BigDecimal.valueOf(request.getRestaurantDiscount()));
        }

        // Payment information - check if COD
        if ("COD".equalsIgnoreCase(request.getPaymentType()) && request.getRestaurantGrossBill() != null) {
            orderInfo.setAmountToBeCollected(request.getRestaurantGrossBill().intValue());
        }
        
        // Restaurant/outlet information
        if (request.getOutletId() != null) {
            parseOutletId(orderInfo, request.getOutletId());
        }

        // Tax information
        if (request.getCartCgst() != null) {
            orderInfo.setCgst(BigDecimal.valueOf(request.getCartCgst()));
        }
        if (request.getCartSgst() != null) {
            orderInfo.setSgst(BigDecimal.valueOf(request.getCartSgst()));
        }

        // Set default values for required BigDecimal fields
        setDefaultBigDecimalFields(orderInfo);
        
        orderInfo.setStatus(1);
        float totalAmount = 0;
        for ( SwiggyItems item : request.getItems()) {
            totalAmount += item.getSubtotal();
        }
        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setFinalAmount(totalAmount - request.getRestaurantDiscount() + request.getOrderPackingCharges());
        orderInfo.setAmountToBeCollected(0);
        
        logger.info("Mapped Swiggy order to OrderInfo: externalOrderId={}, grossAmount={}, finalAmount={}, outletId={}", 
                    externalOrderId, orderInfo.getTotalAmount(), orderInfo.getFinalAmount(), orderInfo.getKitchenId());
        
        return orderInfo;
    }

    /**
     * Create OrderAddress from Swiggy request
     */
    private OrderAddress createAddressFromSwiggyRequest(SwiggyOrderRequest request, String orderId) {
        OrderAddress address = new OrderAddress();
        
        try {
            StringBuilder addressBuilder = new StringBuilder();
            
            if (StringUtils.hasText(request.getCustomerAddress())) {
                addressBuilder.append(request.getCustomerAddress());
            }
            if (StringUtils.hasText(request.getCustomerArea())) {
                if (addressBuilder.length() > 0) addressBuilder.append(" ");
                addressBuilder.append(request.getCustomerArea());
            }
            if (StringUtils.hasText(request.getCustomerCity())) {
                if (addressBuilder.length() > 0) addressBuilder.append(" ");
                addressBuilder.append(request.getCustomerCity());
            }
            
            address.setAddressLine1(addressBuilder.toString().trim());
            address.setFirstName(request.getCustomerName());
            address.setMobileNumber(request.getCustomerPhone());
            address.setCity(request.getCustomerCity());
            // Note: Swiggy doesn't provide pincode/state in this structure
            address.setLat("0.0");
            address.setLon("0.0");
            address.setLastName(orderId);
            
            logger.info("Created address: addressLine1={}, city={}, customerName={}", 
                       address.getAddressLine1(), address.getCity(), address.getFirstName());
            
            return address;
            
        } catch (Exception e) {
            logger.warn("Error creating address from Swiggy request: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Parse outlet ID to extract brand_id and kitchen_id
     */
    public static void parseOutletId(OrderInfo orderInfo, String outletIdStr) {
        try {
            if (outletIdStr.contains("_")) {
                // Format like "6_1" - split by underscore
                String[] parts = outletIdStr.split("_");
                if (parts.length == 2) {
                    Integer brandId = Integer.parseInt(parts[0]);
                    Long kitchenId = Long.parseLong(parts[1]);
                    orderInfo.setBrandId(brandId);
                    orderInfo.setKitchenId(kitchenId);
                    logger.info("Parsed outletId '{}' -> brand_id={}, kitchen_id={}", outletIdStr, brandId, kitchenId);
                } else {
                    logger.warn("Invalid outletId format with underscore: {}", outletIdStr);
                }
            } else {
                // Single digit format like "5" - set brand_id=1 and kitchen_id=outletId
                Long kitchenId = Long.parseLong(outletIdStr);
                Integer brandId = 1;
                orderInfo.setBrandId(brandId);
                orderInfo.setKitchenId(kitchenId);
                logger.info("Parsed outletId '{}' -> brand_id={}, kitchen_id={}", outletIdStr, brandId, kitchenId);
            }
        } catch (NumberFormatException e) {
            logger.warn("Could not parse outletId '{}': {}", outletIdStr, e.getMessage());
        }
    }


    /**
     * Save order items from Swiggy request
     */
    private void saveOrderItems(List<SwiggyItems> items, Long orderId) {
        try {
            if (items != null && !items.isEmpty()) {
                logger.info("Processing {} items for orderId: {}", items.size(), orderId);
                
                for (SwiggyItems item : items) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(orderId);
                    orderItem.setOrderItemType(OrderItemType.PRODUCT);
                    
                    // Product information
                    if (item.getId() != null) {
                        orderItem.setProductId(Long.valueOf(item.getId()));
                    }
                    
                    // Quantity
                    if (item.getQuantity() != null) {
                        orderItem.setQuantity(item.getQuantity());
                    }
                    
                    // Price information
                    if (item.getPrice() != null) {
                        orderItem.setSellingPrice(item.getPrice().intValue());
                        orderItem.setMrp(item.getPrice().intValue());
                    }
                    
                    if (item.getPrice() != null) {
                        orderItem.setTsp(item.getPrice().intValue());
                    }

                    
                    // Packing charges
                    if (item.getPackingCharges() != null) {
                        orderItem.setPackagingPrice(BigDecimal.valueOf(item.getPackingCharges()));
                    } else {
                        orderItem.setPackagingPrice(BigDecimal.ZERO);
                    }
                    
                    // Set default values for other BigDecimal fields
                    setDefaultItemBigDecimalFields(orderItem);
                    
                    OrderItem savedOrderItem = orderItemRepository.save(orderItem);
                    logger.debug("Saved order item with ID: {} for item_id: {}, quantity: {}", 
                               savedOrderItem.getId(), orderItem.getProductId(), orderItem.getQuantity());
                    
                    // Process addons if present
                    if (item.getAddons() != null && !item.getAddons().isEmpty()) {
                        processAddOns(item.getAddons(), orderId, savedOrderItem.getId());
                    }

                }
                
                logger.info("Successfully processed and saved order items for orderId: {}", orderId);
            } else {
                logger.info("No items found in order for orderId: {}", orderId);
            }
        } catch (Exception e) {
            logger.error("Error processing order items for orderId: {}", orderId, e);
        }
    }

    /**
     * Process add-ons for an item (placeholder - implement based on your SwiggyAddOn structure)
     */
    private void processAddOns(List<SwiggyAddon> addOns, Long orderId, Long parentOrderItemId) {
        try {
            if (addOns != null && !addOns.isEmpty()) {
                logger.info("Processing {} add-ons for parent orderItemId: {}", addOns.size(), parentOrderItemId);

                for (SwiggyAddon addOn : addOns) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(orderId);
                    orderItem.setOrderItemType(OrderItemType.ADDON);
                    orderItem.setParentOrderItemId(parentOrderItemId);

                    // Add-on product info
                    if (addOn.getId() != null) {
                        orderItem.setProductId(Long.valueOf(addOn.getId()));
                    }

                    // Quantity
                    if (addOn.getQuantity() != null) {
                        orderItem.setQuantity(addOn.getQuantity());
                    } else {
                        orderItem.setQuantity(1); // default to 1 if not provided
                    }

                    // Price info
                    if (addOn.getPrice() != null) {
                        orderItem.setSellingPrice(addOn.getPrice().intValue());
                        orderItem.setMrp(addOn.getPrice().intValue());
                        orderItem.setTsp(addOn.getPrice().intValue());
                    }

                    // Default BigDecimal fields
                    setDefaultItemBigDecimalFields(orderItem);

                    OrderItem savedAddOn = orderItemRepository.save(orderItem);
                    logger.debug("Saved add-on item with ID: {} for parent orderItemId: {}, addon_id: {}",
                            savedAddOn.getId(), parentOrderItemId, orderItem.getProductId());
                }

                logger.info("Successfully processed and saved add-ons for parent orderItemId: {}", parentOrderItemId);
            }
        } catch (Exception e) {
            logger.error("Error processing add-ons for parent order item: {}", parentOrderItemId, e);
        }
    }


    /**
     * Save payment entry
     */
    private PaymentEntry savePaymentEntry(OrderInfo orderInfo) {
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

        // Payment gateway and method for Swiggy
        paymentEntry.setPaymentGateway(PaymentGateway.THIRD_PARTY);
        paymentEntry.setPaymentMethod(PaymentMethod.SWIGGY);
        paymentEntry.setPaymentMode(PaymentMode.ONLINE);
        
        Map<String, String> data = new HashMap<>();
        data.put("channel", orderInfo.getChannel().name());
        paymentEntry.setData(data);

        // Generate unique 18-digit transaction ID
        long transactionId = System.currentTimeMillis() * 1000
                + ThreadLocalRandom.current().nextInt(0, 1000);
        paymentEntry.setTransactionId(String.valueOf(transactionId));

        // Save to DB
        com.buyer.entity.PaymentEntry paymentEntry1 =  paymentEntryRepository.save(paymentEntry);
        logger.info("Saved payment entry for orderId: {}", orderInfo.getId());

        return paymentEntry1;
    }

    /**
     * Save additional order details from Swiggy request
     */
    private  List<OrderAdditionalDetailsDto> saveOrderAdditionalDetails(SwiggyOrderRequest request, Long orderId, OrderInfo orderInfo) {
        try {
            List<OrderAdditionalDetailsDto> additionalDetailsDtos = new ArrayList<>();
            
            // Swiggy Order ID
            if (request.getOrderId() != null) {
                additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.SWIGGY_ORDER_ID,
                        String.valueOf(request.getOrderId())));
            }
            
            // order type
            if (request.getRestaurantGrossBill() != null) {
                additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.SWIGGY_ORDER_TYPE,
                        request.getOrderType()));
            }

            // Instructions
            if (StringUtils.hasText(request.getInstructions())) {
                additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.NOTES,
                        request.getInstructions()));
            }

            if(request.getRewardType() !=null){
                additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.SWIGGY_REWARD_TYPE,
                        request.getRewardType()));
            }

            if (request.getCutleryOptedIn() != null) {
                OrderAdditionalDetailsDto additionalDetailsDto1 = new OrderAdditionalDetailsDto();
                additionalDetailsDto1.setOrderId(orderId);
                additionalDetailsDto1.setOrderKey(OrderAdditionalData.SWIGGY_CUTLERY_OPTED_IN);
                if (request.getCutleryOptedIn().equals(Boolean.TRUE)) {
                    additionalDetailsDto1.setOrderKeyValue("Send Plates, Napkins, Spoons");
                } else {
                    additionalDetailsDto1.setOrderKeyValue("DONT Send Cutlery");
                }
                additionalDetailsDtos.add(additionalDetailsDto1);
            }

            // Add standard details
            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.EXPECTED_KITCHEN_ID,
                    orderInfo.getKitchenId().toString()));

            // otp
            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.SWIGGY_OTP,
                    request.getCustomerId()));

            // packaging charge
            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.SWIGGY_ORDER_PACKAGING_CHARGE,
                    request.getOrderPackingCharges().toString()));

            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ORDER_DISCOUNT_AMOUNT,
                    request.getRestaurantDiscount().toString()));
            
            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.DELIVERY_CHANNEL,
                    Channel.SWIGGY.name()));

            if (!CollectionUtils.isEmpty(request.getItems())) {
                List<Map<String, List<String>>> productVariants = new ArrayList<>();
                for (SwiggyItems swiggyItems : request.getItems()) {
                    if (!CollectionUtils.isEmpty(swiggyItems.getVariants())) {

                        Map<String, List<String>> productVariant = new HashMap<>();
                        productVariant.put(swiggyItems.getId(), new ArrayList<>());
                        for (Variant variant : swiggyItems.getVariants()) {
                            productVariant.get(swiggyItems.getId()).add(variant.getName());
                        }
                        productVariants.add(productVariant);
                    }
                    additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId,OrderAdditionalData.PRODUCT_VARIANT_NAME,
                            (productVariants.toString())));

                }
            }

            // Save all additional details
            for (OrderAdditionalDetailsDto dto : additionalDetailsDtos) {
                OrderAdditionalDetails detail = new OrderAdditionalDetails();
                detail.setOrderId(dto.getOrderId());
                detail.setOrderKey(dto.getOrderKey());
                detail.setOrderKeyValue(dto.getOrderKeyValue());
                orderAdditionalDetailsRepository.save(detail);
            }
            
            logger.info("Saved {} additional details for orderId: {}", additionalDetailsDtos.size(), orderId);
            return additionalDetailsDtos;

        } catch (Exception e) {
            logger.error("Error saving additional order details for orderId: {}", orderId, e);
        }
         return null;
    }


    /**
     * Save order in delivery database
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

    /**
     * Set default values for BigDecimal fields in OrderInfo
     */
    private void setDefaultBigDecimalFields(OrderInfo orderInfo) {
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
        if (orderInfo.getPackagingCharges() == null) {
            orderInfo.setPackagingCharges(BigDecimal.ZERO);
        }
    }

    /**
     * Set default values for BigDecimal fields in OrderItem
     */
    private void setDefaultItemBigDecimalFields(OrderItem orderItem) {
        if (orderItem.getDiscountAmount() == null) {
            orderItem.setDiscountAmount(BigDecimal.ZERO);
        }
        if (orderItem.getCashbackAmount() == null) {
            orderItem.setCashbackAmount(BigDecimal.ZERO);
        }
        if (orderItem.getPackagingPrice() == null) {
            orderItem.setPackagingPrice(BigDecimal.ZERO);
        }
        if (orderItem.getcDisc() == null) {
            orderItem.setcDisc(BigDecimal.ZERO);
        }
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "failed");
        response.put("message", message);
        response.put("error_code", status.value());
        return ResponseEntity.status(status).body(response);
    }
}