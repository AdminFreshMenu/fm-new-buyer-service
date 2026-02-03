package com.buyer.entity.MongoDB.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "orders")
@EqualsAndHashCode(exclude = "orders")
public class ExperimentData {

    private String experimentId;
    private String variant;
}
