package com.buyer.service;

import com.buyer.deliveryDB.entity.Order;
import com.buyer.deliveryDB.repository.OrderRepository;
import com.buyer.dto.OrderAdditionalDetailsDto;
import com.buyer.dto.PaymentUserInfo;
import com.buyer.dto.bitsila.ChargesBreakupDTO;
import com.buyer.dto.bitsila.OrderItemDTO;
import com.buyer.dto.bitsila.OrderRequest;
import com.buyer.dto.bitsila.Response;
import com.buyer.entity.MongoDB.MongoOrder;
import com.buyer.entity.OrderAdditionalDetails;
import com.buyer.entity.OrderAddress;
import com.buyer.entity.OrderInfo;
import com.buyer.entity.OrderUserInfo;
import com.buyer.entity.PaymentEntry;
import com.buyer.entity.OrderEnum.Channel;
import com.buyer.entity.OrderEnum.OrderAdditionalData;
import com.buyer.entity.OrderEnum.OrderItemType;
import com.buyer.entity.PaymentEnum.PaymentFor;
import com.buyer.entity.PaymentEnum.PaymentGateway;
import com.buyer.entity.PaymentEnum.PaymentMethod;
import com.buyer.entity.PaymentEnum.PaymentMode;
import com.buyer.entity.PaymentEnum.PaymentStatus;
import com.buyer.repository.MongoDB.OrdersRepository;
import com.buyer.repository.OrderAdditionalDetailsRepository;
import com.buyer.repository.OrderAddressRepository;
import com.buyer.repository.OrderInfoRepository;
import com.buyer.repository.OrderItemRepository;
import com.buyer.repository.PaymentEntryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.buyer.service.SwiggyOrderService.parseOutletId;
import static com.buyer.service.ZomatoOrderService.SetSourceByBrandId;
import static com.buyer.service.ZomatoOrderService.createOrderAdditionalDetailsDto;
import static com.buyer.service.ZomatoOrderService.generateExternalOrderId;

@Service
public class BitsilaServices {
    private static final Logger logger = LoggerFactory.getLogger(BitsilaServices.class);

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

    @Autowired
    private ZomatoOrderService zomatoOrderService;

    @Autowired
    private OrdersRepository ordersRepository;

    @Transactional
    public ResponseEntity<Response> createOrder(String authKey, OrderRequest orderRequest) {
        try {
            logger.info("Bitsila create_order service invoked at: {}", new java.util.Date());

            String bitsilaOrderId = orderRequest.getOrder().getOrderNo();

            if (bitsilaOrderId == null) {
                logger.warn("No order id found in Bitsila order: {}", bitsilaOrderId);
                Response response = new Response("No order id found", bitsilaOrderId, "422", false);
                return ResponseEntity.unprocessableEntity().body(response);
            }

            if (orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
                logger.warn("No items found in Bitsila order: {}", bitsilaOrderId);
                Response response = new Response("No items found", bitsilaOrderId, "422", false);
                return ResponseEntity.unprocessableEntity().body(response);
            }

            if (orderRequest.getOrderItems() != null && !orderRequest.getOrderItems().isEmpty()) {
                for (OrderItemDTO item : orderRequest.getOrderItems()) {
                    if (item.getQuantity() == null || item.getQuantity() <= 0) {
                        logger.warn("Invalid quantity found in Bitsila order: {}", bitsilaOrderId);
                        Response response = new Response("Invalid quantity found", bitsilaOrderId, "422", false);
                        return ResponseEntity.unprocessableEntity().body(response);
                    }
                }

                if (orderRequest.getOrder().getTotalAmount() == 0.0) {
                    logger.warn("Invalid amount found in Bitsila order: {}", bitsilaOrderId);
                    Response response = new Response("Invalid amount found", bitsilaOrderId, "422", false);
                    return ResponseEntity.unprocessableEntity().body(response);
                }
            }

            if (orderInfoRepository.findByUserLastNameAndChannel(bitsilaOrderId, Channel.BITSILA_ONDC).isPresent()) {
                logger.warn("Duplicate Bitsila order received: {}", bitsilaOrderId);
                Response response = new Response("Order already exists", bitsilaOrderId, "200", true);
                return ResponseEntity.ok(response);
            }


            OrderInfo orderInfo = mapBitsilaOrderToOrderInfo(orderRequest);
            OrderInfo savedOrder = orderInfoRepository.save(orderInfo);

            String externalOrderId = generateExternalOrderId(savedOrder.getBrandId(), savedOrder.getId());
            savedOrder.setExternalOrderId(externalOrderId);
            savedOrder = orderInfoRepository.save(savedOrder);

            List<OrderAdditionalDetailsDto> orderAdditionalDetailsDtos = saveOrderAdditionalDetails(orderRequest, savedOrder.getId(), savedOrder);
            saveBitsilaOrderItems(orderRequest.getOrderItems(), savedOrder.getId(), savedOrder.getBrandId());
            PaymentEntry paymentEntry = savePaymentEntry(savedOrder);
            saveOrderInDeliveryDatabase(savedOrder);

            MongoOrder mongoOrder = zomatoOrderService.mapOrderInfoToMongoOrders(orderInfo,orderAdditionalDetailsDtos,paymentEntry);
            if (mongoOrder != null) {
                ordersRepository.save(mongoOrder);
            } else {
                logger.warn("MongoDB order mapping returned null for orderId: {}, skipping MongoDB save", savedOrder.getId());
            }

            logger.info("Bitsila order created: internalId={}, bitsilaOrderId={}, externalOrderId={}",
                    savedOrder.getId(), bitsilaOrderId, externalOrderId);

            Response response = new Response("Order Successfully submitted", externalOrderId, "200", true);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error in Bitsila create order service", e);
            Response errorResponse = new Response(e.getMessage(), null, "422", false);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
        }
    }

