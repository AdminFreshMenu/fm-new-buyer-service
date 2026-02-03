package com.buyer.dto.redis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NutritionDTO {

    private String n;
    private Integer q;

    private List<NutritionItemDTO> nul;
}
