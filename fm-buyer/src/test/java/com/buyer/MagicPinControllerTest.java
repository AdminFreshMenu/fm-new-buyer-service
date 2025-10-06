package com.buyer;


import com.buyer.entity.OrderInfo;
import com.buyer.repository.OrderAdditionalDetailsRepository;
import com.buyer.repository.OrderInfoRepository;
import com.buyer.repository.OrderItemRepository;
import com.buyer.repository.PaymentEntryRepository;
import com.buyer.service.TestDataCleanupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class MagicPinControllerTest extends AbstractTestNGSpringContextTests {

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

    /**
     * Generate random Integer order ID for test orders
     */
    private Integer generateRandomOrderId() {
        String prefix = "123";

        int randomPart = random.nextInt(90000) + 10000;

        String fullIdString = prefix + randomPart;

        return Integer.parseInt(fullIdString);
    }


    @BeforeMethod
    public void cleanupBefore() {
        // Clean up test data before each test based on lastName prefix and specific firstName
//        String prefix = "123";
        String firstName = "MAGICPIN_TEST";

        String[] prefixes = {"123" };
        for (String prefix : prefixes) {
            paymentEntryRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderItemRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderAdditionalDetailsRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderInfoRepository.deleteByUserLastNamePrefix(prefix);
        }

        List<OrderInfo> orderInfos = orderInfoRepository.findByUserFirstNameLike(firstName);

        System.out.println("magicpin count " + orderInfos.size());
        logger.debug("magicpin order ids" + orderInfos.size());
        for (Long orderId : orderInfos.stream().map(OrderInfo::getId).collect(Collectors.toList())) {

            paymentEntryRepository.deleteByOrderId(orderId);
            orderItemRepository.deleteByOrderId(orderId);
            orderAdditionalDetailsRepository.deleteByOrderId(orderId);
            orderInfoRepository.deleteById(orderId); // finally delete the order itself
        }

    }


    @AfterMethod
    public void cleanupAfter() {
//        // Clean up test data after each test based on lastName prefix
        String[] prefixes = {"123" };
        for (String prefix : prefixes) {
            paymentEntryRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderItemRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderAdditionalDetailsRepository.deleteByOrderUserLastNamePrefix(prefix);
            orderInfoRepository.deleteByUserLastNamePrefix(prefix);
        }
        String firstName = "MAGICPIN_TEST";

        List<OrderInfo> orderInfos = orderInfoRepository.findByUserFirstNameLike(firstName);

        System.out.println("magicpinn count " +orderInfos.size());

        for (Long orderId : orderInfos.stream().map(OrderInfo::getId).collect(Collectors.toList())) {
            paymentEntryRepository.deleteByOrderId(orderId);
            orderItemRepository.deleteByOrderId(orderId);
            orderAdditionalDetailsRepository.deleteByOrderId(orderId);
            orderInfoRepository.deleteById(orderId);
        }
    }

    /**
     * ✅ Basic MagicPin order creation test
     */
    @Test(groups = {"api", "smoke"}, priority = 1, description = "Verify MagicPin order creation with valid data")
    public void testCreateMagicPinOrderSuccess() throws Exception {
        Integer orderId = generateRandomOrderId();

        String requestJson = String.format("""
            {
              "orderId": %d,
              "shipmentId": 56789,
              "orderCreatedAt": 1759479954000,
              "merchantUserId": 10233504,
              "items": [
                {
                  "id": 110634417,
                  "quantity": 1,
                  "amount": 150,
                  "itemType": "ITEM",
                  "third_party_id": "16732",
                  "displayText": "MAGICPIN_TEST Chicken Wrap"
                }
              ],
              "amount": 150.0,
              "tax": 7.5,
              "userId": 45987190,
              "userName": "MAGICPIN_TESTSuccess",
              "phoneNo": "9876543210",
              "orderStatus": "PAYMENT_DONE",
              "orderType": "DELIVERY",
              "paymentTransactionId": "MP_TXN_12345",
              "paymentMode": "ONLINE",
              "merchantData": {
                "merchant_id": 1870669,
                "client_id": "37",
                "integration_partner_name": "freshmenu"
              },
               "shippingAddress": {
                      "addressLine1": "Aditya Birla working women s hostel",
                      "addressLine2": "Siddharth Colony, Postal Colony, Chembur, Mumbai, Swastik Park Swastik Park, ",
                      "addressLine3": "",
                      "localityName": "",
                      "city": "Mumbai",
                      "state": "Maharashtra",
                      "country": "India",
                      "lat": 19.056217193603516,
                      "lon": 72.89323425292969,
                      "name": "Swati",
                      "contactNumbers": [
                        "7903550956"
                      ],
                      "pinCode": "400071"
                    }
            }""", orderId);

        mockMvc.perform(post("/api/magicpin/order/create")
                        .header("Auth-Key", "magicpin")
                        .header("fm_cl", "MAGIC_PIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").exists());

    }

    /**
     * ⚠️ Missing orderId
     */
    @Test(groups = {"api", "validation"}, priority = 3, description = "Validate missing orderId field in MagicPin order creation")
    public void testCreateMagicPinOrderMissingOrderId() throws Exception {
        String requestJson = """
            {
              "amount": 299,
              "tax": 12,
              "userName": "MAGICPIN_TESTMissingOrderId"
            }""";

        mockMvc.perform(post("/api/magicpin/order/create")
                        .header("Auth-Key", "magicpin")
                        .header("fm_cl", "MAGIC_PIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value("Failed"));
    }

    /**
     * 🧾 Test multiple items in order
     */
    @Test(groups = {"api", "integration"}, priority = 4, description = "Verify MagicPin order creation with multiple items")
    public void testCreateMagicPinOrderMultipleItems() throws Exception {
        Integer orderId = generateRandomOrderId();

        String requestJson = String.format("""
            {
              "orderId": %d,
              "shipmentId": 1001,
              "merchantUserId": 10233504,
              "items": [
                {
                  "id": 110634417,
                  "quantity": 1,
                  "amount": 100,
                  "itemType": "ITEM",
                  "third_party_id": "16731",
                  "displayText": "MAGICPIN_TEST Soup"
                },
                {
                  "id": 87417271,
                  "quantity": 1,
                  "amount": 199,
                  "itemType": "ITEM",
                  "third_party_id": "16732",
                  "displayText": "MAGICPIN_TEST Wrap"
                }
              ],
              "amount": 299.0,
              "tax": 15.0,
              "userId": 45987190,
              "userName": "MAGICPIN_TESTMultiItem",
              "orderStatus": "PAYMENT_DONE",
              "paymentMode": "ONLINE",
              "merchantData": {
                "merchant_id": 1870669,
                "client_id": "37",
                "integration_partner_name": "freshmenu"
              },
               "shippingAddress": {
                      "addressLine1": "Aditya Birla working women s hostel",
                      "addressLine2": "Siddharth Colony, Postal Colony, Chembur, Mumbai, Swastik Park Swastik Park, ",
                      "addressLine3": "",
                      "localityName": "",
                      "city": "Mumbai",
                      "state": "Maharashtra",
                      "country": "India",
                      "lat": 19.056217193603516,
                      "lon": 72.89323425292969,
                      "name": "Swati",
                      "contactNumbers": [
                        "7903550956"
                      ],
                      "pinCode": "400071"
                    }
            }""", orderId);

         mockMvc.perform(post("/api/magicpin/order/create")
                        .header("Auth-Key", "magicpin")
                        .header("fm_cl", "MAGIC_PIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

    }

    /**
     * 🧩 Test zero-amount item
     */
    @Test(groups = {"api", "edge"}, priority = 5, description = "Validate MagicPin order with zero amount item")
    public void testCreateMagicPinOrderZeroAmountItem() throws Exception {
        Integer orderId = generateRandomOrderId();

        String requestJson = String.format("""
            {
              "orderId": %d,
              "items": [
                {
                  "id": 9999,
                  "quantity": 1,
                  "amount": 0.0,
                  "itemType": "ITEM",
                  "third_party_id": "16732",
                  "displayText": "MAGICPIN_TEST Free Sample"
                }
              ],
              "amount": 0.0,
              "tax": 0.0,
              "userName": "MAGICPIN_TEST FreeItem",
              "orderStatus": "PAYMENT_DONE",
              "merchantData": {
                "merchant_id": 1870669,
                "client_id": "37",
                "integration_partner_name": "freshmenu"
              },
              "paymentMode": "ONLINE",
               "shippingAddress": {
                      "addressLine1": "Aditya Birla working women s hostel",
                      "addressLine2": "Siddharth Colony, Postal Colony, Chembur, Mumbai, Swastik Park Swastik Park, ",
                      "addressLine3": "",
                      "localityName": "",
                      "city": "Mumbai",
                      "state": "Maharashtra",
                      "country": "India",
                      "lat": 19.056217193603516,
                      "lon": 72.89323425292969,
                      "name": "Swati",
                      "contactNumbers": [
                        "7903550956"
                      ],
                      "pinCode": "400071"
                    }
            }""", orderId);

        mockMvc.perform(post("/api/magicpin/order/create")
                        .header("Auth-Key", "magicpin")
                        .header("fm_cl", "MAGIC_PIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnprocessableEntity());
    }
}
