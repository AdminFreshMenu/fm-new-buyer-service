package com.buyer.dto.magicpin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AdditionalCharge {

    @JsonProperty("title")
    private String title;
    @JsonProperty("type")
    private String type;
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("taxes")
    private List<Tax> taxes;
    @JsonProperty("infoText")
    private String infoText;
    @JsonProperty("tex_liability")
    private String texLiability;

    public AdditionalCharge(String title, String type, Double amount, List<Tax> taxes, String infoText, String texLiability) {
        this.title = title;
        this.type = type;
        this.amount = amount;
        this.taxes = taxes;
        this.infoText = infoText;
        this.texLiability = texLiability;
    }

    public AdditionalCharge() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public List<Tax> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<Tax> taxes) {
        this.taxes = taxes;
    }

    public String getInfoText() {
        return infoText;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }

    public String getTexLiability() {
        return texLiability;
    }

    public void setTexLiability(String texLiability) {
        this.texLiability = texLiability;
    }

    @Override
    public String toString() {
        return "AdditionalCharge{" +
                "title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", taxes=" + taxes +
                ", infoText='" + infoText + '\'' +
                ", texLiability='" + texLiability + '\'' +
                '}';
    }
}