    private OrderInfo mapBitsilaOrderToOrderInfo(OrderRequest orderRequest) {
        OrderInfo orderInfo = new OrderInfo();

        parseOutletId(orderInfo, orderRequest.getOrder().getOutletRefId());
        orderInfo.setChannel(Channel.BITSILA_ONDC);

        orderInfo.setTotalAmount(orderRequest.getOrder().getSubTotal().floatValue());
        orderInfo.setAmountToBeCollected(0);

        int offeramount = 0;
        double totalPackagingCharge = 0;
        double amount = 0;
        for (OrderItemDTO itemDTO : orderRequest.getOrderItems()) {
            offeramount += itemDTO.getTotalDiscountAmount();
            amount += itemDTO.getPrice();
            if (!CollectionUtils.isEmpty(itemDTO.getChargesBreakup())) {
                for (ChargesBreakupDTO charge : itemDTO.getChargesBreakup()) {
                    if (charge.getName().equalsIgnoreCase("packaging_charges"))
                        totalPackagingCharge += (charge.getAmount() != null) ? charge.getAmount() : 0;
                }
                orderInfo.setPackagingCharges(new BigDecimal(totalPackagingCharge));
            }
        }

        orderInfo.setOfferAmount((float) offeramount);
        orderInfo.setFinalAmount((float) amount + (float) totalPackagingCharge);

        orderInfo.setOfferCode("BITSILA_DISCOUNT");
        orderInfo.setStatus(1);
        orderInfo.setOrderData("Buyer_v2");
        OrderUserInfo userInfo = new OrderUserInfo();
        userInfo.setFirstName(orderRequest.getCustomer().getName());
        userInfo.setLastName(orderRequest.getOrder().getOrderNo());
        userInfo.setMobileNumber(orderRequest.getCustomer().getPhoneNumber());
        userInfo.setEmail(orderRequest.getCustomer().getEmail());
        orderInfo.setUser(userInfo);

        if (orderRequest.getCustomer().getAddress() != null) {
            com.buyer.dto.bitsila.AddressDTO shippingAddr = orderRequest.getCustomer().getAddress();
            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getCustomer().getName());
            address.setLastName(orderRequest.getOrder().getOrderNo());
            address.setAddressLine1(shippingAddr.getAddress1());
            address.setAddressLine2(shippingAddr.getLandmark());
            address.setCity(shippingAddr.getCity());
            address.setState(shippingAddr.getCountry());
            address.setPincode(shippingAddr.getPincode());
            address.setMobileNumber(orderRequest.getCustomer().getPhoneNumber());
            address.setLat(String.valueOf(shippingAddr.getLatitude()));
            address.setLon(String.valueOf(shippingAddr.getLongitude()));

            OrderAddress savedAddress = orderAddressRepository.save(address);
            orderInfo.setShippingAddress(savedAddress);
            orderInfo.setBillingAddress(savedAddress);
        }

