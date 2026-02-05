package com.buyer.service;

import com.buyer.deliveryDB.entity.Order;
import com.buyer.deliveryDB.repository.OrderRepository;
import com.buyer.dto.OrderAdditionalDetailsDto;
import com.buyer.dto.PaymentUserInfo;
import com.buyer.dto.magicpin.Address;
import com.buyer.dto.magicpin.Charge;
import com.buyer.dto.magicpin.CreateOrderResponse;
import com.buyer.dto.magicpin.Item;
import com.buyer.dto.magicpin.MagicpinOrderRequest;
import com.buyer.dto.magicpin.Status;
import com.buyer.entity.MongoDB.MongoOrder;
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
import com.buyer.repository.MongoDB.OrdersRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.buyer.service.SwiggyOrderService.parseOutletId;
import static com.buyer.service.ZomatoOrderService.createOrderAdditionalDetailsDto;
import static com.buyer.service.ZomatoOrderService.generateExternalOrderId;

@Service
public class MagicpinService {

    private static final Logger logger = LoggerFactory.getLogger(MagicpinService.class);

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
    public ResponseEntity<CreateOrderResponse> createOrder(MagicpinOrderRequest orderRequest, Integer brandId) {
        try {
            logger.info("MagicPin create_order service invoked at: {}", new java.util.Date());

            String magicpinOrderId = String.valueOf(orderRequest.getOrderId());

            if(orderRequest.getOrderId() == null){
                logger.warn("No order id found in MagicPin order: {}", magicpinOrderId);
                CreateOrderResponse response = new CreateOrderResponse(
                        Status.Failed, "No order id found", 422, magicpinOrderId);
                return ResponseEntity.unprocessableEntity().body(response);
            }

            if(orderRequest.getItems()==null || orderRequest.getItems().isEmpty()){
                logger.warn("No items found in MagicPin order: {}", magicpinOrderId);
                CreateOrderResponse response = new CreateOrderResponse(
                        Status.SUCCESS, "No items found", 200, magicpinOrderId);
                return ResponseEntity.ok(response);
            }

            if(orderRequest.getItems()!=null && !orderRequest.getItems().isEmpty()){
                for(Item item : orderRequest.getItems()){
                    if(item.getQuantity() == null || item.getQuantity() <= 0){
                        logger.warn("Invalid quantity found in MagicPin order: {}", magicpinOrderId);
                        CreateOrderResponse response = new CreateOrderResponse(
                                Status.SUCCESS, "Invalid quantity found", 200, magicpinOrderId);
                        return ResponseEntity.ok(response);
                    }
                }

                if(orderRequest.getAmount() == 0.0){
                    logger.warn("Invalid amount found in MagicPin order: {}", magicpinOrderId);
                    CreateOrderResponse response = new CreateOrderResponse(
                            Status.Failed, "Invalid amount found", 422, magicpinOrderId);
                    return ResponseEntity.unprocessableEntity().body(response);
                }
            }

            if (orderInfoRepository.findByUserLastNameAndChannel(magicpinOrderId, Channel.MAGIC_PIN).isPresent()) {
                logger.warn("Duplicate MagicPin order received: {}", magicpinOrderId);
                CreateOrderResponse response = new CreateOrderResponse(
                        Status.SUCCESS, "Order already exists", 200, magicpinOrderId);
                return ResponseEntity.ok(response);
            }

            OrderInfo orderInfo = mapMagicpinOrderToOrderInfo(orderRequest, brandId);
            OrderInfo savedOrder = orderInfoRepository.save(orderInfo);

            String externalOrderId = generateExternalOrderId(savedOrder.getBrandId(), savedOrder.getId());
            savedOrder.setExternalOrderId(externalOrderId);
            savedOrder = orderInfoRepository.save(savedOrder);

            List<OrderAdditionalDetailsDto> orderAdditionalDetailsDtos = saveOrderAdditionalDetails(orderRequest, savedOrder.getId(), savedOrder);
            saveMagicpinOrderItems(orderRequest.getItems(), savedOrder.getId());
            PaymentEntry paymentEntry = savePaymentEntry(savedOrder);
            saveOrderInDeliveryDatabase(savedOrder);

            MongoOrder mongoOrder = zomatoOrderService.mapOrderInfoToMongoOrders(orderInfo,orderAdditionalDetailsDtos,paymentEntry);
            ordersRepository.save(mongoOrder);

            logger.info("MagicPin order created: internalId={}, magicpinOrderId={}, externalOrderId={}",
                    savedOrder.getId(), magicpinOrderId, externalOrderId);

            CreateOrderResponse response = new CreateOrderResponse(
                    Status.SUCCESS, "Order Successfully submitted", 200, externalOrderId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error in MagicPin create order service", e);
            CreateOrderResponse errorResponse = new CreateOrderResponse(
                    Status.Failed, e.getMessage(), 422, null);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
        }
    }

    private OrderInfo mapMagicpinOrderToOrderInfo(MagicpinOrderRequest orderRequest, Integer brandId) {
        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setExternalOrderId(String.valueOf(orderRequest.getOrderId()));
        orderInfo.setBrandId(brandId);
        orderInfo.setChannel(Channel.MAGIC_PIN);
        orderInfo.setAmountToBeCollected(0);

        int totalAmount = 0;
        int packagingCharge = 0;
        if(orderRequest.getItems()!=null && !(orderRequest.getItems()).isEmpty()){
            for (Item item : orderRequest.getItems()) {
                totalAmount += item.getAmount();
                if (item.getCharges() != null && !item.getCharges().isEmpty()) {
                    for (Charge charge : item.getCharges()) {
                        packagingCharge += charge.getAmount();
                    }
                }
            }
        }

        orderInfo.setTotalAmount((float) totalAmount);
        if(orderRequest.getMerchantFundedDiscount()!=null)
        {
            orderInfo.setOfferAmount(orderRequest.getMerchantFundedDiscount().floatValue());
            orderInfo.setFinalAmount((float) (totalAmount - orderRequest.getMerchantFundedDiscount() + packagingCharge));

        }else{
            orderInfo.setOfferAmount(0.0f);
            orderInfo.setFinalAmount((float) (totalAmount  + packagingCharge));
        }

        orderInfo.setOfferCode("MAGIC_PIN_DISCOUNT");
        orderInfo.setStatus(1);
        orderInfo.setOrderData("Buyer_v2");
        orderInfo.setPackagingCharges(BigDecimal.valueOf(packagingCharge));

        // Set kitchen ID from merchant data or default to 1
        if (orderRequest.getMerchantData() != null && orderRequest.getMerchantData().getClientId() != null) {
            String clientId = orderRequest.getMerchantData().getClientId();
            parseOutletId(orderInfo, clientId);
        }

        // Set user info
        OrderUserInfo userInfo = new OrderUserInfo();
        userInfo.setFirstName(orderRequest.getUserName());
        userInfo.setLastName(String.valueOf(orderRequest.getOrderId()));
        userInfo.setMobileNumber(orderRequest.getPhoneNo());
        userInfo.setEmail(orderRequest.getUserName() + "@magicpin.com");
        orderInfo.setUser(userInfo);

        // Set shipping address
        if (orderRequest.getShipingAddress() != null) {
            Address shippingAddr = orderRequest.getShipingAddress();
            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getUserName());
            address.setLastName(String.valueOf(orderRequest.getOrderId()));
            address.setAddressLine1(shippingAddr.getAddressLine1());
            address.setAddressLine2(shippingAddr.getAddressLine2());
            address.setCity(shippingAddr.getCity());
            address.setState(shippingAddr.getState());
            address.setPincode(shippingAddr.getPincode());
            address.setMobileNumber(orderRequest.getPhoneNo());

            if (shippingAddr.getLat() != null && shippingAddr.getLon() != null) {
                address.setLat(String.valueOf(shippingAddr.getLat()));
                address.setLon(String.valueOf(shippingAddr.getLon()));
            }

            OrderAddress savedAddress = orderAddressRepository.save(address);
            orderInfo.setShippingAddress(savedAddress);
            orderInfo.setBillingAddress(savedAddress);
        }

        return orderInfo;
    }

