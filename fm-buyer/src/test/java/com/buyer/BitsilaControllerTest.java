package com.buyer;

import com.buyer.entity.OrderEnum.Channel;
import com.buyer.repository.OrderAdditionalDetailsRepository;
import com.buyer.repository.OrderInfoRepository;
import com.buyer.repository.OrderItemRepository;
import com.buyer.repository.PaymentEntryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BitsilaControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentEntryRepository paymentEntryRepository;

    @Autowired
    private OrderAdditionalDetailsRepository orderAdditionalDetailsRepository;

  @Autowired
  private PlatformTransactionManager transactionManager;

    private static final String BITSILA_ORDER_ID = "15860-1004-3091";

    private static final String FULL_BITSILA_ORDER_JSON = """
                {
                  "customer": {
                    "name": "Prajwal Singh",
                    "phone_number": "7041748889",
                    "country_code": "91",
                    "email": "prajwalsingh00017@gmail.com",
                    "gender": "",
                    "age": 0,
                    "address": {
                      "address_1": "Prajwal Singh,3rd Cross Road, Kudlu,30, 3rd floor",
                      "address_2": "",
                      "locality": null,
                      "landmark": "Prajwal Singh",
                      "city": "Bengaluru",
                      "state": "Karnataka",
                      "country": "IND",
                      "pincode": "560068",
                      "instructions": "Prajwal Singh, 3rd Cross Road, Kudlu, 30, 3rd floor, Prajwal Singh, Bengaluru, 560068",
                      "latitude": 12.883994,
                      "longitude": 77.650072
                    }
                  },
                  "order": {
                    "outlet_name": "FreshMenu_HSR",
                    "outlet_ref_id": "22",
                    "order_no": "15860-1004-3091",
                    "order_ref_no": "",
                    "ordered_on": 1759815785,
                    "delivery_on": null,
                    "order_type": "ondc",
                    "fulfilment_type": "delivery",
                    "logistics_type": "third_party",
                    "item_level_charges": 0,
                    "item_level_taxes": 0,
                    "order_level_charges": 0,
                    "order_level_taxes": 8.09,
                    "order_offer_amount": 0,
                    "item_offer_amount": 0,
                    "order_offer_ref_id": null,
                    "extra_info": {
                      "no_of_persons": 0,
                      "table_no": "0",
                      "flash_order": true
                    },
                    "sub_total": 161.85,
                    "total_charges": 77.1,
                    "total_offer_amount": 0,
                    "total_taxes": 8.09,
                    "total_amount": 247.04,
                    "charges_breakup": [
                      {
                        "delivery_charges": 77.1,
                        "packaging_charges": 0
                      }
                    ],
                    "charges_breakup_v2": [
                      {
                        "ref_id": "",
                        "name": "delivery_charges",
                        "amount": 77.1,
                        "tax_inclusive": false,
                        "tax": 0,
                        "delivery_type": "delivery"
                      },
                      {
                        "ref_id": "",
                        "name": "packaging_charges",
                        "amount": 0,
                        "tax_inclusive": false,
                        "tax": 0,
                        "tax_amount": 0
                      }
                    ],
                    "prep_time": 20,
                    "notes": "",
                    "invoice_url": "https://biz.enstore.com/m/orders/XsU9586731EWP/print?_tkn_=ee006851mpt"
                  },
                  "order_items": [
                    {
                      "ref_id": "12932",
                      "name": "Paneer Popeye Sandwich 1.0 pc",
                      "price": 78,
                      "quantity": 1,
                      "offer_amount": 0,
                      "mrp": 120,
                      "total_discount_amount": 42,
                      "sub_total": 0,
                      "charges": 0,
                      "tax": 5,
                      "total_amount": 78,
                      "notes": "",
                      "item_type": "veg",
                      "tax_inclusive": null,
                      "tax_amount": 3.9,
                      "item_nature": "services",
                      "charges_breakup": [],
                      "charges_breakup_v2": [
                        {
                          "ref_id": "",
                          "name": "delivery_charges",
                          "amount": 0,
                          "tax_inclusive": false,
                          "tax": 0,
                          "delivery_type": "delivery"
                        },
                        {
                          "ref_id": "pc_6",
                          "name": "packaging_charges",
                          "amount": 10,
                          "tax_inclusive": true,
                          "tax": 0,
                          "tax_amount": 0
                        }
                      ],
                      "tax_breakup": [],
                      "variation_name": "",
                      "variation_id": "",
                      "customization": [],
                      "customizations": []
                    }
                  ],
                  "offers": [],
                  "payment": {
                    "amount_paid": 247.04,
                    "amount_balance": 0,
                    "mode": "aggregator",
                    "status": "success"
                  },
                  "network_order_id": "2025-10-07-000443",
                  "network_transaction_id": "44104332-dcc1-46f1-be5f-9ff35bbea6fa",
                  "buyer_app_name": "prod.nirmitbap.ondc.org"
                }
                """;

    @BeforeMethod
    public void setup() {
        cleanupTestData();
    }

    @AfterMethod
    public void teardown() {
        cleanupTestData();
    }

  private void cleanupTestData() {
    TransactionTemplate tx = new TransactionTemplate(transactionManager);
    tx.execute(status -> {
      orderInfoRepository.findByUserLastNameAndChannel(BITSILA_ORDER_ID, Channel.BITSILA_ONDC).ifPresent(orderInfo -> {
        paymentEntryRepository.deleteByOrderId(orderInfo.getId());
        orderItemRepository.deleteByOrderId(orderInfo.getId());
        orderAdditionalDetailsRepository.deleteByOrderId(orderInfo.getId());
        orderInfoRepository.deleteById(orderInfo.getId());
      });
      return null;
    });
  }

    @Test(groups = {"api", "integration"}, priority = 1,description = "Test successful creation of a Bitsila ONDC order.")
    public void testCreateBitsilaOrder_Success() throws Exception {
        String orderRequestJson = """
                {
                  "customer": {
                    "name": "Prajwal Singh",
                    "phone_number": "7041748889",
                    "country_code": "91",
                    "email": "prajwalsingh00017@gmail.com",
                    "gender": "",
                    "age": 0,
                    "address": {
                      "address_1": "Prajwal Singh,3rd Cross Road, Kudlu,30, 3rd floor",
                      "address_2": "",
                      "locality": null,
                      "landmark": "Prajwal Singh",
                      "city": "Bengaluru",
                      "state": "Karnataka",
                      "country": "IND",
                      "pincode": "560068",
                      "instructions": "Prajwal Singh, 3rd Cross Road, Kudlu, 30, 3rd floor, Prajwal Singh, Bengaluru, 560068",
                      "latitude": 12.883994,
                      "longitude": 77.650072
                    }
                  },
                  "order": {
                    "outlet_name": "FreshMenu_HSR",
                    "outlet_ref_id": "22",
                    "order_no": "15860-1004-3091",
                    "order_ref_no": "",
                    "ordered_on": 1759815785,
                    "delivery_on": null,
                    "order_type": "ondc",
                    "fulfilment_type": "delivery",
                    "logistics_type": "third_party",
                    "item_level_charges": 0,
                    "item_level_taxes": 0,
                    "order_level_charges": 0,
                    "order_level_taxes": 8.09,
                    "order_offer_amount": 0,
                    "item_offer_amount": 0,
                    "order_offer_ref_id": null,
                    "extra_info": {
                      "no_of_persons": 0,
                      "table_no": "0",
                      "flash_order": true
                    },
                    "sub_total": 161.85,
                    "total_charges": 77.1,
                    "total_offer_amount": 0,
                    "total_taxes": 8.09,
                    "total_amount": 247.04,
                    "charges_breakup": [
                      {
                        "delivery_charges": 77.1,
                        "packaging_charges": 0
                      }
                    ],
                    "charges_breakup_v2": [
                      {
                        "ref_id": "",
                        "name": "delivery_charges",
                        "amount": 77.1,
                        "tax_inclusive": false,
                        "tax": 0,
                        "delivery_type": "delivery"
                      },
                      {
                        "ref_id": "",
                        "name": "packaging_charges",
                        "amount": 0,
                        "tax_inclusive": false,
                        "tax": 0,
                        "tax_amount": 0
                      }
                    ],
                    "prep_time": 20,
                    "notes": "",
                    "invoice_url": "https://biz.enstore.com/m/orders/XsU9586731EWP/print?_tkn_=ee006851mpt"
                  },
                  "order_items": [
                    {
                      "ref_id": "12932",
                      "name": "Paneer Popeye Sandwich 1.0 pc",
                      "price": 78,
                      "quantity": 1,
                      "offer_amount": 0,
                      "mrp": 120,
                      "total_discount_amount": 42,
                      "sub_total": 0,
                      "charges": 0,
                      "tax": 5,
                      "total_amount": 78,
                      "notes": "",
                      "item_type": "veg",
                      "tax_inclusive": null,
                      "tax_amount": 3.9,
                      "item_nature": "services",
                      "charges_breakup": [],
                      "charges_breakup_v2": [
                        {
                          "ref_id": "",
                          "name": "delivery_charges",
                          "amount": 0,
                          "tax_inclusive": false,
                          "tax": 0,
                          "delivery_type": "delivery"
                        },
                        {
                          "ref_id": "pc_6",
                          "name": "packaging_charges",
                          "amount": 10,
                          "tax_inclusive": true,
                          "tax": 0,
                          "tax_amount": 0
                        }
                      ],
                      "tax_breakup": [],
                      "variation_name": "",
                      "variation_id": "",
                      "customization": [],
                      "customizations": []
                    },
                    {
                      "ref_id": "12259",
                      "name": "Cheese Chutney Whole Wheat Sandwich 1.0 pc",
                      "price": 83.85,
                      "quantity": 1,
                      "offer_amount": 0,
                      "mrp": 129,
                      "total_discount_amount": 45.15,
                      "sub_total": 0,
                      "charges": 0,
                      "tax": 5,
                      "total_amount": 83.85,
                      "notes": "",
                      "item_type": "veg",
                      "tax_inclusive": null,
                      "tax_amount": 4.19,
                      "item_nature": "services",
                      "charges_breakup": [],
                      "charges_breakup_v2": [
                        {
                          "ref_id": "",
                          "name": "delivery_charges",
                          "amount": 0,
                          "tax_inclusive": false,
                          "tax": 0,
                          "delivery_type": "delivery"
                        },
                        {
                          "ref_id": "pc_9",
                          "name": "packaging_charges",
                          "amount": 5,
                          "tax_inclusive": true,
                          "tax": 0,
                          "tax_amount": 0
                        }
                      ],
                      "tax_breakup": [],
                      "variation_name": "",
                      "variation_id": "",
                      "customization": [],
                      "customizations": []
                    }
                  ],
                  "offers": [],
                  "payment": {
                    "amount_paid": 247.04,
                    "amount_balance": 0,
                    "mode": "aggregator",
                    "status": "success"
                  },
                  "network_order_id": "2025-10-07-000443",
                  "network_transaction_id": "44104332-dcc1-46f1-be5f-9ff35bbea6fa",
                  "buyer_app_name": "prod.nirmitbap.ondc.org"
                }
                """;

        mockMvc.perform(post("/api/bitsilaondc/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.order_id").exists());
    }

    @Test(groups = {"api", "smoke"}, priority = 2,description = "Test duplicate order creation attempt.")
    public void testCreateBitsilaOrder_DuplicateOrder() throws Exception {

  mockMvc.perform(post("/api/bitsilaondc/order/create")
      .contentType(MediaType.APPLICATION_JSON)
      .content(FULL_BITSILA_ORDER_JSON))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.status").value("200"));

  // Attempt to create the same order again should return "Order already exists"
  mockMvc.perform(post("/api/bitsilaondc/order/create")
      .contentType(MediaType.APPLICATION_JSON)
      .content(FULL_BITSILA_ORDER_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.message").value("Order already exists"));
    }

    @Test(groups = {"api", "validation"}, priority = 3,description = "Test order creation with a missing order number.")
    public void testCreateBitsilaOrder_MissingOrderNo() throws Exception {
        String orderRequestJson = "{\"order\":{}, \"order_items\":[{\"quantity\":1}]}";

        mockMvc.perform(post("/api/bitsilaondc/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequestJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value("422"))
                .andExpect(jsonPath("$.message").value("No order id found"));
    }

    @Test(groups = {"api", "validation"}, priority = 4,description = "Test order creation with no order items.")
    public void testCreateBitsilaOrder_NoItems() throws Exception {
        String orderRequestJson = "{\"order\":{\"order_no\":\"12345\"}, \"order_items\":[]}";

        mockMvc.perform(post("/api/bitsilaondc/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequestJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value("422"))
                .andExpect(jsonPath("$.message").value("No items found"));
    }

    @Test(groups = {"api", "edge"}, priority = 5,description = "Test order creation with invalid item quantity.")
    public void testCreateBitsilaOrder_InvalidQuantity() throws Exception {
        String orderRequestJson = "{\"order\":{\"order_no\":\"12345\"}, \"order_items\":[{\"quantity\":0}]}";

        mockMvc.perform(post("/api/bitsilaondc/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequestJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value("422"))
                .andExpect(jsonPath("$.message").value("Invalid quantity found"));
    }
}
