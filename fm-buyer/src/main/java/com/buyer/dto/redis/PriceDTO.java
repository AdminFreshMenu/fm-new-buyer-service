package com.buyer.dto.redis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceDTO {

    private Integer sellingPrice;
    private Integer mrpPrice;

    private Integer entityId;
    private String filterKey;

    private String data;

    private Integer delta;
    private String filterValue;
    private String channelName;
    private Integer kitchenId;
    private String city;
    private Long startAt;
    private Long endAt;
    private Integer brandId;
    private Integer segmentId;
}
