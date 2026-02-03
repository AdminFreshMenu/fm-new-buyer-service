package com.buyer.dto.redis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkuDTO {

    private Integer id;
    private String name;
    private String detail;
    private String createdAt;
    private String updatedAt;
    private Double price;
    private String skuType;
    private Integer recordStatus;
}
