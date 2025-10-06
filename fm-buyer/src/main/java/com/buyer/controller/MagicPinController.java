package com.buyer.controller;


import com.buyer.dto.magicpin.CreateOrderResponse;
import com.buyer.dto.magicpin.MagicpinOrderRequest;
import com.buyer.dto.magicpin.Status;
import com.buyer.service.MagicpinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/magicpin")
public class MagicPinController {

    private static final Logger logger = LoggerFactory.getLogger(MagicPinController.class);

    @Autowired
    private MagicpinService magicpinService;

    @RequestMapping(value = "/order/create", method = RequestMethod.POST)
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestHeader(value = "Auth-Key", required = false) String authKey,
                                                           @RequestHeader(value = "fm_cl", required = false) String channel,
                                                           @RequestHeader(value = "brand_id", required = false, defaultValue = "1") Integer brandId,
                                                           @RequestBody MagicpinOrderRequest orderJson) {
        logger.info("MagicPin createOrder API called at: {}", new java.util.Date());
        logger.debug("MagicPin createOrder request received: {}", orderJson);
        
        try {
            return magicpinService.createOrder(orderJson, brandId);
        } catch (Exception e) {
            logger.error("Exception in MagicPin create order", e);
            CreateOrderResponse errorResponse = new CreateOrderResponse(
                    Status.Failed, "Internal server error: " + e.getMessage(), 500, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
