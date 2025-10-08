package com.buyer.service;

import com.buyer.deliveryDB.entity.Order;
import com.buyer.deliveryDB.repository.OrderRepository;
import com.buyer.dto.OrderAdditionalDetailsDto;
import com.buyer.dto.PaymentUserInfo;
import com.buyer.dto.rapido.Customer;
import com.buyer.dto.rapido.ItemAddon;
import com.buyer.dto.rapido.LocationDTO;
import com.buyer.dto.rapido.OrderItem;
import com.buyer.dto.rapido.RapidoOrderCreate;
import com.buyer.entity.OrderAdditionalDetails;
import com.buyer.entity.OrderAddress;
import com.buyer.entity.OrderEnum.Channel;
import com.buyer.entity.OrderEnum.OrderAdditionalData;
import com.buyer.entity.OrderEnum.OrderItemType;
import com.buyer.entity.OrderInfo;
import com.buyer.entity.OrderUserInfo;
import com.buyer.entity.PaymentEntry;
import com.buyer.entity.PaymentEnum.PaymentFor;
import com.buyer.entity.PaymentEnum.PaymentGateway;
import com.buyer.entity.PaymentEnum.PaymentMethod;
import com.buyer.entity.PaymentEnum.PaymentMode;
import com.buyer.entity.PaymentEnum.PaymentStatus;
import com.buyer.repository.OrderAdditionalDetailsRepository;
import com.buyer.repository.OrderAddressRepository;
import com.buyer.repository.OrderInfoRepository;
import com.buyer.repository.OrderItemRepository;
import com.buyer.repository.PaymentEntryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
public class RapidoOrderService {

