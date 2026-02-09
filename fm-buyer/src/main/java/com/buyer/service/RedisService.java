package com.buyer.service;

import com.buyer.dto.redis.ProductDTORedis;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public ProductDTORedis getActiveProductJson(long productId) throws JsonProcessingException {
        try {
            String json = (String) redisTemplate.opsForValue().get("ACTIVE_PRODUCT_" + productId);

        if (json == null) {
            return null;
        }

        // Remove the outer quotes and unescape
        if (json != null && json.startsWith("\"") && json.endsWith("\"")) {
            // Remove outer quotes
            json = json.substring(1, json.length() - 1);
            // Unescape the JSON
            json = json.replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        }

            ProductDTORedis product = objectMapper.readValue(json, ProductDTORedis.class);
            return product;
        } catch (Exception e) {
            logger.warn("Failed to get product from Redis, returning null. ProductId: {}, Error: {}", productId, e.getMessage());
            return null;
        }
    }
}
