package com.buyer.entity.MongoDB.dto;

import lombok.Data;

@Data

public class ShopifyDiscountCode {
    private String code;
    private Number amount;
    private String type;

}
