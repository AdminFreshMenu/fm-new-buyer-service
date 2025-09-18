package com.buyer;

import com.buyer.entity.OrderItem;
import com.buyer.entity.OrderItemType;
import com.buyer.repository.OrderItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
public class ZomatoControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private MockMvc mockMvc;

    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testHealthEndpoint() throws Exception {
        setup();
        mockMvc.perform(get("/api/zomato/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("fm-buyer Zomato Integration"));
    }

    @Test
    public void testCreateOrderEndpoint() throws Exception {
        setup();
        // Use timestamp to create unique order ID for each test run
        String uniqueOrderId = "TEST" + System.currentTimeMillis();
        String orderJson = "{\n" +
                "  \"order\": {\n" +
                "    \"order_id\": \"" + uniqueOrderId + "\",\n" +
                "    \"outlet_id\": \"5\",\n" +
                "    \"gross_amount\": 250.50,\n" +
                "    \"net_amount\": 275.75,\n" +
                "    \"customer_details\": {\n" +
                "      \"name\": \"John Doe\",\n" +
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

    @Test
    public void testCreateOrderWithoutAuth() throws Exception {
        setup();
        String orderJson = "{\n" +
                "  \"order\": {\n" +
                "    \"orderId\": \"TEST789\"\n" +
                "  }\n" +
                "}";

        mockMvc.perform(post("/api/zomato/create_order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("failed"))
                .andExpect(jsonPath("$.message").value("Invalid or missing Auth-Key"));
    }

    @Test
    public void testCreateOrderWithDishes() throws Exception {
        setup();
        // Use timestamp to create unique order ID for each test run
        String uniqueOrderId = "DISH_TEST" + System.currentTimeMillis();
        String orderJsonWithDishes = "{\n" +
                "  \"order\": {\n" +
                "    \"order_id\": \"" + uniqueOrderId + "\",\n" +
                "    \"outlet_id\": \"5\",\n" +
                "    \"gross_amount\": 550.75,\n" +
                "    \"net_amount\": 585.50,\n" +
                "    \"customer_details\": {\n" +
                "      \"name\": \"John Doe\",\n" +
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
        assertEquals(3, orderItems.size(), "Should have 3 order items (2 dishes + 1 addon)");
        
        // Verify main dishes
        List<OrderItem> mainDishes = orderItems.stream()
                .filter(item -> item.getOrderItemType() == OrderItemType.PRODUCT)
                .toList();
        assertEquals(2, mainDishes.size(), "Should have 2 main dishes");
        
        // Verify Pizza order item
        OrderItem pizzaItem = mainDishes.stream()
                .filter(item -> item.getProductId() == 1001L)
                .findFirst()
                .orElse(null);
        assertNotNull(pizzaItem, "Pizza order item should exist");
        assertEquals(2, pizzaItem.getQuantity(), "Pizza quantity should be 2");
        assertEquals(250, pizzaItem.getSellingPrice(), "Pizza selling price should be 250 rupees");
        assertEquals(500, pizzaItem.getTsp(), "Pizza TSP should be 500 rupees");
        
        // Verify Caesar Salad order item
        OrderItem saladItem = mainDishes.stream()
                .filter(item -> item.getProductId() == 1002L)
                .findFirst()
                .orElse(null);
        assertNotNull(saladItem, "Salad order item should exist");
        assertEquals(1, saladItem.getQuantity(), "Salad quantity should be 1");
        assertEquals(180, saladItem.getSellingPrice(), "Salad selling price should be 180 rupees");
        assertEquals(180, saladItem.getTsp(), "Salad TSP should be 180 rupees");
        
        // Verify addon
        List<OrderItem> addons = orderItems.stream()
                .filter(item -> item.getOrderItemType() == OrderItemType.ADDON)
                .toList();
        assertEquals(1, addons.size(), "Should have 1 addon");
        
        OrderItem addonItem = addons.get(0);
        assertEquals(2001L, addonItem.getProductId(), "Addon product ID should be 2001");
        assertEquals(1, addonItem.getQuantity(), "Addon quantity should be 1");
        assertEquals(50, addonItem.getSellingPrice(), "Addon selling price should be 50 rupees");
        assertNotNull(addonItem.getParentOrderItemId(), "Addon should have parent order item ID");
        
        System.out.println("Successfully verified order items processing: " + orderItems.size() + " items saved");
    }
}
