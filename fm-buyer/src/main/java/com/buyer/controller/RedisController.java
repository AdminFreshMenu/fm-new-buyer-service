package com.buyer.controller;

import com.buyer.dto.redis.ProductDTORedis;
import com.buyer.service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {


    @Autowired
    private RedisService redisService;

    @GetMapping("/active-products/{productId}")
    public ProductDTORedis getActiveProducts(@PathVariable Long productId) throws JsonProcessingException {
        return redisService.getActiveProductJson(productId);
    }
}
