package com.buyer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
}