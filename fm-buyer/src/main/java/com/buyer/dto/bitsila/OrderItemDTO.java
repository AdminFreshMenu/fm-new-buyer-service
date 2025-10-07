package com.buyer.dto.bitsila;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class OrderItemDTO implements Serializable {

    @JsonProperty("ref_id")
    private String refId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("offer_amount")
    private Integer offerAmount;

    @JsonProperty("sub_total")
    private Integer subTotal;


    @JsonProperty("total_discount_amount")
    private Double totalDiscountAmount;

    @JsonProperty("charges")
    private Integer charges;

    @JsonProperty("tax")
    private Integer tax;

    @JsonProperty("total_amount")
    private Integer totalAmount;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("charges_breakup_v2")
    private List<ChargesBreakupDTO> chargesBreakup;

    @JsonProperty("tax_breakup")
    private List<TaxBreakupDTO> taxBreakup;

    @JsonProperty("offer_ref_id")
    private String offerRefId;

    @JsonProperty("variant_ref_id")
    private String variantRefId;

    @JsonProperty("variant_name")
    private String variantName;

    @JsonProperty("customizations")
    private List<CustomizationDTO> customizations;

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

    public Integer getOfferAmount() {
        return offerAmount;
    }

    public void setOfferAmount(Integer offerAmount) {
        this.offerAmount = offerAmount;
    }

    public Integer getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Integer subTotal) {
        this.subTotal = subTotal;
    }

    public Integer getCharges() {
        return charges;
    }

    public void setCharges(Integer charges) {
        this.charges = charges;
    }

    public Integer getTax() {
        return tax;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<ChargesBreakupDTO> getChargesBreakup() {
        return chargesBreakup;
    }

    public void setChargesBreakup(List<ChargesBreakupDTO> chargesBreakup) {
        this.chargesBreakup = chargesBreakup;
    }

    public List<TaxBreakupDTO> getTaxBreakup() {
        return taxBreakup;
    }

    public void setTaxBreakup(List<TaxBreakupDTO> taxBreakup) {
        this.taxBreakup = taxBreakup;
    }

    public String getOfferRefId() {
        return offerRefId;
    }

    public void setOfferRefId(String offerRefId) {
        this.offerRefId = offerRefId;
    }

    public String getVariantRefId() {
        return variantRefId;
    }

    public void setVariantRefId(String variantRefId) {
        this.variantRefId = variantRefId;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public List<CustomizationDTO> getCustomizations() {
        return customizations;
    }

    public void setCustomizations(List<CustomizationDTO> customizations) {
        this.customizations = customizations;
    }

    public Double getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(Double totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }
}
