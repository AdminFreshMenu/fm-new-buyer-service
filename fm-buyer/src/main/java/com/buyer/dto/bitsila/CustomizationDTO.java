package com.buyer.dto.bitsila;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomizationDTO implements Serializable {

    @JsonProperty("ref_id")
    private String refId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("customization_group_name")
    private String customizationGroupName;

    @JsonProperty("customization_group_ref_id")
    private String customizationGroupRefId;

    // Getters and Setters


    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCustomizationGroupName() {
        return customizationGroupName;
    }

    public void setCustomizationGroupName(String customizationGroupName) {
        this.customizationGroupName = customizationGroupName;
    }

    public String getCustomizationGroupRefId() {
        return customizationGroupRefId;
    }

    public void setCustomizationGroupRefId(String customizationGroupRefId) {
        this.customizationGroupRefId = customizationGroupRefId;
    }
}