package com.buyer.dto.magicpin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Charge {

    private static final long serialVersionUID = 1L;

    @JsonProperty("name")
    private String name;
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("tax_liability")
    private String taxLiability;
    @JsonProperty("taxes")
    private List<Tax> taxes;

    public Charge() {
    }

    public Charge(String name, Double amount, String taxLiability, List<Tax> taxes) {
        this.name = name;
        this.amount = amount;
        this.taxLiability = taxLiability;
        this.taxes = taxes;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTaxLiability() {
        return taxLiability;
    }

    public void setTaxLiability(String taxLiability) {
        this.taxLiability = taxLiability;
    }

    public List<Tax> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<Tax> taxes) {
        this.taxes = taxes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Charge{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                ", taxLiability='" + taxLiability + '\'' +
                ", taxes=" + taxes +
                '}';
    }
}
