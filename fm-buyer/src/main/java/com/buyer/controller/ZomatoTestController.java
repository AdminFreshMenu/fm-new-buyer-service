package com.buyer.controller;

import com.buyer.service.ZomatoOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/zomato/test")
public class ZomatoTestController {

    private static final Logger logger = LoggerFactory.getLogger(ZomatoTestController.class);

    @Autowired
    private ZomatoOrderService zomatoOrderService;

    @PostMapping("/create_order")
    public ResponseEntity<Map<String, Object>> testCreateOrder() {
        logger.info("Zomato test create_order API called");
        
        String testOrderJson = """
            {
              "order": {
                "order_id": 72811753,
                "restaurant_id": 20706590,
                "restaurant_name": "Bowl Soul",
                "outlet_id": "1",
                "order_date_time": 1758104019,
                "enable_delivery": 0,
                "order_type": "DELIVERY",
                "order_instructions": "Send cutlery",
                "customer_details": {
                  "name": "Sai Pavan A",
                  "order_instructions": "Send cutlery",
                  "customer_id": "b3146e782730c0b296695964555bdf85240f16ee104e832a322d7202cd8099c7",
                  "phone_number": "08046806752",
                  "email": "8046806752@example.com",
                  "pincode": "",
                  "delivery_area": "Nanja Reddy Colony, Murugesh Palyla, Bangalore",
                  "delivery_coordinates_type": "user",
                  "city": "Bengaluru",
                  "state": "",
                  "country": "India",
                  "address_type": "Home"
                },
                "pickup_time": 20,
                "net_amount": 348,
                "gross_amount": 183,
                "order_discounts": [],
                "is_special_combo_order": 0,
                "dishes": [
                  {
                    "dish_type": "variant",
                    "dish_id": "14758",
                    "composition": {
                      "variant_id": "14758",
                      "catalogue_id": "14758",
                      "catalogue_name": "Middle Eastern Chicken Shawarma Bowl",
                      "catalogue_description": "Transport your taste buds to the streets of the Middle East with our Middle Eastern Chicken Shawarma Bowl, boasting tender shawarma chicken, fragrant rice, and hearty chickpeas for an authentic culinary adventure",
                      "properties": [],
                      "modifier_groups": [
                        {
                          "group_id": "14758_0",
                          "group_name": "AddOns",
                          "variants": [
                            {
                              "variant_id": "16696",
                              "catalogue_id": "16696",
                              "catalogue_name": "Double Egg Omelette",
                              "catalogue_description": "Spiced double-egg omelette packed with onion, tomato, green chilli and coriander. power up with our protein bestseller! \\nEnegry-174 Kcal, Fat-14.04 gms, Carbs-2.15 gms, Fiber-0.5 gms, Protein-9.67 gms, Sugar-1.62 gms",
                              "unit_cost": 49,
                              "quantity": 1,
                              "total_cost": 49,
                              "max_allowed_quantity": 1
                            }
                          ]
                        }
                      ],
                      "instructions": [],
                      "unit_cost": 348,
                      "sub_category_id": "487",
                      "sub_category_name": "Continental Bowls",
                      "category_id": "487",
                      "category_name": "Continental Bowls"
                    },
                    "quantity": 1,
                    "total_cost": 348,
                    "total_discount": 0,
                    "final_cost": 348,
                    "taxes": [
                      {
                        "type": "SOURCE_TAX",
                        "slug": "SGST_D_P_2.50",
                        "name": "SGST",
                        "amount": 4.2
                      },
                      {
                        "type": "SOURCE_TAX",
                        "slug": "CGST_D_P_2.50",
                        "name": "CGST",
                        "amount": 4.2
                      }
                    ],
                    "charges": [
                      {
                        "charge_id": "3",
                        "name": "Restaurant Packaging Charges",
                        "type": "FIXED",
                        "value": 15,
                        "amount": 15,
                        "taxes": [
                          {
                            "type": "SOURCE_TAX",
                            "slug": "SGST_D_P_2.50",
                            "name": "SGST",
                            "amount": 0.38
                          },
                          {
                            "type": "SOURCE_TAX",
                            "slug": "CGST_D_P_2.50",
                            "name": "CGST",
                            "amount": 0.38
                          }
                        ]
                      }
                    ],
                    "dish_discounts": [
                      {
                        "type": "SALT",
                        "amount": 180,
                        "offer_id": ""
                      }
                    ]
                  }
                ],
                "is_rbt_enabled": false,
                "merchant_bag_flow": false,
                "is_bulk_order": false,
                "vendor_total": 192.15,
                "total_merchant": 183,
                "payment_mode": "ONLINE",
                "payment_status": "PAID",
                "amount_balance": 0,
                "amount_paid": 192.15,
                "cash_to_be_collected": 0,
                "order_status": "placed",
                "otp": "2883",
                "otp_message": "Please share the OTP with rider to pick up the order"
              }
            }
            """;

        try {
            ResponseEntity<Map<String, Object>> response = zomatoOrderService.createOrder(testOrderJson);
            logger.info("Test order creation completed successfully");
            return response;
        } catch (Exception e) {
            logger.error("Error in test order creation", e);
            throw e;
        }
    }

