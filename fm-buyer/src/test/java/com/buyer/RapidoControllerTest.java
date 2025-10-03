package com.buyer;

import com.buyer.repository.*;
import com.buyer.service.TestDataCleanupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.*;

import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RapidoControllerTest extends AbstractTestNGSpringContextTests {

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
        String[] prefixes = {"RF"};
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
        String[] prefixes = {"RF"};
        for (String prefix : prefixes) {
            paymentEntryRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderItemRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderAdditionalDetailsRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderInfoRepository.deleteByUserLastNamePrefix(prefix);
        }
    }


    private String generateRandomOrderId() {
        return "RF" + System.currentTimeMillis();
    }
    private String buildOrderJson(String orderId) {
        return String.format("""
                {
                    "orderInfo": {
                        "orderId": "%s",
                        "restId": "7",
                        "providerRestId": "7",
                        "menuSharingCode": "7",
                        "status": "ORDER_REQUESTED",
                        "total": 126,
                        "subTotal": 120,
                        "createdAt": 1759073265701,
                        "totalPackingCharge": 0,
                        "itemLevelTaxes": 6,
                        "totalTaxes": 6,
                        "deliveryMode": "delivery",
                        "taxes": [
                            { "id": "12343", "providerId": "12343", "title": "CGST", "value": 3, "percentage": 2.5, "liabilityOn": "aggregator" },
                            { "id": "12344", "providerId": "12344", "title": "SGST", "value": 3, "percentage": 2.5, "liabilityOn": "aggregator" }
                        ],
                        "isBrand": true,
                        "brandId": "BR112460"
                    },
                    "payment": {
                        "mode": "online",
                        "status": "paid",
                        "amountPaid": 0
                    },
                    "customer": {
                        "firstName": "RAPIDO_TEST Customer",
                        "lastName": "RF",
                        "location": {
                            "latitude": 12.913318247183508,
                            "longitude": 77.63354843482375,
                            "address": "",
                            "pinCode": "",
                            "instructions": ""
                        }
                    },
                    "items": [
                        {
                            "itemId": "10193",
                            "providerId": "10193",
                            "name": "Rooster Chicken Rice Bowl",
                            "quantity": 1,
                            "unitPrice": 120,
                            "taxes": [
                                { "id": "12343", "providerId": "12343", "title": "CGST", "value": 3, "percentage": 2.5, "liabilityOn": "aggregator" },
                                { "id": "12344", "providerId": "12344", "title": "SGST", "value": 3, "percentage": 2.5, "liabilityOn": "aggregator" }
                            ],
                            "charges": null,
                            "subTotal": 120,
                            "total": 126,
                            "gstType": "services"
                        }
                    ]
                }""", orderId);
    }

    // =======================
    // Test Cases
    // =======================

    @Test(groups = {"api","smoke"},priority = 1, description = "Test basic Rapido order creation")
    public void testCreateRapidoOrderBasic() throws Exception {
        String orderId = generateRandomOrderId();
        String orderJson = buildOrderJson(orderId);

        mockMvc.perform(post("/api/rapido/create_order")
                        .header("Auth-Key", "jvVGCPqkPv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.message").value("Order Successfully submitted"));
    }



    @Test(groups = {"security"},priority = 2, description = "Test Rapido order creation without authentication")
    public void testCreateRapidoOrderWithoutAuth() throws Exception {
        String orderId = generateRandomOrderId();
        String orderJson = buildOrderJson(orderId);

        var result = mockMvc.perform(post("/api/rapido/create_order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Invalid or missing Auth-Key"))
                .andExpect(jsonPath("$.error_code").value(401))
                .andReturn();
    }

    @Test(groups = {"security"},priority = 3, description = "Test Rapido order creation with empty request body")
    public void testCreateRapidoOrderWithEmptyBody() throws Exception {
        var result = mockMvc.perform(post("/api/rapido/create_order")
                        .header("Auth-Key", "jvVGCPqkPv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error_code").value(422))
                .andReturn();
    }


    @Test(groups = {"security"},priority = 4, description = "Test Rapido order creation with missing order ID")
    public void testCreateRapidoOrderWithMissingOrderId() throws Exception {
        String orderJson = """
                {
                    "customer": { "firstName": "Test User" },
                    "items": []
                }""";

        var result = mockMvc.perform(post("/api/rapido/create_order")
                        .header("Auth-Key", "jvVGCPqkPv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error_code").value(422))
                .andReturn();
    }

    @Test(groups = {"api","integration"},priority = 5, description = "Test Rapido order with multiple items and taxes")
    public void testCreateRapidoOrderWithMultipleItems() throws Exception {
        String orderId = generateRandomOrderId();

        String orderJson = String.format("""
            {
                "orderInfo": {
                    "orderId": "%s",
                    "restId": "7",
                    "providerRestId": "7",
                    "menuSharingCode": "7",
                    "status": "ORDER_REQUESTED",
                    "total": 246,
                    "subTotal": 240,
                    "createdAt": 1759073265701,
                    "totalPackingCharge": 0,
                    "itemLevelTaxes": 6,
                    "totalTaxes": 6,
                    "deliveryMode": "delivery",
                    "isBrand": true,
                    "brandId": "BR112460"
                },
                "payment": {
                    "mode": "online",
                    "status": "paid",
                    "amountPaid": 246
                },
                "customer": {
                    "firstName": "RAPIDO_TEST Multi",
                    "lastName": "RF",
                    "location": { "latitude": 12.913318, "longitude": 77.633548, "address": "", "pinCode": "", "instructions": "" }
                },
                "items": [
                    {
                        "itemId": "14134",
                        "providerId": "14134",
                        "name": "Rooster Chicken Rice Bowl",
                        "quantity": 1,
                        "unitPrice": 120,
                        "taxes": [
                            { "id": "12343", "providerId": "12343", "title": "CGST", "value": 3, "percentage": 2.5, "liabilityOn": "aggregator" },
                            { "id": "12344", "providerId": "12344", "title": "SGST", "value": 3, "percentage": 2.5, "liabilityOn": "aggregator" }
                        ],
                        "subTotal": 120,
                        "total": 126,
                        "gstType": "services"
                    },
                    {
                        "itemId": "14135",
                        "providerId": "14135",
                        "name": "Veggie Delight Bowl",
                        "quantity": 1,
                        "unitPrice": 120,
                        "taxes": [
                            { "id": "12343", "providerId": "12343", "title": "CGST", "value": 3, "percentage": 2.5, "liabilityOn": "aggregator" },
                            { "id": "12344", "providerId": "12344", "title": "SGST", "value": 3, "percentage": 2.5, "liabilityOn": "aggregator" }
                        ],
                        "subTotal": 120,
                        "total": 126,
                        "gstType": "services"
                    }
                ]
            }""", orderId);

        mockMvc.perform(post("/api/rapido/create_order")
                        .header("Auth-Key", "jvVGCPqkPv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.message").value("Order Successfully submitted"));
    }

    @Test( groups = {"api","integration"}, priority = 6, description = "Test Rapido order with zero price items")
    public void testCreateRapidoOrderWithZeroPriceItems() throws Exception {
        String orderId = generateRandomOrderId();

        String orderJson = String.format("""
            {
                "orderInfo": {
                    "orderId": "%s",
                    "restId": "7",
                    "providerRestId": "7",
                    "menuSharingCode": "7",
                    "status": "ORDER_REQUESTED",
                    "total": 0,
                    "subTotal": 0,
                    "createdAt": 1759073265701,
                    "totalPackingCharge": 0,
                    "itemLevelTaxes": 0,
                    "totalTaxes": 0,
                    "deliveryMode": "delivery",
                    "isBrand": true,
                    "brandId": "BR112460"
                },
                "payment": {
                    "mode": "online",
                    "status": "paid",
                    "amountPaid": 0
                },
                "customer": {
                    "firstName": "RAPIDO_TEST Free",
                    "lastName": "RF",
                    "location": { "latitude": 12.913318, "longitude": 77.633548, "address": "", "pinCode": "", "instructions": "" }
                },
                "items": [
                    {
                        "itemId": "14136",
                        "providerId": "14136",
                        "name": "Free Sample Dish",
                        "quantity": 1,
                        "unitPrice": 0,
                        "subTotal": 0,
                        "total": 0,
                        "taxes": [],
                        "gstType": "services"
                    }
                ]
            }""", orderId);

        mockMvc.perform(post("/api/rapido/create_order")
                        .header("Auth-Key", "jvVGCPqkPv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.message").exists())
                .andReturn();

    }

    @Test( groups = {"api","integration"},priority = 7, description = "Test Rapido order with high quantity items")
    public void testCreateRapidoOrderWithHighQuantity() throws Exception {
        String orderId = generateRandomOrderId();

        String orderJson = String.format("""
            {
                "orderInfo": {
                    "orderId": "%s",
                    "restId": "7",
                    "providerRestId": "7",
                    "menuSharingCode": "7",
                    "status": "ORDER_REQUESTED",
                    "total": 1800,
                    "subTotal": 1800,
                    "createdAt": 1759073265701,
                    "totalPackingCharge": 0,
                    "itemLevelTaxes": 0,
                    "totalTaxes": 0,
                    "deliveryMode": "delivery",
                    "isBrand": true,
                    "brandId": "BR112460"
                },
                "payment": {
                    "mode": "online",
                    "status": "paid",
                    "amountPaid": 1800
                },
                "customer": {
                    "firstName": "RAPIDO_TEST Bulk",
                    "lastName": "RF",
                    "location": { "latitude": 12.913318, "longitude": 77.633548, "address": "", "pinCode": "", "instructions": "" }
                },
                "items": [
                    {
                        "itemId": "14137",
                        "providerId": "14137",
                        "name": "Bulk Order Item",
                        "quantity": 10,
                        "unitPrice": 180,
                        "subTotal": 1800,
                        "total": 1800,
                        "taxes": [],
                        "gstType": "services"
                    }
                ]
            }""", orderId);

        mockMvc.perform(post("/api/rapido/create_order")
                        .header("Auth-Key", "jvVGCPqkPv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.message").value("Order Successfully submitted"));
    }
}