    private static final Logger logger = LoggerFactory.getLogger(RapidoOrderService.class);

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
    public ResponseEntity<Map<String, Object>> createOrder(RapidoOrderCreate orderRequest, Integer brandId) {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Rapido create_order service invoked at: {}", new Date());

            String rapidoOrderId = orderRequest.getOrderInfo().getOrderId();
            if (orderInfoRepository.findByUserLastNameAndChannel(rapidoOrderId, Channel.RAPIDO_FOOD).isPresent()) {
                logger.warn("Duplicate Rapido order received: {}", rapidoOrderId);
                response.put("status", "Success");
                response.put("message", "Order already exists");
                response.put("rapidoOrderId", rapidoOrderId);
                return ResponseEntity.ok(response);
            }

            OrderInfo orderInfo = mapRapidoOrderToOrderInfo(orderRequest, brandId);
            OrderInfo savedOrder = orderInfoRepository.save(orderInfo);

            String externalOrderId = generateExternalOrderId(savedOrder.getBrandId(), savedOrder.getId());
            savedOrder.setExternalOrderId(externalOrderId);
            savedOrder = orderInfoRepository.save(savedOrder);

            saveOrderAdditionalDetails(orderRequest, savedOrder.getId(), savedOrder);

            saveRapidoOrderItems(orderRequest.getItems(), savedOrder.getId());
            savePaymentEntry(savedOrder);
            saveOrderInDeliveryDatabase(savedOrder);

            logger.info("Rapido order created: internalId={}, rapidoOrderId={}, externalOrderId={}",
                    savedOrder.getId(), rapidoOrderId, externalOrderId);

            response.put("status", "200");
            response.put("message", "Order Successfully submitted");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error in Rapido create order service", e);
            return createErrorResponse(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private OrderInfo mapRapidoOrderToOrderInfo(RapidoOrderCreate orderRequest, Integer brandId) {
        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setExternalOrderId(orderRequest.getOrderInfo().getOrderId());
        orderInfo.setBrandId(brandId);
        orderInfo.setChannel(Channel.RAPIDO_FOOD);
        orderInfo.setTotalAmount((float) orderRequest.getOrderInfo().getSubTotal());
        orderInfo.setFinalAmount((float) orderRequest.getOrderInfo().getSubTotal());
        orderInfo.setStatus(1);
        orderInfo.setOrderData("Buyer_v2");
        orderInfo.setKitchenId(Long.parseLong(orderRequest.getOrderInfo().getRestId()));

        if (orderRequest.getCustomer() != null) {
            Customer customer = orderRequest.getCustomer();
            OrderUserInfo userInfo = new OrderUserInfo();
            userInfo.setFirstName(customer.getFirstName());
            userInfo.setLastName(orderRequest.getOrderInfo().getOrderId());
            userInfo.setEmail(customer.getFirstName() + "@" + customer.getLastName() + ".com");

            orderInfo.setUser(userInfo);

            if (customer.getLocation() != null) {
                LocationDTO loc = customer.getLocation();
                OrderAddress address = new OrderAddress();
                address.setFirstName(customer.getFirstName());
                address.setLastName(orderRequest.getOrderInfo().getOrderId());
                address.setAddressLine1(loc.getAddress());
                address.setPincode(loc.getPinCode());
                address.setLat(String.valueOf(loc.getLatitude()));
                address.setLon(String.valueOf(loc.getLongitude()));

                OrderAddress savedAddress = orderAddressRepository.save(address);
                orderInfo.setShippingAddress(savedAddress);
                orderInfo.setBillingAddress(savedAddress);
            }
        }

        return orderInfo;
    }

    private void saveRapidoOrderItems(List<OrderItem> items, Long orderId) {
        if (items == null || items.isEmpty()) return;

        for (OrderItem item : items) {
            com.buyer.entity.OrderItem orderItem = new com.buyer.entity.OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setOrderItemType(OrderItemType.PRODUCT);
            orderItem.setProductId(Long.parseLong(item.getItemId()));
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSellingPrice((int) item.getUnitPrice());
            orderItem.setMrp((int) item.getUnitPrice());
            orderItem.setDiscountAmount(BigDecimal.ZERO);
            orderItem.setCashbackAmount(BigDecimal.ZERO);
            orderItem.setPackagingPrice(BigDecimal.ZERO);
            orderItem.setcDisc(BigDecimal.ZERO);
            orderItemRepository.save(orderItem);

            List<ItemAddon> addOns = item.getAddOns();
            if (addOns != null && !addOns.isEmpty()) {
                for (ItemAddon addOn : addOns) {
                    com.buyer.entity.OrderItem orderItemAddon = new com.buyer.entity.OrderItem();
                    orderItemAddon.setOrderId(orderId);
                    orderItemAddon.setOrderItemType(OrderItemType.ADDON);
                    orderItemAddon.setProductId(Long.parseLong(addOn.getId()));
                    orderItemAddon.setQuantity(item.getQuantity());
                    orderItemAddon.setSellingPrice((int) addOn.getUnitPrice());
                    orderItemAddon.setMrp((int) addOn.getUnitPrice());
                    orderItemAddon.setDiscountAmount(BigDecimal.ZERO);
                    orderItemAddon.setCashbackAmount(BigDecimal.ZERO);
                    orderItemAddon.setPackagingPrice(BigDecimal.ZERO);
                    orderItemAddon.setcDisc(BigDecimal.ZERO);
                    orderItemRepository.save(orderItemAddon);
                }
            }
        }
    }

    private void savePaymentEntry(OrderInfo orderInfo) {
        PaymentEntry paymentEntry = new PaymentEntry();
        paymentEntry.setOrderId(orderInfo.getId());
        paymentEntry.setAmount(String.valueOf(orderInfo.getFinalAmount().intValue()));
        paymentEntry.setPaymentFor(PaymentFor.ORDER);
        paymentEntry.setBrandId(orderInfo.getBrandId());
        paymentEntry.setStatus(PaymentStatus.DONE);
        paymentEntry.setPaymentGateway(PaymentGateway.THIRD_PARTY);
        paymentEntry.setPaymentMethod(PaymentMethod.RAPIDO_FOOD);
        paymentEntry.setPaymentMode(PaymentMode.ONLINE);

        PaymentUserInfo paymentUserInfo = new PaymentUserInfo();
        paymentUserInfo.setEmail(orderInfo.getUser().getEmail());
        paymentUserInfo.setFirstName(orderInfo.getUser().getFirstName());
        paymentUserInfo.setLastName(orderInfo.getUser().getLastName());
        paymentUserInfo.setMobileNumber(orderInfo.getUser().getMobileNumber());
        paymentEntry.setUser(paymentUserInfo);

        Map<String, String> data = new HashMap<>();
        data.put("channel", orderInfo.getChannel().name());
        paymentEntry.setData(data);

        long transactionId = System.currentTimeMillis() * 1000 + ThreadLocalRandom.current().nextInt(0, 1000);
        paymentEntry.setTransactionId(String.valueOf(transactionId));

        paymentEntryRepository.save(paymentEntry);
    }

    private void saveOrderInDeliveryDatabase(OrderInfo orderInfo) {
        Order deliveryOrder = new Order();
        deliveryOrder.setOrderNumber(orderInfo.getId().intValue());
        deliveryOrder.setLatitude(orderInfo.getShippingAddress() != null ? orderInfo.getShippingAddress().getLat() : "0.0");
        deliveryOrder.setLongitude(orderInfo.getShippingAddress() != null ? orderInfo.getShippingAddress().getLon() : "0.0");
        deliveryOrder.setUserAddressId(orderInfo.getShippingAddress() != null ? orderInfo.getShippingAddress().getId() : null);
        deliveryOrder.setDeliveryPersonId(null);
        deliveryOrder.setOrderInvoiceId(null);
        deliveryOrder.setDeliveredAt(null);
        deliveryOrder.setStatus("new");
        deliveryOrder.setKitchenId(orderInfo.getKitchenId().intValue());
        deliveryOrder.setBrandId(orderInfo.getBrandId());
        deliveryOrder.setSource("fm");
        deliveryOrder.setDeliveryChannel(orderInfo.getChannel().toString());
        deliveryOrder.setTripOrderSeq(1L);
        deliveryOrder.setSearchKey(orderInfo.getId() + "," + orderInfo.getShippingAddress().getLastName() + ",");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata")).withNano(0);
        deliveryOrder.setCreatedAt(now);
        deliveryOrder.setUpdatedAt(now);

        orderRepository.save(deliveryOrder);
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
    private void saveOrderAdditionalDetails(RapidoOrderCreate orderRequest, Long orderId, OrderInfo orderInfo) {
        try {
            List<OrderAdditionalDetailsDto> additionalDetailsDtos = new ArrayList<>();

            // rapido Order ID
            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.RAPIDO_FOOD_ORDER_ID,
                    orderRequest.getOrderInfo().getOrderId()));

            // Amount Balance
            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ORDER_AMOUNT_BALANCE,
                    String.valueOf(orderRequest.getOrderInfo().getTotal())));


            // Amount Paid
            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ORDER_AMOUNT_PAID,
                    String.valueOf(orderRequest.getOrderInfo().getTotal())));


            // Delivery Channel
            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.DELIVERY_CHANNEL,
                    Channel.RAPIDO_FOOD.name()));


            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.EXPECTED_KITCHEN_ID,
                    orderInfo.getKitchenId().toString()));

//            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ORDER_DISCOUNT_AMOUNT,
//                    orderInfo.getOfferAmount().toString()));

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



}
