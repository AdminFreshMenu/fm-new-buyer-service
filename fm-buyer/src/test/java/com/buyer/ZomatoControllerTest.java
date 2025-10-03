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

import static org.testng.Assert.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ZomatoControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private TestDataCleanupService testDataCleanupService;
    
    // Additional repository dependencies for direct cleanup
    @Autowired
    private OrderInfoRepository orderInfoRepository;
    
    @Autowired
    private PaymentEntryRepository paymentEntryRepository;
    
    @Autowired
    private OrderAdditionalDetailsRepository orderAdditionalDetailsRepository;
    
    @Autowired
    private MockMvc mockMvc;

    @BeforeMethod
    public void cleanupBefore() {
        // Clean up test data before each test based on lastName prefix
        String[] prefixes = {"TEST", "DISH_TEST"};
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
        String[] prefixes = {"TEST", "DISH_TEST"};
        for (String prefix : prefixes) {
            paymentEntryRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderItemRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderAdditionalDetailsRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderInfoRepository.deleteByUserLastNamePrefix(prefix);
        }
    }


    @Test(groups = {"api", "smoke"}, priority = 1, description = "Test basic order creation endpoint")
    public void testCreateOrderEndpoint() throws Exception {
        // Use timestamp to create unique order ID for each test run
        String uniqueOrderId = "TEST" + System.currentTimeMillis();
        String orderJson = "{\n" +
                "  \"order\": {\n" +
                "    \"order_id\": \"" + uniqueOrderId + "\",\n" +
                "    \"outlet_id\": \"5\",\n" +
                "    \"gross_amount\": 250.50,\n" +
                "    \"net_amount\": 275.75,\n" +
                "    \"customer_details\": {\n" +
                "      \"name\": \"TEST User\",\n" +
                "      \"phone_number\": \"+919876543210\",\n" +
                "      \"email\": \"john@example.com\",\n" +
                "      \"address\": \"123 Main Street\",\n" +
                "      \"delivery_area\": \"Koramangala\",\n" +
                "      \"city\": \"Bangalore\",\n" +
                "      \"state\": \"Karnataka\",\n" +
                "      \"pincode\": \"560001\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        mockMvc.perform(post("/api/zomato/create_order")
                        .header("Auth-Key", "jvVGCPqkPv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Order created successfully"))
                .andExpect(jsonPath("$.externalOrderId").value(org.hamcrest.Matchers.startsWith("fm")))
                .andExpect(jsonPath("$.internalOrderId").exists());
    }

    @Test(groups = {"api", "security"}, priority = 2, description = "Test order creation without authentication")
    public void testCreateOrderWithoutAuth() throws Exception {
        String orderJson = "{\n" +
                "  \"order\": {\n" +
                "    \"order_id\": \"TEST789\"\n" +
                "  }\n" +
                "}";

        mockMvc.perform(post("/api/zomato/create_order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Invalid or missing Auth-Key"));
    }

    @Test(groups = {"api", "integration"}, priority = 3, description = "Test order creation with dishes and addons")
    public void testCreateOrderWithDishes() throws Exception {
        // Use timestamp to create unique order ID for each test run
        String uniqueOrderId = "DISH_TEST" + System.currentTimeMillis();
        String orderJsonWithDishes = "{\n" +
                "  \"order\": {\n" +
                "    \"order_id\": \"" + uniqueOrderId + "\",\n" +
                "    \"outlet_id\": \"5\",\n" +
                "    \"gross_amount\": 550.75,\n" +
                "    \"net_amount\": 585.50,\n" +
                "    \"customer_details\": {\n" +
                "      \"name\": \"DISH_TEST User\",\n" +
                "      \"phone_number\": \"+919876543210\",\n" +
                "      \"email\": \"john.doe@example.com\",\n" +
                "      \"address\": \"123 Main Street\",\n" +
                "      \"delivery_area\": \"Koramangala\",\n" +
                "      \"city\": \"Bangalore\",\n" +
                "      \"state\": \"Karnataka\",\n" +
                "      \"pincode\": \"560001\"\n" +
                "    },\n" +
                "    \"dishes\": [\n" +
                "      {\n" +
                "        \"dish_type\": \"variant\",\n" +
                "        \"dish_id\": \"1001\",\n" +
                "        \"composition\": {\n" +
                "          \"variant_id\": \"1001\",\n" +
                "          \"catalogue_id\": \"1001\",\n" +
                "          \"catalogue_name\": \"Margherita Pizza\",\n" +
                "          \"unit_cost\": 250,\n" +
                "          \"modifier_groups\": [\n" +
                "            {\n" +
                "              \"group_id\": \"1001_0\",\n" +
                "              \"group_name\": \"AddOns\",\n" +
                "              \"variants\": [\n" +
                "                {\n" +
                "                  \"variant_id\": \"2001\",\n" +
                "                  \"catalogue_name\": \"Extra Cheese\",\n" +
                "                  \"unit_cost\": 50,\n" +
                "                  \"quantity\": 1,\n" +
                "                  \"total_cost\": 50\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        \"quantity\": 2,\n" +
                "        \"total_cost\": 500,\n" +
                "        \"final_cost\": 500\n" +
                "      },\n" +
                "      {\n" +
                "        \"dish_type\": \"variant\",\n" +
                "        \"dish_id\": \"1002\",\n" +
                "        \"composition\": {\n" +
                "          \"variant_id\": \"1002\",\n" +
                "          \"catalogue_id\": \"1002\",\n" +
                "          \"catalogue_name\": \"Caesar Salad\",\n" +
                "          \"unit_cost\": 180,\n" +
                "          \"modifier_groups\": []\n" +
                "        },\n" +
                "        \"quantity\": 1,\n" +
                "        \"total_cost\": 180,\n" +
                "        \"final_cost\": 180\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";

        // Perform the API call and get the response
        var result = mockMvc.perform(post("/api/zomato/create_order")
                        .header("Auth-Key", "jvVGCPqkPv")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJsonWithDishes))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Order created successfully"))
                .andExpect(jsonPath("$.externalOrderId").value(org.hamcrest.Matchers.startsWith("fm")))
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
        
        // Verify we have the expected number of items (2 main dishes + 1 addon)
        assertEquals(orderItems.size(), 3, "Should have 3 order items (2 dishes + 1 addon)");
        
        // Verify main dishes
        List<OrderItem> mainDishes = orderItems.stream()
                .filter(item -> item.getOrderItemType() == OrderItemType.PRODUCT)
                .toList();
        assertEquals(mainDishes.size(), 2, "Should have 2 main dishes");
        
        // Verify Pizza order item
        OrderItem pizzaItem = mainDishes.stream()
                .filter(item -> item.getProductId() == 1001L)
                .findFirst()
                .orElse(null);
        assertNotNull(pizzaItem, "Pizza order item should exist");
        assertEquals(pizzaItem.getQuantity(), Integer.valueOf(2), "Pizza quantity should be 2");
        assertEquals(pizzaItem.getSellingPrice().intValue(), 250, "Pizza selling price should be 250 rupees");
        assertEquals(pizzaItem.getTsp().intValue(), 500, "Pizza TSP should be 500 rupees");
        
        // Verify Caesar Salad order item
        OrderItem saladItem = mainDishes.stream()
                .filter(item -> item.getProductId() == 1002L)
                .findFirst()
                .orElse(null);
        assertNotNull(saladItem, "Salad order item should exist");
        assertEquals(saladItem.getQuantity(), Integer.valueOf(1), "Salad quantity should be 1");
        assertEquals(saladItem.getSellingPrice().intValue(), 180, "Salad selling price should be 180 rupees");
        assertEquals(saladItem.getTsp().intValue(), 180, "Salad TSP should be 180 rupees");
        
        // Verify addon
        List<OrderItem> addons = orderItems.stream()
                .filter(item -> item.getOrderItemType() == OrderItemType.ADDON)
                .toList();
        assertEquals(addons.size(), 1, "Should have 1 addon");
        
        OrderItem addonItem = addons.get(0);
        assertEquals(addonItem.getProductId(), Long.valueOf(2001L), "Addon product ID should be 2001");
        assertEquals(addonItem.getQuantity(), Integer.valueOf(1), "Addon quantity should be 1");
        assertEquals(addonItem.getSellingPrice().intValue(), 50, "Addon selling price should be 50 rupees");
        assertNotNull(addonItem.getParentOrderItemId(), "Addon should have parent order item ID");
        
        System.out.println("Successfully verified order items processing: " + orderItems.size() + " items saved");
    }
}
