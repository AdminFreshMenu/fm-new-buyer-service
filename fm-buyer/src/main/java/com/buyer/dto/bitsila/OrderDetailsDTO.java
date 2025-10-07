package com.buyer.dto.bitsila;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailsDTO implements Serializable {

    @JsonProperty("outlet_name")
    private String outletName;

    @JsonProperty("outlet_ref_id")
    private String outletRefId;

    @JsonProperty("order_no")
    private String orderNo;

    @JsonProperty("order_ref_no")
    private String orderRefNo;

    @JsonProperty("ordered_on")
    private Long orderedOn;

    @JsonProperty("delivery_on")
    private Long deliveryOn;

    @JsonProperty("order_type")
    private String orderType;

    @JsonProperty("fulfilment_type")
    private String fulfilmentType;

    @JsonProperty("logistics_type")
    private String logisticsType;

    @JsonProperty("item_level_charges")
    private Integer itemLevelCharges;

    @JsonProperty("item_level_taxes")
    private Integer itemLevelTaxes;

    @JsonProperty("item_offer_amount")
    private Integer itemOfferAmount;

    @JsonProperty("order_level_charges")
    private Integer orderLevelCharges;

    @JsonProperty("order_level_taxes")
    private Integer orderLevelTaxes;

    @JsonProperty("order_offer_amount")
    private Integer orderOfferAmount;

    @JsonProperty("order_offer_ref_id")
    private String orderOfferRefId;

    @JsonProperty("extra_info")
    private ExtraInfoDTO extraInfo;

    @JsonProperty("sub_total")
    private Integer subTotal;

    @JsonProperty("total_charges")
    private Integer totalCharges;

    @JsonProperty("total_offer_amount")
    private Integer totalOfferAmount;

    @JsonProperty("total_taxes")
    private Integer totalTaxes;

    @JsonProperty("total_amount")
    private Integer totalAmount;

    @JsonProperty("prep_time")
    private Integer prepTime;

    @JsonProperty("notes")
    private String notes;

    // Getters and Setters


    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getOutletRefId() {
        return outletRefId;
    }

    public void setOutletRefId(String outletRefId) {
        this.outletRefId = outletRefId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderRefNo() {
        return orderRefNo;
    }

    public void setOrderRefNo(String orderRefNo) {
        this.orderRefNo = orderRefNo;
    }

    public Long getOrderedOn() {
        return orderedOn;
    }

    public void setOrderedOn(Long orderedOn) {
        this.orderedOn = orderedOn;
    }

    public Long getDeliveryOn() {
        return deliveryOn;
    }

    public void setDeliveryOn(Long deliveryOn) {
        this.deliveryOn = deliveryOn;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getFulfilmentType() {
        return fulfilmentType;
    }

    public void setFulfilmentType(String fulfilmentType) {
        this.fulfilmentType = fulfilmentType;
    }

    public String getLogisticsType() {
        return logisticsType;
    }

    public void setLogisticsType(String logisticsType) {
        this.logisticsType = logisticsType;
    }

    public Integer getItemLevelCharges() {
        return itemLevelCharges;
    }

    public void setItemLevelCharges(Integer itemLevelCharges) {
        this.itemLevelCharges = itemLevelCharges;
    }

    public Integer getItemLevelTaxes() {
        return itemLevelTaxes;
    }

    public void setItemLevelTaxes(Integer itemLevelTaxes) {
        this.itemLevelTaxes = itemLevelTaxes;
    }

    public Integer getItemOfferAmount() {
        return itemOfferAmount;
    }

    public void setItemOfferAmount(Integer itemOfferAmount) {
        this.itemOfferAmount = itemOfferAmount;
    }

    public Integer getOrderLevelCharges() {
        return orderLevelCharges;
    }

    public void setOrderLevelCharges(Integer orderLevelCharges) {
        this.orderLevelCharges = orderLevelCharges;
    }

    public Integer getOrderLevelTaxes() {
        return orderLevelTaxes;
    }

    public void setOrderLevelTaxes(Integer orderLevelTaxes) {
        this.orderLevelTaxes = orderLevelTaxes;
    }

    public Integer getOrderOfferAmount() {
        return orderOfferAmount;
    }

    public void setOrderOfferAmount(Integer orderOfferAmount) {
        this.orderOfferAmount = orderOfferAmount;
    }

    public String getOrderOfferRefId() {
        return orderOfferRefId;
    }

    public void setOrderOfferRefId(String orderOfferRefId) {
        this.orderOfferRefId = orderOfferRefId;
    }

    public ExtraInfoDTO getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(ExtraInfoDTO extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Integer getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Integer subTotal) {
        this.subTotal = subTotal;
    }

    public Integer getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(Integer totalCharges) {
        this.totalCharges = totalCharges;
    }

    public Integer getTotalOfferAmount() {
        return totalOfferAmount;
    }

    public void setTotalOfferAmount(Integer totalOfferAmount) {
        this.totalOfferAmount = totalOfferAmount;
    }

    public Integer getTotalTaxes() {
        return totalTaxes;
    }

    public void setTotalTaxes(Integer totalTaxes) {
        this.totalTaxes = totalTaxes;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(Integer prepTime) {
        this.prepTime = prepTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
