package com.buyer.dto.bitsila;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDTO implements Serializable {

    @JsonProperty("amount_balance")
    private Integer amountBalance;

    @JsonProperty("amount_paid")
    private Integer amountPaid;

    @JsonProperty("mode")
    private String mode;

    @JsonProperty("status")
    private String status;

    public Integer getAmountBalance() {
        return amountBalance;
    }

    public void setAmountBalance(Integer amountBalance) {
        this.amountBalance = amountBalance;
    }

    public Integer getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Integer amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}