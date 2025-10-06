package com.buyer.dto.magicpin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tax {

    private static final long serialVersionUID = 1L;

    @JsonProperty("name")
    private String name;
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("rate")
    private Double rate;

    public Tax() {
    }

    public Tax(String name, Double amount, Double rate) {
        this.name = name;
        this.amount = amount;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Tax{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                ", rate=" + rate +
                '}';
    }
}