        return orderInfo;
    }

    private void saveBitsilaOrderItems(List<OrderItemDTO> items, Long orderId, int brandId) {
        if (items == null || items.isEmpty()) return;

        for (OrderItemDTO item : items) {
            com.buyer.entity.OrderItem orderItem = new com.buyer.entity.OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setOrderItemType(OrderItemType.PRODUCT);

            if (brandId == 1) {
                orderItem.setProductId(Long.valueOf(item.getRefId()));
            } else {
                String[] refId = (item.getRefId()).split("_");
                orderItem.setProductId(Long.parseLong(refId[0]));
            }


            orderItem.setQuantity(item.getQuantity());
            orderItem.setSellingPrice(item.getPrice());
            orderItem.setMrp(item.getPrice());
            orderItem.setDiscountAmount(BigDecimal.ZERO);
            orderItem.setPackagingPrice(BigDecimal.valueOf(item.getCharges()));
            orderItem.setCashbackAmount(BigDecimal.ZERO);
            orderItem.setcDisc(BigDecimal.ZERO);
            orderItem.setTsp(item.getPrice());
            orderItemRepository.save(orderItem);
        }
    }

    private PaymentEntry savePaymentEntry(OrderInfo orderInfo) {
        PaymentEntry paymentEntry = new PaymentEntry();
        paymentEntry.setOrderId(orderInfo.getId());
        paymentEntry.setAmount(String.valueOf(orderInfo.getFinalAmount().intValue()));
        paymentEntry.setPaymentFor(PaymentFor.ORDER);
        paymentEntry.setBrandId(orderInfo.getBrandId());
        paymentEntry.setStatus(PaymentStatus.DONE);
        paymentEntry.setPaymentGateway(PaymentGateway.THIRD_PARTY);
        paymentEntry.setPaymentMethod(PaymentMethod.BITSILA_ONDC);
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

        PaymentEntry paymentEntry1 = paymentEntryRepository.save(paymentEntry);
        return paymentEntry1;
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
        deliveryOrder.setSource(SetSourceByBrandId(orderInfo.getBrandId()));
        deliveryOrder.setDeliveryChannel(orderInfo.getChannel().toString());
        deliveryOrder.setTripOrderSeq(1L);
        deliveryOrder.setSearchKey(orderInfo.getId() + "," + orderInfo.getShippingAddress().getLastName() + ",");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata")).withNano(0);
        deliveryOrder.setCreatedAt(now);
        deliveryOrder.setUpdatedAt(now);

        orderRepository.save(deliveryOrder);
    }

    private  List<OrderAdditionalDetailsDto> saveOrderAdditionalDetails(OrderRequest orderRequest, Long orderId, OrderInfo orderInfo) {
        List<OrderAdditionalDetailsDto> additionalDetailsDtos = new ArrayList<>();

        try {

            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.BITSILA_ORDER_ID,
                    orderRequest.getOrder().getOrderNo()));

            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ORDER_AMOUNT_BALANCE,
                    String.valueOf(orderRequest.getOrder().getTotalAmount())));

            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ORDER_AMOUNT_PAID,
                    String.valueOf(orderRequest.getOrder().getTotalAmount())));

            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.DELIVERY_CHANNEL,
                    Channel.BITSILA_ONDC.name()));

            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.EXPECTED_KITCHEN_ID,
                    orderInfo.getKitchenId().toString()));

//            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.IS_EXPRESS_CHECK_OUT,
//                    "true"));

            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.NOTES,
                    orderRequest.getOrder().getNotes()));

            if (orderRequest.getOrder().getExtraInfo() != null && orderRequest.getOrder().getExtraInfo().getFlashOrder() != null) {
                additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.FLASH_ORDER, String.valueOf(orderRequest.getOrder().getExtraInfo().getFlashOrder())));
            }

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
}