    private void saveMagicpinOrderItems(List<Item> items, Long orderId) {
        if (items == null || items.isEmpty()) return;

        for (Item item : items) {
            com.buyer.entity.OrderItem orderItem = new com.buyer.entity.OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setOrderItemType(OrderItemType.PRODUCT);
            orderItem.setProductId(Long.valueOf(item.getThirdPartyId()));
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSellingPrice(item.getAmount());
            orderItem.setMrp(item.getAmount());
            orderItem.setDiscountAmount(BigDecimal.ZERO);
            if(item.getCharges() != null && !(item.getCharges().isEmpty())){
                for (Charge charge : item.getCharges()) {
                    orderItem.setPackagingPrice(BigDecimal.valueOf(charge.getAmount()));
                }
            }else{
                orderItem.setPackagingPrice(BigDecimal.ZERO);

            }

            orderItem.setCashbackAmount(BigDecimal.ZERO);
            orderItem.setcDisc(BigDecimal.ZERO);
            orderItem.setTsp(item.getAmount());
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
        paymentEntry.setPaymentMethod(PaymentMethod.MAGIC_PIN);
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

        com.buyer.entity.PaymentEntry paymentEntry1 = paymentEntryRepository.save(paymentEntry);

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
        deliveryOrder.setSource("fm");
        deliveryOrder.setDeliveryChannel(orderInfo.getChannel().toString());
        deliveryOrder.setTripOrderSeq(1L);
        deliveryOrder.setSearchKey(orderInfo.getId() + "," + orderInfo.getShippingAddress().getLastName() + ",");
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata")).withNano(0);
        deliveryOrder.setCreatedAt(now);
        deliveryOrder.setUpdatedAt(now);

        orderRepository.save(deliveryOrder);
    }

    private List<OrderAdditionalDetailsDto> saveOrderAdditionalDetails(MagicpinOrderRequest orderRequest, Long orderId, OrderInfo orderInfo) {
        List<OrderAdditionalDetailsDto> additionalDetailsDtos = new ArrayList<>();

        try {

            // MagicPin Order ID
            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.MAGIC_PIN_ORDER_ID,
                    String.valueOf(orderRequest.getOrderId())));

            // Amount Balance
            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ORDER_AMOUNT_BALANCE,
                    String.valueOf(orderRequest.getAmount())));

            // Amount Paid
            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.ORDER_AMOUNT_PAID,
                    String.valueOf(orderRequest.getAmount())));

            // Delivery Channel
            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.DELIVERY_CHANNEL,
                    Channel.MAGIC_PIN.name()));

            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.EXPECTED_KITCHEN_ID,
                    orderInfo.getKitchenId().toString()));

            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.IS_EXPRESS_CHECK_OUT,
                    "true"));

            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.MAGIC_PIN_OTP,
                    orderRequest.getRiderOTP()));

            additionalDetailsDtos.add(createOrderAdditionalDetailsDto(orderId, OrderAdditionalData.NOTES,
                    orderRequest.getNote()));


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
}
