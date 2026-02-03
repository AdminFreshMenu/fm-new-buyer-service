package com.buyer.entity.MongoDB.dto;

import com.buyer.dto.redis.CategoryDTO;
import com.buyer.dto.redis.CuisineDTO;
import com.buyer.dto.redis.MediaDTO;
import com.buyer.dto.redis.NutritionDTO;
import com.buyer.dto.redis.SkuDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString(exclude = "orders")
@EqualsAndHashCode(exclude = "orders")
public class ShopifyOrderLineItem {

    private Number price;
    private Integer quantity;
    private String name;
    private String title;
    private Long productId;

    @JsonIgnore
    private List<ShopifyOrderLineItem> addons;
    @JsonIgnore
    private List<ShopifyOrderLineItem> comboItems;
    private String skuId;
    private String mealType;
    private String kitchenLine;
    private String torqusSku;
    private CuisineDTO cuisine;
    private List<CategoryDTO> categories;
    private Map<String, String> additionalProperties;
    private Number mrp;
    private Integer addonIncPrice;

    private String internalName;

    private String webTitle;

    private String appTitle;

    private String subTitle;

    private String allergyInfo;

    private String metaTitle;

    private String badgeImage;

    private List<String> markers;

    private List<String> appMarkers;

    private NutritionDTO nutrition;

    private String slug;

    private List<Map<DimensionType, MediaDTO>> medias;

    private String shareUrl;

    private List<SkuDTO> skuDTOList;

    private String productRecipe;
}
