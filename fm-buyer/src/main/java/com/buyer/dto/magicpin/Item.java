package com.buyer.dto.magicpin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Item {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;
    @JsonProperty("third_party_id")
    private String thirdPartyId;
    @JsonProperty("displayText")
    private String displayText;
    @JsonProperty("quantity")
    private Integer quantity;
    @JsonProperty("amount")
    private Integer amount;
    @JsonProperty("tax")
    private Integer tax;
    //    @JsonProperty("subItems")
//    private List<SubItem> subItems;
    @JsonProperty("primary_type")
    private PrimaryType primaryType;
    @JsonProperty("taxCharges")
    private List<Tax> taxCharges;
    @JsonProperty("charges")
    private List<Charge> charges;
    @JsonProperty("taxLiability")
    private String taxLiability;

    public Item() {
    }

    public Item(Long id, String thirdPartyId, String displayText, Integer quantity, Integer amount, Integer tax
            /*, List<SubItem> subItems*/, PrimaryType primaryType, List<Tax> taxCharges, List<Charge> charges,
                String taxLiability) {
        this.id = id;
        this.thirdPartyId = thirdPartyId;
        this.displayText = displayText;
        this.quantity = quantity;
        this.amount = amount;
        this.tax = tax;
//        this.subItems = subItems;
        this.primaryType = primaryType;
        this.taxCharges = taxCharges;
        this.charges = charges;
        this.taxLiability = taxLiability;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getTax() {
        return tax;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }

//    public List<SubItem> getSubItems() {
//        return subItems;
//    }

//    public void setSubItems(List<SubItem> subItems) {
//        this.subItems = subItems;
//    }

    public PrimaryType getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(PrimaryType primaryType) {
        this.primaryType = primaryType;
    }

    public List<Tax> getTaxCharges() {
        return taxCharges;
    }

    public void setTaxCharges(List<Tax> taxCharges) {
        this.taxCharges = taxCharges;
    }

    public List<Charge> getCharges() {
        return charges;
    }

    public void setCharges(List<Charge> charges) {
        this.charges = charges;
    }

    public String getTaxLiability() {
        return taxLiability;
    }

    public void setTaxLiability(String taxLiability) {
        this.taxLiability = taxLiability;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", thirdPartyId='" + thirdPartyId + '\'' +
                ", displayText='" + displayText + '\'' +
                ", quantity=" + quantity +
                ", amount=" + amount +
                ", tax=" + tax +
                ", primaryType=" + primaryType +
                ", taxCharges=" + taxCharges +
                ", charges=" + charges +
                ", taxLiability='" + taxLiability + '\'' +
                '}';
    }
}
