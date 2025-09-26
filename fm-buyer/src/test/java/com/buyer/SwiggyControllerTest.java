package com.buyer;

import com.buyer.entity.OrderItem;
import com.buyer.entity.OrderEnum.OrderItemType;
import com.buyer.repository.OrderAdditionalDetailsRepository;
import com.buyer.repository.OrderInfoRepository;
import com.buyer.repository.OrderItemRepository;
import com.buyer.repository.PaymentEntryRepository;
import com.buyer.service.TestDataCleanupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test suite for Swiggy order creation API
 * Tests various scenarios including success cases, validation failures, and edge cases
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SwiggyControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private TestDataCleanupService testDataCleanupService;
    
    @Autowired
    private OrderInfoRepository orderInfoRepository;
    
    @Autowired
    private PaymentEntryRepository paymentEntryRepository;
    
    @Autowired
    private OrderAdditionalDetailsRepository orderAdditionalDetailsRepository;
    
    @Autowired
    private MockMvc mockMvc;

    private final Random random = new Random();

    @BeforeMethod
    public void cleanupBefore() {
        // Clean up test data before each test based on lastName prefix
        String[] prefixes = {"SWIGGY_TEST", "SW_TEST"};
        for (String prefix : prefixes) {
            paymentEntryRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderItemRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderAdditionalDetailsRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderInfoRepository.deleteByUserLastNamePrefix(prefix);
        }
    }

    @AfterMethod
    public void cleanupAfter() {
        // Clean up test data after each test based on lastName prefix
        String[] prefixes = {"SWIGGY_TEST", "SW_TEST"};
        for (String prefix : prefixes) {
            paymentEntryRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderItemRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderAdditionalDetailsRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderInfoRepository.deleteByUserLastNamePrefix(prefix);
        }
    }

    /**
     * Generate random order ID for tests
     */
    private Long generateRandomOrderId() {
        return Math.abs(random.nextLong() % 9000000000L) + 1000000000L;
    }


    @Test(groups = {"api", "smoke"}, priority = 2, description = "Test basic Swiggy order creation")
    public void testCreateSwiggyOrderBasic() throws Exception {
        Long randomOrderId = generateRandomOrderId();
        String orderJson = String.format("""
                {
                    "sender_phone_number": null,
                    "cart_gst": 8.7,
                    "instructions": "",
                    "order_packing_charges": 15.0,
                    "cart_igst_percent": 0.0,
                    "channel": "Swiggy",
                    "order_cess_charges": {},
                    "sender_name": "SWIGGY_TEST User",
                    "payment_qr_url": "",
                    "cart_cgst_percent": 2.5,
                    "cart_igst": 0.0,
                    "delivery_type": "PICKUP",
                    "is_self_delivery_v2": false,
                    "order_packing_charges_cgst": 0.375,
                    "order_packing_charges_igst_percent": 0.0,
                    "order_type": "regular",
                    "transaction_id": "241768936000948",
                    "restaurant_gross_bill": 361.7,
                    "order_packing_charges_sgst": 0.375,
                    "special_instructions": {
                        "si_opted_in": true,
                        "si_cx_instruction": "",
                        "si_status": "SENT"
                    },
                    "cart_cgst": 4.35,
                    "restaurant_service_charges": 0.0,
                    "restaurant_name": "FreshMenu",
                    "tags": ["Swiggy"],
                    "can_reject_order": false,
                    "payment_type": "Online",
                    "restaurant_discount": 179.0,
                    "offer_data": null,
                    "customer_email": "testuser@example.com",
                    "items": [
                        {
                            "sgst": 3.975,
                            "gst_inclusive": false,
                            "quantity": 1,
                            "cgst_percent": 2.5,
                            "gst_liability": "SWIGGY",
                            "reward_type": null,
                            "addons": [
                                {
                                    "sgst": 0.0,
                                    "sgst_percent": 0.0,
                                    "cgst_percent": 0.0,
                                    "gst_liability": "SWIGGY",
                                    "group_id": "15446_1",
                                    "price": 39.0,
                                    "name": "Large Bowl(500gms)",
                                    "id": "16713",
                                    "cgst": 0.0,
                                    "igst_percent": 0.0,
                                    "igst": 0.0
                                }
                            ],
                            "final_sub_total": 159.0,
                            "cgst": 3.975,
                            "variants": [],
                            "is_veg": "0",
                            "partner_promo_details": [],
                            "igst": 0.0,
                            "free_quantity": 0,
                            "sgst_percent": 2.5,
                            "item_restaurant_offers_discount": 179.0,
                            "subtotal": 338.0,
                            "price": 299.0,
                            "name": "Peri Peri Mornay Chicken Bowl",
                            "id": "15446",
                            "igst_percent": 0.0,
                            "packing_charges": 15.0
                        }
                    ],
                    "order_id": %d,
                    "meals": [],
                    "order_packing_charges_sgst_percent": 2.5,
                    "cutlery_opted_in": false,
                    "reward_type": "Get items under 120",
                    "cart_sgst": 4.35,
                    "delivery_fee_coupon_restaurant_discount": 0.0,
                    "cart_sgst_percent": 2.5,
                    "order_cess_expressions": {},
                    "callback_url": "https://rms.swiggy.com/external/order/confirm",
                    "order_packing_charges_gst": 0.75,
                    "order_edit": false,
                    "cart_gst_percent": 5.0,
                    "gst_breakup": {
                        "total": 8.7,
                        "vendor": {
                            "packaging_charge_gst": 0.0,
                            "total": 0.0,
                            "item_charge_gst": 0.0,
                            "service_charge_gst": 0.0
                        },
                        "swiggy": {
                            "packaging_charge_gst": 0.75,
                            "total": 8.7,
                            "item_charge_gst": 7.95,
                            "service_charge_gst": 0.0
                        }
                    },
                    "prep_time_details": {
                        "predicted_prep_time": 14.933333,
                        "max_increase_threshold": 5.0,
                        "max_decrease_threshold": 5.0
                    },
                    "parent_order_id": "",
                    "order_packing_charges_igst": 0.0,
                    "order_date_time": "2025-09-24 11:32:16",
                    "delivery_type_v2": "DELIVERY",
                    "order_packing_charges_cgst_percent": 2.5,
                    "outlet_id": "114",
                    "order_edit_reason": null,
                    "customer_name": "SWIGGY_TEST Customer",
                    "is_thirty_mof": false,
                    "customer_id": "9580894"
                }""", randomOrderId);

        mockMvc.perform(post("/partners/api/v1/swiggy/orders/114")
                        .header("x-auth-token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.externalOrderId").exists())
                .andExpect(jsonPath("$.internalOrderId").exists());
    }

    @Test(groups = {"api", "integration"}, priority = 3, description = "Test Swiggy order with multiple items and addons")
    public void testCreateSwiggyOrderWithMultipleItems() throws Exception {
        Long randomOrderId = generateRandomOrderId();
        String orderJson = String.format("""
                {
                    "sender_phone_number": "+919876543210",
                    "cart_gst": 18.45,
                    "instructions": "Handle with care",
                    "order_packing_charges": 25.0,
                    "cart_igst_percent": 0.0,
                    "channel": "Swiggy",
                    "order_cess_charges": {},
                    "sender_name": "SW_TEST Sender",
                    "payment_qr_url": "",
                    "cart_cgst_percent": 2.5,
                    "cart_igst": 0.0,
                    "delivery_type": "DELIVERY",
                    "is_self_delivery_v2": false,
                    "order_packing_charges_cgst": 0.625,
                    "order_packing_charges_igst_percent": 0.0,
                    "order_type": "regular",
                    "transaction_id": "241768936000999",
                    "restaurant_gross_bill": 785.50,
                    "order_packing_charges_sgst": 0.625,
                    "special_instructions": {
                        "si_opted_in": true,
                        "si_cx_instruction": "Ring the bell twice",
                        "si_status": "SENT"
                    },
                    "cart_cgst": 9.225,
                    "restaurant_service_charges": 15.0,
                    "restaurant_name": "FreshMenu",
                    "tags": ["Swiggy", "Premium"],
                    "can_reject_order": false,
                    "payment_type": "Online",
                    "restaurant_discount": 50.0,
                    "offer_data": null,
                    "customer_email": "multitest@example.com",
                    "items": [
                        {
                            "sgst": 7.45,
                            "gst_inclusive": false,
                            "quantity": 2,
                            "cgst_percent": 2.5,
                            "gst_liability": "SWIGGY",
                            "reward_type": null,
                            "addons": [
                                {
                                    "sgst": 1.25,
                                    "sgst_percent": 2.5,
                                    "cgst_percent": 2.5,
                                    "gst_liability": "SWIGGY",
                                    "group_id": "15446_1",
                                    "price": 50.0,
                                    "name": "Extra Large Bowl(750gms)",
                                    "id": "16714",
                                    "cgst": 1.25,
                                    "igst_percent": 0.0,
                                    "igst": 0.0
                                },
                                {
                                    "sgst": 0.75,
                                    "sgst_percent": 2.5,
                                    "cgst_percent": 2.5,
                                    "gst_liability": "SWIGGY",
                                    "group_id": "15446_2",
                                    "price": 30.0,
                                    "name": "Extra Sauce",
                                    "id": "16715",
                                    "cgst": 0.75,
                                    "igst_percent": 0.0,
                                    "igst": 0.0
                                }
                            ],
                            "final_sub_total": 298.0,
                            "cgst": 7.45,
                            "variants": [],
                            "is_veg": "0",
                            "partner_promo_details": [],
                            "igst": 0.0,
                            "free_quantity": 0,
                            "sgst_percent": 2.5,
                            "item_restaurant_offers_discount": 25.0,
                            "subtotal": 598.0,
                            "price": 299.0,
                            "name": "Peri Peri Mornay Chicken Bowl",
                            "id": "15446",
                            "igst_percent": 0.0,
                            "packing_charges": 15.0
                        },
                        {
                            "sgst": 3.75,
                            "gst_inclusive": false,
                            "quantity": 1,
                            "cgst_percent": 2.5,
                            "gst_liability": "SWIGGY",
                            "reward_type": null,
                            "addons": [],
                            "final_sub_total": 150.0,
                            "cgst": 3.75,
                            "variants": [],
                            "is_veg": "1",
                            "partner_promo_details": [],
                            "igst": 0.0,
                            "free_quantity": 0,
                            "sgst_percent": 2.5,
                            "item_restaurant_offers_discount": 0.0,
                            "subtotal": 150.0,
                            "price": 150.0,
                            "name": "Garden Fresh Salad",
                            "id": "15447",
                            "igst_percent": 0.0,
                            "packing_charges": 5.0
                        }
                    ],
                    "order_id": %d,
                    "meals": [],
                    "order_packing_charges_sgst_percent": 2.5,
                    "cutlery_opted_in": true,
                    "reward_type": "Weekend Special",
                    "cart_sgst": 9.225,
                    "delivery_fee_coupon_restaurant_discount": 10.0,
                    "cart_sgst_percent": 2.5,
                    "order_cess_expressions": {},
                    "callback_url": "https://rms.swiggy.com/external/order/confirm",
                    "order_packing_charges_gst": 1.25,
                    "order_edit": false,
                    "cart_gst_percent": 5.0,
                    "gst_breakup": {
                        "total": 18.45,
                        "vendor": {
                            "packaging_charge_gst": 0.0,
                            "total": 0.0,
                            "item_charge_gst": 0.0,
                            "service_charge_gst": 0.0
                        },
                        "swiggy": {
                            "packaging_charge_gst": 1.25,
                            "total": 18.45,
                            "item_charge_gst": 17.20,
                            "service_charge_gst": 0.0
                        }
                    },
                    "prep_time_details": {
                        "predicted_prep_time": 25.0,
                        "max_increase_threshold": 8.0,
                        "max_decrease_threshold": 5.0
                    },
                    "parent_order_id": "",
                    "order_packing_charges_igst": 0.0,
                    "order_date_time": "2025-09-25 14:15:30",
                    "delivery_type_v2": "DELIVERY",
                    "order_packing_charges_cgst_percent": 2.5,
                    "outlet_id": "114",
                    "order_edit_reason": null,
                    "customer_name": "SW_TEST Multiple Items",
                    "is_thirty_mof": false,
                    "customer_id": "9580895"
                }""", randomOrderId);

        var result = mockMvc.perform(post("/partners/api/v1/swiggy/orders/114")
                        .header("x-auth-token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.externalOrderId").exists())
                .andExpect(jsonPath("$.internalOrderId").exists())
                .andReturn();

        // Extract the internal order ID from the response
        String responseContent = result.getResponse().getContentAsString();
        var responseObj = objectMapper.readTree(responseContent);
        Long internalOrderId = responseObj.get("internalOrderId").asLong();
        
        // Verify that order items were saved to the database
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(internalOrderId);
        assertNotNull(orderItems, "Order items should not be null");
        assertFalse(orderItems.isEmpty(), "Order items should not be empty");
        
        // Verify we have expected items (2 main items + 2 addons)
        assertEquals(orderItems.size(), 4, "Should have 4 order items (2 main + 2 addons)");
        
        // Verify main dishes count
        List<OrderItem> mainDishes = orderItems.stream()
                .filter(item -> item.getOrderItemType() == OrderItemType.PRODUCT)
                .toList();
        assertEquals(mainDishes.size(), 2, "Should have 2 main dishes");
        
        // Verify addons count
        List<OrderItem> addons = orderItems.stream()
                .filter(item -> item.getOrderItemType() == OrderItemType.ADDON)
                .toList();
        assertEquals(addons.size(), 2, "Should have 2 addons");
        
        System.out.println("Successfully verified multi-item order processing: " + orderItems.size() + " items saved");
    }

    @Test(groups = {"api", "validation"}, priority = 4, description = "Test Swiggy order creation without authentication")
    public void testCreateSwiggyOrderWithoutAuth() throws Exception {
        Long randomOrderId = generateRandomOrderId();
        String orderJson = String.format("""
                {
                    "order_id": %d,
                    "outlet_id": "999",
                    "customer_name": "Test User",
                    "restaurant_discount": 0.0,
                    "items": []
                }""", randomOrderId);

        mockMvc.perform(post("/partners/api/v1/swiggy/orders/114")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Invalid or missing x-auth-token"))
                .andExpect(jsonPath("$.error_code").value(401));
    }

    @Test(groups = {"api", "validation"}, priority = 5, description = "Test Swiggy order creation with different merchant ID")
    public void testCreateSwiggyOrderWithDifferentMerchant() throws Exception {
        Long randomOrderId = generateRandomOrderId();
        String orderJson = String.format("""
                {
                    "order_id": %d,
                    "outlet_id": "999",
                    "customer_name": "Test User",
                    "restaurant_discount": 0.0,
                    "items": []
                }""", randomOrderId);

        mockMvc.perform(post("/partners/api/v1/swiggy/orders/invalid")
                        .header("x-auth-token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.externalOrderId").exists())
                .andExpect(jsonPath("$.internalOrderId").exists());
    }

    @Test(groups = {"api", "validation"}, priority = 6, description = "Test Swiggy order creation with empty request body")
    public void testCreateSwiggyOrderWithEmptyBody() throws Exception {
        mockMvc.perform(post("/partners/api/v1/swiggy/orders/114")
                        .header("x-auth-token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Missing order ID"))
                .andExpect(jsonPath("$.error_code").value(400));
    }

    @Test(groups = {"api", "validation"}, priority = 7, description = "Test Swiggy order creation with missing order ID")
    public void testCreateSwiggyOrderWithMissingOrderId() throws Exception {
        String orderJson = """
                {
                    "outlet_id": "114",
                    "customer_name": "Test User",
                    "restaurant_discount": 0.0,
                    "items": []
                }""";

        mockMvc.perform(post("/partners/api/v1/swiggy/orders/114")
                        .header("x-auth-token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Missing order ID"))
                .andExpect(jsonPath("$.error_code").value(400));
    }


    @Test(groups = {"api", "edge"}, priority = 9, description = "Test Swiggy order with zero price items")
    public void testCreateSwiggyOrderWithZeroPriceItems() throws Exception {
        Long randomOrderId = generateRandomOrderId();
        String orderJson = String.format("""
                {
                    "cart_gst": 0.0,
                    "instructions": "Free sample",
                    "order_packing_charges": 0.0,
                    "cart_igst_percent": 0.0,
                    "channel": "Swiggy",
                    "sender_name": "SWIGGY_TEST Free",
                    "cart_cgst_percent": 0.0,
                    "cart_igst": 0.0,
                    "delivery_type": "DELIVERY",
                    "order_type": "promotional",
                    "restaurant_gross_bill": 0.0,
                    "cart_cgst": 0.0,
                    "restaurant_service_charges": 0.0,
                    "restaurant_name": "FreshMenu",
                    "tags": ["Swiggy", "Free"],
                    "payment_type": "Free",
                    "restaurant_discount": 0.0,
                    "customer_email": "free@example.com",
                    "items": [
                        {
                            "quantity": 1,
                            "price": 0.0,
                            "name": "Free Sample Dish",
                            "id": "12300",
                            "addons": [],
                            "final_sub_total": 0.0,
                            "subtotal": 0.0
                        }
                    ],
                    "order_id": %d,
                    "cutlery_opted_in": false,
                    "cart_sgst": 0.0,
                    "cart_sgst_percent": 0.0,
                    "callback_url": "https://rms.swiggy.com/external/order/confirm",
                    "order_edit": false,
                    "cart_gst_percent": 0.0,
                    "order_date_time": "2025-09-25 17:00:00",
                    "delivery_type_v2": "DELIVERY",
                    "outlet_id": "114",
                    "customer_name": "SWIGGY_TEST Free Customer",
                    "is_thirty_mof": false,
                    "customer_id": "free001"
                }""", randomOrderId);

        mockMvc.perform(post("/partners/api/v1/swiggy/orders/114")
                        .header("x-auth-token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.externalOrderId").exists())
                .andExpect(jsonPath("$.internalOrderId").exists());
    }

    @Test(groups = {"api", "integration"}, priority = 10, description = "Test Swiggy order with high quantity items")
    public void testCreateSwiggyOrderWithHighQuantity() throws Exception {
        Long randomOrderId = generateRandomOrderId();
        String orderJson = String.format("""
                {
                    "cart_gst": 45.0,
                    "instructions": "Bulk order",
                    "order_packing_charges": 50.0,
                    "channel": "Swiggy",
                    "sender_name": "SW_TEST Bulk",
                    "cart_cgst_percent": 2.5,
                    "delivery_type": "DELIVERY",
                    "order_type": "bulk",
                    "restaurant_gross_bill": 1800.0,
                    "cart_cgst": 22.5,
                    "restaurant_service_charges": 50.0,
                    "restaurant_name": "FreshMenu",
                    "tags": ["Swiggy", "Bulk"],
                    "payment_type": "Online",
                    "restaurant_discount": 100.0,
                    "customer_email": "bulk@example.com",
                    "items": [
                        {
                            "quantity": 10,
                            "price": 180.0,
                            "name": "Bulk Order Item",
                            "id": "15500",
                            "addons": [],
                            "final_sub_total": 1800.0,
                            "subtotal": 1800.0
                        }
                    ],
                    "order_id": %d,
                    "cutlery_opted_in": true,
                    "cart_sgst": 22.5,
                    "cart_sgst_percent": 2.5,
                    "callback_url": "https://rms.swiggy.com/external/order/confirm",
                    "order_edit": false,
                    "cart_gst_percent": 5.0,
                    "order_date_time": "2025-09-25 18:00:00",
                    "delivery_type_v2": "DELIVERY",
                    "outlet_id": "114",
                    "customer_name": "SW_TEST Bulk Customer",
                    "is_thirty_mof": false,
                    "customer_id": "bulk001"
                }""", randomOrderId);

        var result = mockMvc.perform(post("/partners/api/v1/swiggy/orders/114")
                        .header("x-auth-token", "valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.externalOrderId").exists())
                .andExpect(jsonPath("$.internalOrderId").exists())
                .andReturn();

        // Extract the internal order ID from the response
        String responseContent = result.getResponse().getContentAsString();
        var responseObj = objectMapper.readTree(responseContent);
        Long internalOrderId = responseObj.get("internalOrderId").asLong();
        
        // Verify that order items were saved with correct quantity
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(internalOrderId);
        assertNotNull(orderItems, "Order items should not be null");
        assertFalse(orderItems.isEmpty(), "Order items should not be empty");
        
        OrderItem bulkItem = orderItems.get(0);
        assertEquals(bulkItem.getQuantity(), Integer.valueOf(10), "Bulk item quantity should be 10");
        
        System.out.println("Successfully verified bulk order processing with quantity: " + bulkItem.getQuantity());
    }
}