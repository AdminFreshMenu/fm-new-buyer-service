package com.buyer.dto.redis;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProductDTORedis {
    private Long id;
    private String title;
    private String subTitle;
    private String details;

    private Integer likesCount;

    private String metaTitle;
    private String metaDescription;

    private String skuId;
    private String slug;

    private String mealType;
    private String kitchenLine;

    private CuisineDTO cuisine;

    private List<MediaWrapperDTO> medias;
    private List<CategoryDTO> categories;

    private Map<String, String> additionalProperties;

    private String  createdAt;
    private String  updatedAt;

    private String shareUrl;

    private Boolean isCustomizationAvailable;
    private List<String> foodTags;
    private Boolean showToNewUser;

    private NutritionDTO nutrition;

    private Integer preparationTime;

    private Boolean isPrime;
    private Boolean isExpressDeliveryEnabled;
    private Boolean showInUpsale;
    private Boolean flashSaleEnabled;
    private Boolean freeShipping;
    private Boolean swiggyRecommendationEnabled;

    private String thirdPartyDescription;

    private List<SkuDTO> skuDTOList;
    private List<PriceDTO> price;

    private Boolean corpDisc;
    private Boolean exclPrime;
    private Boolean il;

    //added fields
    private String appTitle;
    private String internalName;
    private String webDescription;

}