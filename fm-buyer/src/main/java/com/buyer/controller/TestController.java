package com.buyer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "fm-buyer Test Service");
        response.put("timestamp", new Date());
        return ResponseEntity.ok(response);
    }

}