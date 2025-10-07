package com.buyer.controller;

import com.buyer.dto.bitsila.OrderRequest;
import com.buyer.dto.bitsila.Response;
import com.buyer.service.BitsilaServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bitsilaondc")
public class BitsilaController {

    @Autowired
    private BitsilaServices bitsilaService;

    @RequestMapping(value = "/order/create", method = RequestMethod.POST)
    public ResponseEntity<Response> orderCreate(@RequestHeader(value = "Auth-Key", required = false) String authKey, @RequestBody OrderRequest orderRequest) {
        return bitsilaService.createOrder(authKey, orderRequest);
    }


}