    @PostMapping("/create_order_without_addon")
    public ResponseEntity<Map<String, Object>> testCreateOrderWithoutAddon() {
        logger.info("Zomato test create_order without addon API called");
        
        String testOrderJson = """
            {
              "order": {
                "order_id": 123321121,
                "restaurant_id": 9999,
                "restaurant_name": "FreshMenu",
                "outlet_id": "1",
                "order_date_time": 1711006455,
                "enable_delivery": 0,
                "order_type": "DELIVERY",
                "order_instructions": "Don't send cutlery, tissues and straws",
                "customer_details": {
                  "name": "Ssss",
                  "order_instructions": "Don't send cutlery, tissues and straws",
                  "customer_id": "10315",
                  "phone_number": "1234567890",
                  "email": "1234567890@example.com",
                  "pincode": "",
                  "delivery_area": "Manjunatha Layout, Marathahalli, Bangalore",
                  "delivery_coordinates_type": "user",
                  "city": "Bengaluru",
                  "state": "",
                  "country": "India",
                  "address_type": "Home"
                },
                "pickup_time": 21,
                "net_amount": 638,
                "gross_amount": 290,
                "order_discounts": [],
                "is_special_combo_order": 0,
                "dishes": [
                  {
                    "dish_type": "variant",
                    "dish_id": "10193",
                    "composition": {
                      "variant_id": "10193",
                      "catalogue_id": "10193",
                      "catalogue_name": "Soboro Don",
                      "catalogue_description": "Minced chicken cooked in a piquant broth of chicken stock, Shaoxing wine, Hoisin sauce and Sichuan peppercorns is served with noodles and sir-fried with veggies – street-food, Sichuan style! A very popular Asian meal bowl on our menu. All our bowls are prepared fresh on your order.\\nEnergy 653 Kcal, Carbs 93 gms, Protein 35 gms, Fiber 4 gms, Fat 9 gms",
                      "properties": [],
                      "modifier_groups": [],
                      "instructions": [],
                      "unit_cost": 149,
                      "sub_category_id": "401",
                      "sub_category_name": "Noodles",
                      "category_id": "357",
                      "category_name": "Bowls"
                    },
                    "quantity": 1,
                    "total_cost": 149,
                    "total_discount": 0,
                    "final_cost": 149,
                    "taxes": [
                      {
                        "type": "SOURCE_TAX",
                        "slug": "SGST_D_P_2.50",
                        "name": "SGST",
                        "amount": 3.23
                      },
                      {
                        "type": "SOURCE_TAX",
                        "slug": "CGST_D_P_2.50",
                        "name": "CGST",
                        "amount": 3.23
                      }
                    ],
                    "charges": [
                      {
                        "charge_id": "3",
                        "name": "Restaurant Packaging Charges",
                        "type": "FIXED",
                        "value": 16,
                        "amount": 16,
                        "taxes": [
                          {
                            "type": "SOURCE_TAX",
                            "slug": "CGST_D_P_2.50",
                            "name": "CGST",
                            "amount": 0.4
                          },
                          {
                            "type": "SOURCE_TAX",
                            "slug": "SGST_D_P_2.50",
                            "name": "SGST",
                            "amount": 0.4
                          }
                        ]
                      }
                    ],
                    "dish_discounts": [
                      {
                        "type": "SALT",
                        "amount": 170,
                        "offer_id": ""
                      }
                    ]
                  }
                ],
                "is_rbt_enabled": true,
                "merchant_bag_flow": false,
                "is_bulk_order": false,
                "vendor_total": 304.5,
                "total_merchant": 290,
                "payment_mode": "ONLINE",
                "payment_status": "PAID",
                "amount_balance": 0,
                "amount_paid": 304.5,
                "cash_to_be_collected": 0,
                "order_status": "placed",
                "otp": "9248",
                "otp_message": "Please share the OTP with rider to pick up the order"
              }
            }
            """;

        try {
            ResponseEntity<Map<String, Object>> response = zomatoOrderService.createOrder(testOrderJson);
            logger.info("Test order creation (without addon) completed successfully");
            return response;
        } catch (Exception e) {
            logger.error("Error in test order creation (without addon)", e);
            throw e;
        }
    }
}