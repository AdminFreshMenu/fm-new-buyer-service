package com.buyer.dto;

import com.buyer.entity.OrderEnum.OrderAdditionalData;

public class OrderAdditionalDetailsDto {
    
    private Long orderId;
    private OrderAdditionalData orderKey;
    private String orderKeyValue;
    
    public OrderAdditionalDetailsDto() {
    }
    
    public OrderAdditionalDetailsDto(Long orderId, OrderAdditionalData orderKey, String orderKeyValue) {
        this.orderId = orderId;
        this.orderKey = orderKey;
        this.orderKeyValue = orderKeyValue;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public OrderAdditionalData getOrderKey() {
        return orderKey;
    }
    
    public void setOrderKey(OrderAdditionalData orderKey) {
        this.orderKey = orderKey;
    }
    
    public String getOrderKeyValue() {
        return orderKeyValue;
    }
    
    public void setOrderKeyValue(String orderKeyValue) {
        this.orderKeyValue = orderKeyValue;
    }
}