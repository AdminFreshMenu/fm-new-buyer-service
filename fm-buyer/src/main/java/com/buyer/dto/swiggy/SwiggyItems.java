package com.buyer.dto.swiggy;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Swiggy Order Item DTO
 * Represents individual items in a Swiggy order
 */
public class SwiggyItems implements Serializable  {

    private final static long serialVersionUID = 1L;

    @JsonProperty("sgst")
    private String sgst;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("cgst_percent")
    private String cgstPercent;

    @JsonProperty("reward_type")
    private String rewardType;

    @JsonProperty("cgst")
    private String cgst;

    @JsonProperty("igst")
    private String igst;

    @JsonProperty("sgst_percent")
    private String sgstPercent;

    @JsonProperty("subtotal")
    private Float subtotal;

    @JsonProperty("price")
    private Float price;

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private String id;

    @JsonProperty("igst_percent")
    private String igstPercent;

    @JsonProperty("packing_charges")
    private Float packingCharges = 0.0f;

    private List<SwiggyAddon> addons;

    private List<Variant> variants;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSgst() {
        return sgst;
    }

    public void setSgst(String sgst) {
        this.sgst = sgst;
    }

    public String getIgst() {
        return igst;
    }

    public void setIgst(String igst) {
        this.igst = igst;
    }

    public String getSgstPercent() {
        return sgstPercent;
    }

    public void setSgstPercent(String sgstPercent) {
        this.sgstPercent = sgstPercent;
    }

    public Float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Float subtotal) {
        this.subtotal = subtotal;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIgstPercent() {
        return igstPercent;
    }

    public void setIgstPercent(String igstPercent) {
        this.igstPercent = igstPercent;
    }

    public Float getPackingCharges() {
        return packingCharges;
    }

    public void setPackingCharges(Float packingCharges) {
        this.packingCharges = packingCharges;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCgst() {
        return cgst;
    }

    public void setCgst(String cgst) {
        this.cgst = cgst;
    }

    public String getCgstPercent() {
        return cgstPercent;
    }

    public void setCgstPercent(String cgstPercent) {
        this.cgstPercent = cgstPercent;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public List<SwiggyAddon> getAddons() {
        return addons;
    }

    public void setAddons(List<SwiggyAddon> addons) {
        this.addons = addons;
    }

    public List<Variant> getVariants() {
        return variants;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }
}